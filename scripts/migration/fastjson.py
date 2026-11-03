#!/usr/bin/env python3
"""fastjson 1.x -> Jackson 3（经 com.mailvor.common.json.JsonUtil 收口）批量迁移。

替换规则按"先静态调用、再注解、再 import、最后类型名"的顺序执行，
避免 JSONObject 被 JSON 的前缀匹配吃掉。

不参与本脚本、需手工改造的文件：
  mshop-system/.../config/RedisConfig.java            （序列化器 + ParserConfig autoType）
  mshop-store/.../tk/config/DataokeConfig.java        （FastJsonHttpMessageConverter）
"""
import re
import sys
from pathlib import Path

SKIP = {
    'mshop-system/src/main/java/com/mailvor/config/RedisConfig.java',
    'mshop-store/src/main/java/com/mailvor/modules/tk/config/DataokeConfig.java',
}

IMPORT_MAP = {
    'com.alibaba.fastjson.JSON': 'com.mailvor.common.json.JsonUtil',
    'com.alibaba.fastjson.JSONObject': 'com.mailvor.common.json.JsonObject',
    'com.alibaba.fastjson.JSONArray': 'com.mailvor.common.json.JsonArray',
    'com.alibaba.fastjson.TypeReference': 'tools.jackson.core.type.TypeReference',
}

ANNOTATION_IMPORTS = {
    'JsonProperty': 'com.fasterxml.jackson.annotation.JsonProperty',
    'JsonIgnore': 'com.fasterxml.jackson.annotation.JsonIgnore',
    'JsonFormat': 'com.fasterxml.jackson.annotation.JsonFormat',
}


def find_close(text, start):
    depth, i = 0, start
    in_str, esc = False, False
    while i < len(text):
        c = text[i]
        if esc:
            esc = False
        elif c == '\\':
            esc = True
        elif in_str:
            if c == '"':
                in_str = False
        elif c == '"':
            in_str = True
        elif c in '([{':
            depth += 1
        elif c in ')]}':
            depth -= 1
            if depth == 0:
                return i
        i += 1
    raise ValueError('括号不匹配: ' + text[start:start + 60])


def split_args(inner):
    parts, buf, depth, in_str, esc = [], [], 0, False, False
    for c in inner:
        if esc:
            buf.append(c)
            esc = False
            continue
        if c == '\\':
            buf.append(c)
            esc = True
            continue
        if c == '"':
            in_str = not in_str
        if not in_str and c in '([{':
            depth += 1
        if not in_str and c in ')]}':
            depth -= 1
        if c == ',' and depth == 0 and not in_str:
            parts.append(''.join(buf).strip())
            buf = []
        else:
            buf.append(c)
    if buf:
        parts.append(''.join(buf).strip())
    return [p for p in parts if p]


def rewrite_jsonfield(text, used):
    """@JSONField(...) -> @JsonProperty/@JsonIgnore/@JsonFormat"""
    out, cursor = [], 0
    for m in re.finditer(r'@JSONField\b', text):
        if m.start() < cursor:
            continue
        out.append(text[cursor:m.start()])
        pos = m.end()
        if pos >= len(text) or text[pos] != '(':
            out.append(m.group(0))
            cursor = m.end()
            continue
        close = find_close(text, pos)
        attrs = {}
        for a in split_args(text[pos + 1:close]):
            km = re.match(r'^([A-Za-z_]\w*)\s*=\s*(.+)$', a, re.S)
            if km:
                attrs[km.group(1)] = km.group(2).strip()
        if 'serialize' in attrs and attrs['serialize'] == 'false':
            used.add('JsonIgnore')
            repl = '@JsonIgnore'
        elif 'format' in attrs:
            used.add('JsonFormat')
            repl = '@JsonFormat(pattern = %s)' % attrs['format']
        elif 'name' in attrs:
            used.add('JsonProperty')
            repl = '@JsonProperty(%s)' % attrs['name']
        else:
            used.add('JsonProperty')
            repl = '@JsonProperty(%s)' % attrs.get('value', '""')
        out.append(repl)
        cursor = close + 1
    out.append(text[cursor:])
    return ''.join(out)


