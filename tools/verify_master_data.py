#!/usr/bin/env python3
"""Compare the port's shipped assets and data with the original Fabric master.

Object key order and JSON whitespace are ignored; values, array order, files,
and non-JSON asset bytes must match. The sole normalization is an omitted
"replace" field in a tag file: vanilla TagFile.CODEC defines its default as false.
No recipes or other gameplay fields are normalized away.
Run after ./gradlew runData to verify the providers' freshly generated output.
"""

import argparse
import difflib
import json
from pathlib import Path
import subprocess
import sys


ROOTS = ("src/main/generated", "src/main/resources/assets")
RECIPE_ROOT = "src/main/generated/data/enchantment_infusion/recipes/"
EXPECTED_RECIPES = {
    "enchantment_infusion:enchantment_infusion": 87,
    "minecraft:crafting_shaped": 2,
}


def git(repo, *args):
    return subprocess.check_output(["git", "-C", str(repo), *args])


def included(path):
    return "/.cache/" not in path


def unique_object(pairs):
    result = {}
    for key, value in pairs:
        if key in result:
            raise ValueError(f"Duplicate JSON key: {key}")
        result[key] = value
    return result


def parse_json(contents):
    return json.loads(contents, object_pairs_hook=unique_object)


def is_json(path):
    return path.endswith((".json", ".mcmeta"))


def normalize_json(path, value):
    # Vanilla/Forge TagFile.CODEC omits this optional field when it is false;
    # Fabric writes the default explicitly. Both append to the existing tag.
    if path.startswith("src/main/generated/data/") and "/tags/" in path and isinstance(value, dict):
        value = dict(value)
        value.setdefault("replace", False)
    return value


def canonical_json(value):
    # JSON booleans must not compare equal to Python's integer 0/1.
    return json.dumps(value, ensure_ascii=False, sort_keys=True, indent=2)


def verify(repo, baseline):
    paths = git(repo, "ls-tree", "-rz", "--name-only", baseline, "--", *ROOTS)
    expected_paths = {
        path.decode("utf-8") for path in paths.split(b"\0") if path and included(path.decode("utf-8"))
    }
    if not expected_paths:
        raise ValueError(f"No baseline assets/data found at {baseline}")
    actual_paths = {
        path.relative_to(repo).as_posix()
        for root in ROOTS
        for path in (repo / root).rglob("*")
        if path.is_file() and included(path.relative_to(repo).as_posix())
    }
    errors = []
    for path in sorted(expected_paths - actual_paths):
        errors.append(f"Missing file: {path}")
    for path in sorted(actual_paths - expected_paths):
        errors.append(f"Unexpected file: {path}")

    json_count = 0
    binary_count = 0
    recipes = {}
    baseline_recipes = {}
    for path in sorted(expected_paths | actual_paths):
        expected = git(repo, "show", f"{baseline}:{path}") if path in expected_paths else None
        actual = (repo / path).read_bytes() if path in actual_paths else None
        if is_json(path):
            try:
                expected_json = normalize_json(path, parse_json(expected)) if expected is not None else None
                actual_json = normalize_json(path, parse_json(actual)) if actual is not None else None
            except (ValueError, UnicodeError) as error:
                errors.append(f"Invalid JSON in {path}: {error}")
                continue
            if path.startswith(RECIPE_ROOT) and path.endswith(".json"):
                for value, counter in ((expected_json, baseline_recipes), (actual_json, recipes)):
                    if value is not None:
                        recipe_type = value.get("type") if isinstance(value, dict) else None
                        counter[recipe_type] = counter.get(recipe_type, 0) + 1
            if expected is not None and actual is not None:
                json_count += 1
                if canonical_json(expected_json) != canonical_json(actual_json):
                    diff = difflib.unified_diff(
                        canonical_json(expected_json).splitlines(),
                        canonical_json(actual_json).splitlines(),
                        fromfile=f"{baseline}:{path}",
                        tofile=path,
                        lineterm="",
                    )
                    errors.append("JSON content changed:\n" + "\n".join(diff))
        elif expected is not None and actual is not None:
            binary_count += 1
            if expected != actual:
                errors.append(f"Asset bytes changed: {path}")

    if baseline_recipes != EXPECTED_RECIPES:
        errors.append(f"Unexpected baseline recipe counts: {baseline_recipes}; expected {EXPECTED_RECIPES}")
    if recipes != EXPECTED_RECIPES:
        errors.append(f"Unexpected current recipe counts: {recipes}; expected {EXPECTED_RECIPES}")
    if errors:
        for error in errors:
            print(error, file=sys.stderr)
        print(f"FAIL: {len(errors)} data/asset parity error(s).", file=sys.stderr)
        return 1

    print(f"PASS: {json_count} JSON files and {binary_count} binary assets match {baseline}.")
    print("Recipes: exactly 87 enchantment infusions and 2 shaped crafting recipes.")
    print("Includes advancements, models, blockstates, both languages, loot tables, tags, and textures.")
    print("Only normalization: an omitted tag replace field is the vanilla default false.")
    return 0


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--baseline", default="origin/master", help="Original Fabric Git revision (default: origin/master)")
    parser.add_argument("--repo", type=Path, default=Path(__file__).resolve().parents[1], help="Repository to verify")
    args = parser.parse_args()
    try:
        return verify(args.repo.resolve(), args.baseline)
    except (subprocess.CalledProcessError, OSError, ValueError) as error:
        print(f"Cannot verify assets/data: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    sys.exit(main())
