#!/usr/bin/env python3
"""Check the built JAR keeps all JEI links inside the optional integration."""
from pathlib import Path
from zipfile import ZipFile

root = Path(__file__).resolve().parents[1]
jars = [p for p in (root / 'build/libs').glob('*.jar') if not p.name.endswith('-sources.jar')]
assert len(jars) == 1, f'Expected one release jar, found {jars}'
with ZipFile(jars[0]) as jar:
    names = jar.namelist()
    assert any('/compat/jei/JEICompat.class' in n for n in names)
    assert not any(n.startswith(('mezz/jei/', 'dev/emi/', 'me/shedaniel/rei/')) for n in names), 'Bundled viewer classes'
    for name in names:
        if name.endswith('.class') and '/compat/jei/' not in name:
            assert b'mezz/jei/' not in jar.read(name), f'JEI link outside isolated integration: {name}'
    metadata = jar.read('META-INF/neoforge.mods.toml').decode()
    assert 'modId="jei"' not in metadata, 'Unexpected JEI dependency'
print(f'PASS: {jars[0].name}; no bundled viewers or common-code JEI linkage')
