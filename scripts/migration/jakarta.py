#!/usr/bin/env python3
"""javax -> jakarta 批量迁移。

只处理已从 Java EE 迁到 Jakarta EE 的包；JDK 自带的
javax.crypto / javax.sql / javax.imageio / javax.naming / javax.net /
javax.annotation.processing 不动。
"""
import re
import sys
from pathlib import Path

ROOT = Path(sys.argv[1] if len(sys.argv) > 1 else '.').resolve()

# 前缀型替换：整个子树都改名
PREFIX = [
    ('javax.servlet', 'jakarta.servlet'),
    ('javax.validation', 'jakarta.validation'),
    ('javax.persistence', 'jakarta.persistence'),
    ('javax.ws.rs', 'jakarta.ws.rs'),
    ('javax.inject', 'jakarta.inject'),
    ('javax.interceptor', 'jakarta.interceptor'),
    ('javax.transaction', 'jakarta.transaction'),
    ('javax.jms', 'jakarta.jms'),
    ('javax.mail', 'jakarta.mail'),
]
# 精确型替换：javax.annotation 下只有 Java EE 那几个注解归 jakarta，
# javax.annotation.processing.* / javax.annotation.Nullable 属 JDK 或 Guava，不能误伤
EXACT = {
    'javax.annotation.Resource': 'jakarta.annotation.Resource',
    'javax.annotation.PostConstruct': 'jakarta.annotation.PostConstruct',
    'javax.annotation.PreDestroy': 'jakarta.annotation.PreDestroy',
    'javax.annotation.security.PermitAll': 'jakarta.annotation.security.PermitAll',
    'javax.annotation.security.RolesAllowed': 'jakarta.annotation.security.RolesAllowed',
    'javax.annotation.security.DenyAll': 'jakarta.annotation.security.DenyAll',
    'javax.annotation.sql.DataSourceDefinition': 'jakarta.annotation.sql.DataSourceDefinition',
}

changed = []
for p in sorted(ROOT.rglob('*.java')):
    if 'target' in p.parts:
        continue
    src = p.read_text(encoding='utf-8')
    out = src
    for old, new in PREFIX:
        out = re.sub(r'(?<![A-Za-z0-9_])' + re.escape(old) + r'(?=[.A-Za-z0-9_])', new, out)
    for old, new in EXACT.items():
        out = re.sub(r'(?<![A-Za-z0-9_])' + re.escape(old) + r'(?![A-Za-z0-9_])', new, out)
    if 'jakarta.annotation.processing' in out:
        sys.exit('误替换 jakarta.annotation.processing: ' + str(p))
    if out != src:
        p.write_text(out, encoding='utf-8')
        changed.append(str(p.relative_to(ROOT)))

print('changed files:', len(changed))
for f in changed[:5]:
    print('  ', f)
