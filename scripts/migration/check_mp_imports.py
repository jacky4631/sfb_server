#!/usr/bin/env python3
"""核对项目 import 的 MyBatis-Plus 类在目标版本 jar 里是否真实存在。

MP 3.5.17 做了模块拆分，extension.service.* 迁到了 spring.service.*，
靠人工记忆改会漏，直接用 jar 清单比对。
"""
import re
import sys
import zipfile
from pathlib import Path

repo = Path.home() / '.m2/repository/com/baomidou'
jars = sorted(p for p in repo.rglob('*.jar')
              if '3.5.17' in p.name and 'sources' not in p.name and 'javadoc' not in p.name)

available = set()
for jar in jars:
    with zipfile.ZipFile(jar) as zf:
        for name in zf.namelist():
            if name.endswith('.class') and not name.endswith('.info'):
                available.add(name[:-len('.class')].replace('/', '.'))

root = Path(sys.argv[1] if len(sys.argv) > 1 else '.').resolve()
imports = set()
for p in root.rglob('*.java'):
    if 'target' in p.parts:
        continue
    for m in re.finditer(r'^import\s+(com\.baomidou\.[\w.]+)', p.read_text(encoding='utf-8'), re.M):
        imports.add(m.group(1))

missing = sorted(c for c in imports if not c.endswith('.*') and c not in available)
print('检查 %d 个 MP import，缺失 %d 个' % (len(imports), len(missing)))
for c in missing:
    simple = c.rsplit('.', 1)[-1]
    # 给出同名类的新位置建议
    hits = sorted(a for a in available if a.rsplit('.', 1)[-1] == simple)
    print('MISSING %-70s 候选: %s' % (c, hits if hits else '无'))
star = sorted(c for c in imports if c.endswith('.*'))
if star:
    print('通配 import:', star)
