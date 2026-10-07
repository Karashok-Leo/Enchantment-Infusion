# Optional JEI integration (Forge 1.20.1)

JEI adds the existing Enchantment Infusion recipe category and both the infusion table and pedestal as catalysts. It reads the same loaded recipe type as EMI/REI, including data-pack recipes. The central ingredient, surrounding pedestal ingredients, arrow, and result use the existing 138 × 84 EMI layout.

Ingredients use their existing `getMatchingStacks()` representatives, including native enchanted books for prerequisite levels. The output is the recipe's native `ItemStack`, so JEI/Minecraft render its enchantment name and level normally. There are no extra explanatory tooltip lines, transfer handlers, or gameplay changes.

JEI is discovered through `@JeiPlugin`. No common initializer references JEI, and no required JEI dependency is added to `mods.toml`. The API is compile-only; no viewer is embedded in the distributed mod. Architectury Loom's Forge remapper expects SRG, whereas JEI's split API artifact uses Mojmap. This branch therefore compiles against the API contained in the SRG Forge release JAR, with transitive dependencies disabled. Only `mezz.jei.api` types are used.

## Development profiles

Keep Architectury Loom and Yarn mappings. Java 17 runs Minecraft; Java 21 may run Gradle.

- `./gradlew runClient -PrecipeViewer=jei`: JEI only
- `./gradlew runClient -PrecipeViewer=emi`: EMI only (default, preserving the previous development setup)
- `./gradlew runClient -PrecipeViewer=none`: no recipe viewer
- `./gradlew runClient -PrecipeViewer=coexist`: EMI and JEI
- `./gradlew runClient -PrecipeViewer=rei`: REI only

The profiles affect development runtime dependencies only, not the shipped JAR. Existing EMI/REI source and registration remain unchanged. Invalid profile names fail early.

Pinned JEI version: **15.20.0.106**, for Minecraft **1.20.1 / Forge**. Compile-only API access and the optional runtime use the same Forge artifact and version from the official Maven repository:

- https://maven.blamejared.com/mezz/jei/jei-1.20.1-forge/15.20.0.106/

## Verification

Run `./gradlew build -PrecipeViewer=jei` and the existing `runData` regression suite, then `python3 tools/verify_master_data.py --baseline 2cec737573188c7ed28fab0c60bc43bfa1d959bc`.

In an isolated client profile, check recipe registration, both catalyst shortcuts, the pedestal ring and central slot, level-one and upgrade recipes, and native prerequisite/output enchantment tooltips. Check `none` for optional-classloading isolation and `emi`/`coexist` for regressions. GUI results are recorded separately after actual client validation; compilation alone is not GUI acceptance.

### Automated results (2026-10-07 UTC)

- Fresh JEI-first remapping followed by `clean build runData -PrecipeViewer=jei`: passed. No duplicate-class remap warnings.
- Test-only JEI builder proxies captured the actual category slots and verified pedestal inputs, central slot positions, ordinary books, efficiency IV/V prerequisite representatives, and unchanged native level-I/level-V enchanted-book output stacks.
- Existing Forge runtime recipe regression suite: 10/10 with JEI and 10/10 with `recipeViewer=none`.
- Existing JUnit callback regression: 1/1, no failures or skips.
- Baseline resource comparison: 107 JSON files and 6 binary assets unchanged, including all 87 infusion recipes and both crafting recipes.
- Runtime dependency reports: `none` has neither viewer; `emi` only EMI; `jei` only JEI; `coexist` both. No JEI implementation/API classes or validation classes are embedded in the production JAR.

Client GUI acceptance is tracked separately and must not be inferred from these automated results.

### Client results (2026-10-07 UTC)

- JEI-only: Efficiency I recipe lookup opens exactly 1/1 with four ingredients, a plain central book and native output tooltip. Both catalysts open the infusion category.
- Temporary QA data-pack recipes confirm an eight-ingredient ring, repeated amethyst inputs, stack count three, preserved custom-name NBT, and a planks tag cycling eleven alternatives. The category has 89 recipes: the original 87 plus two local QA recipes; QA data is not shipped.
- After successful data-pack reload, Efficiency I usage opens exactly the Efficiency II upgrade recipe with its enchanted prerequisite.
- No-viewer client reaches the main menu and exits normally, verifying optional classloading.
- EMI/JEI coexistence: the native EMI category remains singular, both catalysts appear, and 89 recipes display over 45 pages with correct rings and outputs.
- Coexistence development logs contain 324 duplicate JEI vanilla/Forge tag-recipe IDs and 160 EMI synthetic brewing-recipe diagnostics, with no infusion errors. The infusion plugin initializes and reloads successfully. The cloud client also reports unavailable audio and a failed Yggdrasil key fetch; neither prevents this offline GUI validation.

Screenshots and complete client logs are retained in the separate verification package.
