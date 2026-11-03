#!/usr/bin/env python3
"""springfox (io.swagger.annotations.*) -> springdoc (io.swagger.v3.oas.annotations.*) 批量迁移。

覆盖本项目实际出现的形态：
  @Api(tags=..) / @Api(value=.., tags=..)        -> @Tag(name=..)
  @ApiOperation("x") / (value=..) (+notes +response) -> @Operation(summary=.., description=..)
  @ApiModel / @ApiModelProperty                  -> @Schema(description=..)
  @ApiImplicitParams({...})                      -> @Parameters({...})
  @ApiImplicitParam(name,value,paramType,dataType,defaultValue) -> @Parameter(name,description,required)

注解参数用平衡括号解析而非正则，避免中文描述里的括号把匹配截断。
"""
import re
import sys
from pathlib import Path

IMPORT_V3 = {
    'Tag': 'io.swagger.v3.oas.annotations.tags.Tag',
    'Operation': 'io.swagger.v3.oas.annotations.Operation',
    'Parameter': 'io.swagger.v3.oas.annotations.Parameter',
    'Parameters': 'io.swagger.v3.oas.annotations.Parameters',
    'Schema': 'io.swagger.v3.oas.annotations.media.Schema',
}

OLD_IMPORT = re.compile(r'^import io\.swagger\.annotations\.[A-Za-z*]+;\s*$', re.M)


def find_close(text, start):
    """返回与 text[start]=='(' 匹配的 ')' 下标；跳过字符串字面量。"""
    depth = 0
    i = start
    in_str = in_char = False
    esc = False
    while i < len(text):
        c = text[i]
        if esc:
            esc = False
        elif c == '\\':
            esc = True
        elif in_str:
            if c == '"':
                in_str = False
        elif in_char:
            if c == "'":
                in_char = False
        elif c == '"':
            in_str = True
        elif c == "'":
            in_char = True
        elif c in '([{':
            depth += 1
        elif c in ')]}':
            depth -= 1
            if depth == 0:
                return i
        i += 1
    raise ValueError('括号不匹配，起始: ' + text[start:start + 60])


def split_args(inner):
    """按顶层逗号切分注解参数。"""
    parts, buf, depth = [], [], 0
    in_str = False
    esc = False
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
            buf.append(c)
            continue
        if in_str:
            buf.append(c)
            continue
        if c in '([{':
            depth += 1
        elif c in ')]}':
            depth -= 1
        if c == ',' and depth == 0:
            parts.append(''.join(buf).strip())
            buf = []
        else:
            buf.append(c)
    if buf:
        parts.append(''.join(buf).strip())
    return [p for p in parts if p]


def parse_attrs(args):
    """把参数列表拆成 {name: raw}，位置参数记到 key ''。"""
    attrs, order = {}, []
    for a in args:
        m = re.match(r'^([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.+)$', a, re.S)
        if m:
            attrs[m.group(1)] = m.group(2).strip()
            order.append(m.group(1))
        else:
            attrs[''] = a
            order.append('')
    return attrs, order


def first_of(value):
    """{"A","B"} -> "A" """
    value = value.strip()
    if value.startswith('{'):
        items = split_args(value[1:-1])
        return items[0] if items else '""'
    return value


def render(name, pairs):
    body = ', '.join('%s = %s' % (k, v) for k, v in pairs if v is not None)
    return '@%s(%s)' % (name, body) if body else '@' + name