def drop_redundant_jsonfield(text):
    """字段上已有等价的 @JsonProperty("x") 时，先删掉 @JSONField(name = "x") 那一行。

    必须在注解转换之前做：否则转换后会出现两条完全相同的 @JsonProperty，
    而 @JsonProperty 不可重复标注，直接编译报错。
    """
    lines = text.split('\n')
    drop = set()
    for i, line in enumerate(lines):
        m = re.search(r'@JSONField\(name\s*=\s*(".*?")\)', line)
        if not m:
            continue
        name = m.group(1)
        for j in range(max(0, i - 2), min(len(lines), i + 3)):
            if j != i and re.search(r'@JsonProperty\(%s\)' % re.escape(name), lines[j]):
                drop.add(i)
                break
    if not drop:
        return text
    return '\n'.join(l for i, l in enumerate(lines) if i not in drop)


def convert(text):
    used = set()
    # 防呆：hutool 也有同名 JSONObject，一个文件里同时出现两者时无法靠文本区分，
    # 不自动改类型名，只改静态入口与注解，并把该文件列为需人工确认
    mixed = 'cn.hutool.json' in text and re.search(r'(?<![\w.])JSONObject(?!\w)', text) is not None
    # 0) 先消除与 @JsonProperty 重复的 @JSONField
    text = drop_redundant_jsonfield(text)
    # 1) 静态入口先行：JSONObject.parseObject / JSONArray.parseArray / JSONObject.toJSON
    text = re.sub(r'(?<![\w.])JSONObject\.parseObject\s*\(', 'JsonUtil.parseObject(', text)
    text = re.sub(r'(?<![\w.])JSONArray\.parseArray\s*\(', 'JsonUtil.parseArray(', text)
    text = re.sub(r'(?<![\w.])JSONObject\.toJSON\s*\(', 'JsonUtil.toJSON(', text)
    text = re.sub(r'(?<![\w.])JSONObject\.parse\s*\(', 'JsonUtil.parse(', text)
    text = re.sub(r'(?<![\w.])JSON\.parseObject\s*\(', 'JsonUtil.parseObject(', text)
    text = re.sub(r'(?<![\w.])JSON\.parseArray\s*\(', 'JsonUtil.parseArray(', text)
    text = re.sub(r'(?<![\w.])JSON\.toJSONString\s*\(', 'JsonUtil.toJSONString(', text)
    text = re.sub(r'(?<![\w.])JSON\.parse\s*\(', 'JsonUtil.parse(', text)
    text = re.sub(r'(?<![\w.])JSON\.toJavaObject\s*\(', 'JsonUtil.toJavaObject(', text)
    # 2) 注解
    text = rewrite_jsonfield(text, used)
    # 3) 类型名（长名优先）。静态调用已在第 1 步改写完毕，这里允许后跟 '.'
    #    以覆盖 JsonObject.class 这类类字面量
    if not mixed:
        text = re.sub(r'(?<![\w.])JSONObject(?!\w)', 'JsonObject', text)
        text = re.sub(r'(?<![\w.])JSONArray(?!\w)', 'JsonArray', text)
    # new JsonObject(true) 的有序语义在 LinkedHashMap 下天然成立，去掉冗余入参
    text = re.sub(r'new JsonObject\(true\)', 'new JsonObject()', text)
    # 4) import
    for old, new in IMPORT_MAP.items():
        text = re.sub(r'^import\s+%s\s*;$' % re.escape(old), 'import %s;' % new, text, flags=re.M)
    # @JSONField 已转成 Jackson 注解，其 import 直接删除
    text = re.sub(r'^import\s+com\.alibaba\.fastjson\.annotation\.JSONField\s*;\n', '', text, flags=re.M)
    # 残留的 com.alibaba.fastjson.* 其它 import 交给人工处理，先标记
    # 5) 补注解 import
    need = []
    for sym in sorted(used):
        fqcn = ANNOTATION_IMPORTS[sym]
        if fqcn in text:
            continue
        if not re.search(r'(?<![\w.])@' + sym + r'\b', text):
            continue
        need.append('import %s;' % fqcn)
    if need:
        lines = text.split('\n')
        idx = next((i for i, l in enumerate(lines) if l.startswith('import ')), None)
        if idx is None:
            idx = next(i for i, l in enumerate(lines) if l.startswith('package ')) + 1
        lines[idx:idx] = need
        text = '\n'.join(lines)
    return text


