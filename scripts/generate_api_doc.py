import re
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "docs" / "API接口清单.md"

CONTROLLER_PATTERN = "mshop-*/src/main/java/**/*Controller.java"
ANN_METHOD_RE = re.compile(
    r"@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping|RequestMapping)\s*(\((.*?)\))?",
    re.S,
)
API_OPERATION_BLOCK_RE = re.compile(r"@ApiOperation\s*\((.*?)\)", re.S)
API_OPERATION_SIMPLE_RE = re.compile(r"^\s*\"(.*?)\"\s*$", re.S)
CLASS_RE = re.compile(r"\bclass\s+(\w+)")
PACKAGE_RE = re.compile(r"^\s*package\s+([\w\.]+);", re.M)


def pick_paths(arg_text: str):
    if not arg_text:
        return [""]
    explicit = re.findall(r"(?:value|path)\s*=\s*(\{.*?\}|\".*?\")", arg_text, re.S)
    source = " ".join(explicit) if explicit else arg_text
    paths = re.findall(r'\"([^\"]*)\"', source)
    return paths or [""]


def pick_request_methods(arg_text: str):
    if not arg_text:
        return ["ALL"]
    methods = re.findall(r"RequestMethod\.([A-Z]+)", arg_text)
    return methods or ["ALL"]


def normalize_join(base: str, sub: str):
    b = (base or "").strip()
    s = (sub or "").strip()
    if not b and not s:
        return "/"
    if b and not b.startswith("/"):
        b = "/" + b
    if s and not s.startswith("/"):
        s = "/" + s
    if not b:
        p = s
    elif not s:
        p = b
    else:
        p = b.rstrip("/") + s
    return re.sub(r"/{2,}", "/", p) or "/"


def extract_api_operation_text(block: str):
    """从@ApiOperation注解中提取 value/notes 作为功能说明。"""
    simple = API_OPERATION_SIMPLE_RE.search(block)
    if simple:
        return simple.group(1).strip(), ""

    value = ""
    notes = ""
    value_m = re.search(r"value\s*=\s*\"(.*?)\"", block, re.S)
    if value_m:
        value = value_m.group(1).strip()
    elif block.strip().startswith('"') and block.strip().endswith('"'):
        value = block.strip().strip('"').strip()
    notes_m = re.search(r"notes\s*=\s*\"(.*?)\"", block, re.S)
    if notes_m:
        notes = notes_m.group(1).strip()
    return value, notes


def scan_controllers():
    controllers = []
    all_endpoints = 0

    for f in sorted(ROOT.glob(CONTROLLER_PATTERN)):
        text = f.read_text(encoding="utf-8", errors="ignore")
        if "@RestController" not in text and "@Controller" not in text:
            continue

        class_m = CLASS_RE.search(text)
        if not class_m:
            continue

        package_m = PACKAGE_RE.search(text)
        class_name = class_m.group(1)
        package_name = package_m.group(1) if package_m else ""

        before_class = text[: class_m.start()]
        class_base = ""
        reqs = list(re.finditer(r"@RequestMapping\s*\((.*?)\)", before_class, re.S))
        if reqs:
            class_base = pick_paths(reqs[-1].group(1))[0]

        endpoints = []
        method_region = text[class_m.end() :]
        for ann_m in ANN_METHOD_RE.finditer(method_region):
            ann, _, args = ann_m.groups()
            tail = method_region[ann_m.end() : ann_m.end() + 500]
            method_m = re.search(
                r"\b(public|protected|private)\b[^{;\n]*?\b(\w+)\s*\(",
                tail,
                re.S,
            )
            if not method_m:
                continue
            method_name = method_m.group(2)

            # 兼容 @ApiOperation 写在 Mapping 前后两种风格
            anno_zone_start = max(0, ann_m.start() - 500)
            anno_zone_end = ann_m.end() + method_m.start()
            anno_zone = method_region[anno_zone_start:anno_zone_end]
            op_desc = ""
            op_notes = ""
            op_blocks = list(API_OPERATION_BLOCK_RE.finditer(anno_zone))
            if op_blocks:
                op_desc, op_notes = extract_api_operation_text(op_blocks[-1].group(1))

            if ann == "GetMapping":
                methods = ["GET"]
            elif ann == "PostMapping":
                methods = ["POST"]
            elif ann == "PutMapping":
                methods = ["PUT"]
            elif ann == "DeleteMapping":
                methods = ["DELETE"]
            elif ann == "PatchMapping":
                methods = ["PATCH"]
            else:
                methods = pick_request_methods(args or "")

            for hm in methods:
                for path in pick_paths(args or ""):
                    endpoints.append(
                        (
                            hm,
                            normalize_join(class_base, path),
                            method_name,
                            op_desc,
                            op_notes,
                        )
                    )

        uniq = []
        seen = set()
        for ep in endpoints:
            if ep in seen:
                continue
            seen.add(ep)
            uniq.append(ep)

        module = f.relative_to(ROOT).parts[0]
        controllers.append(
            {
                "module": module,
                "class": class_name,
                "package": package_name,
                "file": str(f.relative_to(ROOT)).replace("\\", "/"),
                "base": class_base or "/",
                "endpoints": uniq,
            }
        )
        all_endpoints += len(uniq)

    controllers.sort(key=lambda x: (x["module"], x["file"]))
    return controllers, all_endpoints


def build_markdown(controllers, all_endpoints):
    lines = []
    lines.append("# API接口说明文档（自动扫描）")
    lines.append("")
    lines.append(f"- 生成时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    lines.append(f"- 扫描范围：`{CONTROLLER_PATTERN}`")
    lines.append(f"- 接口类总数：**{len(controllers)}**")
    lines.append(f"- 接口条目总数：**{all_endpoints}**")
    lines.append("")
    lines.append("> 说明：该清单按控制器注解自动提取，动态路由或条件映射需人工复核。")
    lines.append("")

    module_index = {}
    for c in controllers:
        module_index[c["module"]] = module_index.get(c["module"], 0) + 1

    lines.append("## 模块索引")
    for module, count in sorted(module_index.items()):
        lines.append(f"- `{module}`：{count} 个接口类")
    lines.append("")

    current_module = None
    for c in controllers:
        if c["module"] != current_module:
            current_module = c["module"]
            lines.append(f"## 模块：`{current_module}`")
            lines.append("")

        lines.append(f"### `{c['class']}`")
        lines.append(f"- 类路径：`{c['file']}`")
        lines.append(f"- Java包：`{c['package']}`")
        lines.append(f"- 基础路径：`{c['base']}`")
        if not c["endpoints"]:
            lines.append("- 接口：无显式映射（需人工确认）")
        else:
            lines.append("- 接口列表：")
            for hm, path, method_name, op_desc, op_notes in c["endpoints"]:
                desc = op_desc or f"方法 `{method_name}`（未标注ApiOperation）"
                if op_notes and op_notes != op_desc:
                    desc = f"{desc}；备注：{op_notes}"
                lines.append(f"  - `{hm}` `{path}` -> `{method_name}`")
                lines.append(f"    - 功能：{desc}")
        lines.append("")

    return "\n".join(lines)


def main():
    controllers, all_endpoints = scan_controllers()
    markdown = build_markdown(controllers, all_endpoints)
    OUTPUT.write_text(markdown, encoding="utf-8")
    print(f"已生成：{OUTPUT}")
    print(f"控制器：{len(controllers)}，接口：{all_endpoints}")


if __name__ == "__main__":
    main()


