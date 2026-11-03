#!/usr/bin/env python3
"""校验项目里所有第三方 import 在当前 Maven 解析出的 classpath 上是否真实存在，
并给出同名类的新包位置建议。

Spring Boot 4 大量重排了包名（web.server.ErrorPage -> web.error.ErrorPage、
autoconfigure.data.redis -> data.redis.autoconfigure 等），逐个撞编译错误很慢，
这里按真实依赖树一次性列全。

用法：
  mvn -B -q dependency:build-classpath -Dmdep.outputFile=/tmp/cp-all.txt
  python3 scripts/migration/check_imports.py . org.springframework
"""
import re
import sys
import zipfile
from pathlib import Path

root = Path(sys.argv[1] if len(sys.argv) > 1 else '.').resolve()
prefixes = tuple(sys.argv[2:]) or ('org.springframework',)

cp_file = root / 'target' / 'classpath-all.txt'
if not cp_file.exists():
    sys.exit('缺少 %s，请先跑：mvn -B dependency:build-classpath -Dmdep.outputFile=%s'
             % (cp_file, cp_file))

jars = [Path(p) for entry in cp_file.read_text().replace('\\n', '\n').split('\n')
        for p in entry.split(':') if p.strip()]
available = {}
for jar in jars:
    if not jar.is_file():
        continue
    try:
        with zipfile.ZipFile(jar) as zf:
            for name in zf.namelist():
                if name.endswith('.class') and '$' not in name.rsplit('/', 1)[-1]:
                    available.setdefault(name[:-len('.class')].replace('/', '.'), jar.name)
    except (zipfile.BadZipFile, IsADirectoryError):
        continue

missing = {}
for p in sorted(root.rglob('*.java')):
    if 'target' in p.parts:
        continue
    for m in re.finditer(r'^import\s+((?:%s)[\w.]+)\s*;' % '|'.join(map(re.escape, prefixes)),
                         p.read_text(encoding='utf-8'), re.M):
        fqcn = m.group(1)
        if fqcn.endswith('.*') or fqcn in available:
            continue
        missing.setdefault(fqcn, set()).add(str(p.relative_to(root)))

print('classpath 上 %d 个 jar，缺失引用 %d 个' % (len(jars), len(missing)))
for fqcn, files in sorted(missing.items()):
    simple = fqcn.rsplit('.', 1)[-1]
    hits = sorted(a for a in available if a.rsplit('.', 1)[-1] == simple)
    print('\nMISSING %s' % fqcn)
    print('  候选: %s' % (hits[:3] if hits else '无'))
    for f in sorted(files)[:2]:
        print('  用处: %s' % f)
