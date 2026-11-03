#!/usr/bin/env python3
"""校验项目里所有 org.springframework.* 的 import 在目标版本 jar 中是否真实存在，
并给出同名类的新包位置建议。

Spring Boot 4 大量重排了包名（web.server.ErrorPage -> web.error.ErrorPage、
autoconfigure.data.redis -> data.redis.autoconfigure 等），逐个撞编译错误很慢，
这里一次性列全。
"""
import re
import sys
import zipfile
from pathlib import Path

M2 = Path.home() / '.m2/repository/org/springframework'

# 只核对 Spring Boot 自身的类；spring-* 主框架 jar 也一并纳入
jars = []
for pat in ('spring-boot*/**/*.jar', 'spring-core/**/*.jar', 'spring-web*/**/*.jar',
            'spring-context/**/*.jar', 'spring-beans/**/*.jar', 'spring-aop/**/*.jar',
            'spring-tx/**/*.jar', 'spring-jdbc/**/*.jar', 'spring-messaging/**/*.jar',
            'spring-websocket/**/*.jar', 'spring-aspects/**/*.jar', 'spring-expression/**/*.jar',
            'data/spring-data-redis/**/*.jar', 'security/spring-security-*/**/*.jar'):
    jars.extend(p for p in M2.glob(pat)
                if 'sources' not in p.name and 'javadoc' not in p.name)

versions = sys.argv[2:] or None
available = {}
for jar in jars:
    if versions and not any(v in str(jar) for v in versions):
        continue
    try:
        with zipfile.ZipFile(jar) as zf:
            for name in zf.namelist():
                if name.endswith('.class') and '$' not in name.rsplit('/', 1)[-1]:
                    available.setdefault(name[:-6].replace('/', '.'), jar.name)
    except zipfile.BadZipFile:
        continue

root = Path(sys.argv[1] if len(sys.argv) > 1 else '.').resolve()
missing = {}
for p in sorted(root.rglob('*.java')):
    if 'target' in p.parts:
        continue
    for m in re.finditer(r'^import\s+(org\.springframework\.[\w.]+)\s*;',
                         p.read_text(encoding='utf-8'), re.M):
        fqcn = m.group(1)
        if fqcn.endswith('.*'):
            continue
        if fqcn in available:
            continue
        missing.setdefault(fqcn, set()).add(str(p.relative_to(root)))

print('Spring 类引用缺失数:', len(missing))
for fqcn, files in sorted(missing.items()):
    simple = fqcn.rsplit('.', 1)[-1]
    hits = sorted(a for a in available if a.rsplit('.', 1)[-1] == simple
                  and a.startswith('org.springframework'))
    print('\nMISSING %s' % fqcn)
    print('  候选: %s' % (hits[:3] if hits else '无'))
    for f in sorted(files)[:3]:
        print('  用处: %s' % f)
