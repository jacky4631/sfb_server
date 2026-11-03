#!/usr/bin/env python3
"""Spring Boot 2.7 -> 3/4 的配置属性迁移。

目前处理：
  spring.redis.**            -> spring.data.redis.**
  spring.redis.jedis.pool.** -> 保持（Lettuce 下本就是无效配置，仅换前缀）

主配置文件 application.yml 里已经存在 spring.data.redis 节点，脚本遇到已有节点会跳过，
避免生成两个同名 key（SnakeYAML 会后者覆盖前者，属于静默故障）。
"""
import re
import sys
from pathlib import Path


def migrate(text):
    crlf = '\r\n' in text
    lines = text.split('\n')
    out = []
    i = 0
    changed = False
    n = len(lines)
    while i < n:
        line = lines[i]
        # 找到 spring: 直属下的 "  redis:" 块
        if re.match(r'^  redis:(\s|#.*)?$', line.rstrip('\r')):
            # 收集该块的后续行（缩进 > 2）
            block = []
            j = i + 1
            while j < n:
                nxt = lines[j]
                if nxt.strip() and (len(nxt.rstrip('\r')) - len(nxt.lstrip())) <= 2:
                    break
                block.append(nxt)
                j += 1
            # 判断是否已经在 spring: 之下（前一个顶层键是 spring:）
            in_spring = any(re.match(r'^spring:\s*$', l.rstrip('\r')) for l in lines[:i])
            has_data = any(re.match(r'^  data:(\s|#.*)?$', l.rstrip('\r')) for l in lines[:i])
            if in_spring and not has_data:
                r = '\r' if crlf else ''
                out.append('  data:' + r)
                out.append('    redis:' + r)
                for b in block:
                    out.append(('  ' + b) if b.strip() else b)
                changed = True
                i = j
                continue
        out.append(line)
        i += 1
    return '\n'.join(out), changed


def main():
    root = Path(sys.argv[1] if len(sys.argv) > 1 else '.').resolve()
    done = []
    for f in sorted(root.rglob('application*.yml')):
        if 'target' in f.parts:
            continue
        text = f.read_bytes().decode('utf-8', 'replace')
        new, changed = migrate(text)
        if changed:
            f.write_bytes(new.encode('utf-8'))
            done.append(str(f.relative_to(root)))
    print('迁移文件数:', len(done))
    for f in done:
        print('  ', f)


if __name__ == '__main__':
    main()