def convert(text, used):
    def rewrite(name, pos, whole):
        """pos 指向注解名之后的 '('；返回替换后的完整注解文本。"""
        if pos >= len(text) or text[pos] != '(':
            # 无参数的裸注解
            if name == 'ApiModel':
                used.add('Schema')
                return '@Schema'
            return whole
        close = find_close(text, pos)
        args = split_args(text[pos + 1:close])
        attrs, _ = parse_attrs(args)
        out = None
        if name == 'Api':
            used.add('Tag')
            tag = attrs.get('tags') or first_of(attrs.get('value', '""'))
            out = render('Tag', [('name', tag)])
        elif name == 'ApiOperation':
            used.add('Operation')
            summary = attrs.get('value') or attrs.get('') or '""'
            out = render('Operation', [('summary', summary),
                                       ('description', attrs.get('notes'))])
        elif name == 'ApiModel':
            used.add('Schema')
            desc = attrs.get('description') or attrs.get('value') or attrs.get('')
            out = render('Schema', [('description', desc)])
        elif name == 'ApiModelProperty':
            used.add('Schema')
            desc = attrs.get('value') or attrs.get('')
            pairs = [('description', desc)]
            if 'hidden' in attrs:
                pairs.append(('hidden', attrs['hidden']))
            if 'example' in attrs:
                pairs.append(('example', attrs['example']))
            if 'required' in attrs and attrs['required'] != 'false':
                pairs.append(('requiredMode', 'Schema.RequiredMode.REQUIRED'))
            # dataType / allowableValues 在 v3 由反射推导，丢弃
            out = render('Schema', pairs)
        elif name == 'ApiImplicitParams':
            used.add('Parameters')
            # 参数体里内嵌的 @ApiImplicitParam 同样要转，递归一次；
            # 外层已消费整段文本，finditer 里的内层匹配会被 cursor 跳过
            inner = attrs.get('', '{}')
            out = '@Parameters(%s)' % convert(inner, used)
        elif name == 'ApiImplicitParam':
            used.add('Parameter')
            pairs = [('name', attrs.get('name')), ('description', attrs.get('value'))]
            if 'required' in attrs:
                pairs.append(('required', attrs['required']))
            out = render('Parameter', pairs)
        if out is None:
            return whole
        # 连带把注解参数体一起消费掉，否则原括号会残留成 @Schema(x)(x)
        return out, close

    # 长名优先，避免 @ApiModelProperty 被 @ApiModel 抢先匹配
    pattern = re.compile(r'@(ApiModelProperty|ApiImplicitParams|ApiImplicitParam'
                         r'|ApiOperation|ApiModel|ApiParam|Api)\b')
    out, cursor = [], 0
    for m in pattern.finditer(text):
        # 已被外层注解（如 @ApiImplicitParams 的参数体）消费掉，跳过，否则会重复输出
        if m.start() < cursor:
            continue
        res = rewrite(m.group(1), m.end(), m.group(0))
        if isinstance(res, tuple):
            replacement, close = res
            out.append(text[cursor:m.start()])
            out.append(replacement)
            cursor = close + 1
        else:
            # 无参裸注解：只输出替换结果，不能把原文一起留下（否则出现 @ApiModel@Schema）
            out.append(text[cursor:m.start()])
            out.append(res)
            cursor = m.end()
    out.append(text[cursor:])
    return ''.join(out)


def fix_imports(text, used):
    lines = [l for l in text.split('\n') if not OLD_IMPORT.match(l.strip())]
    body = '\n'.join(lines)
    need = []
    for sym in sorted(used):
        fqcn = IMPORT_V3[sym]
        if fqcn in body:
            continue
        if not re.search(r'(?<![\w.])@' + sym + r'\b', body):
            continue
        # 同名简单名已被 import（例如已有 @Schema 的另一种写法）
        if re.search(r'^import\s+%s\s*;' % re.escape(fqcn), body, re.M):
            continue
        need.append('import %s;' % fqcn)
    if not need:
        return body
    for i, line in enumerate(body.split('\n')):
        if line.startswith('import '):
            lines = body.split('\n')
            lines[i:i] = need
            return '\n'.join(lines)
    lines = body.split('\n')
    for i, line in enumerate(lines):
        if line.startswith('package '):
            lines[i + 1:i + 1] = [''] + need
            return '\n'.join(lines)
    return body


def main():
    root = Path(sys.argv[1] if len(sys.argv) > 1 else '.').resolve()
    changed = []
    for p in sorted(root.rglob('*.java')):
        if 'target' in p.parts:
            continue
        src = p.read_text(encoding='utf-8')
        if 'io.swagger.annotations' not in src:
            continue
        used = set()
        out = convert(src, used)
        out = fix_imports(out, used)
        if out != src:
            p.write_text(out, encoding='utf-8')
            changed.append(str(p.relative_to(root)))
    print('swagger 迁移文件数:', len(changed))
    for f in changed[:5]:
        print('  ', f)
    left = sum(1 for p in root.rglob('*.java')
               if 'target' not in p.parts and 'io.swagger.annotations' in p.read_text(encoding='utf-8'))
    print('仍残留 io.swagger.annotations 的文件:', left)


if __name__ == '__main__':
    main()