SHIM_IMPORTS = {
    'JsonUtil': 'com.mailvor.common.json.JsonUtil',
    'JsonObject': 'com.mailvor.common.json.JsonObject',
    'JsonArray': 'com.mailvor.common.json.JsonArray',
}


def ensure_shim_imports(text):
    """保证引用了 JsonUtil/JsonObject/JsonArray 的文件有对应 import。

    只用了静态方法（如 JSONObject.parseObject）的文件原本只 import 了 JSONObject，
    迁移后调用点变成 JsonUtil.xxx，但脚本没有给它补 import，需要这一道兼底。
    可重复执行。
    """
    if 'package ' not in text:
        return text
    need = []
    for sym, fqcn in SHIM_IMPORTS.items():
        if fqcn in text:
            continue
        if not re.search(r'(?<![\w.])' + sym + r'\b', text):
            continue
        # 排除同名但属于其它包的类型，如 hutool 的 cn.hutool.json.JSONObject
        if re.search(r'^import\s+(?!com\.mailvor\.common\.json)[\w.]+\.' + sym + r'\s*;', text, re.M):
            continue
        need.append('import %s;' % fqcn)
    if not need:
        return text
    lines = text.split('\n')
    idx = next((i for i, l in enumerate(lines) if l.startswith('import ')), None)
    if idx is None:
        idx = next((i for i, l in enumerate(lines) if l.startswith('package ')), None)
        if idx is None:
            # 没找到声明行（全是注释的临时脚本类文件），不动
            return text
        idx += 1
    lines[idx:idx] = need
    return '\n'.join(lines)


def main():
    root = Path(sys.argv[1] if len(sys.argv) > 1 else '.').resolve()
    if '--repair-only' in sys.argv[1:]:
        repaired = []
        for p in sorted(root.rglob('*.java')):
            if 'target' in p.parts:
                continue
            src = p.read_text(encoding='utf-8')
            out = ensure_shim_imports(src)
            if out != src:
                p.write_text(out, encoding='utf-8')
                repaired.append(str(p.relative_to(root)))
        print('补 import 文件数:', len(repaired))
        for f in repaired[:10]:
            print('  ', f)
        return
    changed = []
    manual = []
    for p in sorted(root.rglob('*.java')):
        if 'target' in p.parts:
            continue
        rel = str(p.relative_to(root))
        if rel in SKIP:
            print('跳过（需手工改造）:', rel)
            continue
        src = p.read_text(encoding='utf-8')
        if 'com.alibaba.fastjson' not in src and not re.search(
                r'(?<![\w.])(JSON|JSONObject|JSONArray)\.', src):
            continue
        out = convert(src)
        out = ensure_shim_imports(out)
        if 'cn.hutool.json' in src and re.search(r'(?<![\w.])JSONObject(?!\w)', src):
            manual.append(rel)
        if out != src:
            p.write_text(out, encoding='utf-8')
            changed.append(rel)
    print('fastjson 迁移文件数:', len(changed))
    if manual:
        print('!! 与 hutool JSONObject 同名混用，类型名未自动改，需人工核对:')
        for f in manual:
            print('  ', f)
    left = [str(p.relative_to(root)) for p in root.rglob('*.java')
            if 'target' not in p.parts and 'com.alibaba.fastjson' in p.read_text(encoding='utf-8')]
    print('仍含 com.alibaba.fastjson 的文件:', len(left))
    for f in left:
        print('  ', f)


if __name__ == '__main__':
    main()
