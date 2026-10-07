# JEI integration (NeoForge 1.21.1)

JEI is discovered only through `@JeiPlugin`. Neither common initialization nor the mod metadata requires JEI. Only the JEI compatibility package links JEI classes; JEI and other viewer libraries are not bundled.

The plugin registers the existing infusion recipe type (both enchantment and simple infusion serializers) and both table/pedestal catalysts. The 138×84 view follows the existing EMI geometry: 1–8 circular pedestal inputs, central input, arrow, and output. Native ingredient stacks preserve prerequisite enchanted-book level alternatives, and the native output stack preserves its enchantment components for JEI recipes/usages indexing. This does not promise every valid equipment outcome or recipe transfer.

## Pinned official API

- [JEI common API 19.27.0.340](https://maven.blamejared.com/mezz/jei/jei-1.21.1-common-api/19.27.0.340/)
- [NeoForge API 19.27.0.340](https://maven.blamejared.com/mezz/jei/jei-1.21.1-neoforge-api/19.27.0.340/)
- [NeoForge runtime 19.27.0.340](https://maven.blamejared.com/mezz/jei/jei-1.21.1-neoforge/19.27.0.340/)

The official runtime metadata retains its 1.21 range; NeoForge 21.1.252’s FancyModLoader 4.0.44 `VersionSupportMatrix` explicitly accepts 1.21 mods on 1.21.1, so no dependency override or modified JEI JAR is used.

Source API inspection confirms `RecipeType.createRecipeHolderType`, native ingredient slots, standard/output backgrounds and registry recipe IDs. `registerRecipes` reads the current client recipe manager when JEI registers/reloads plugins; it keeps no static recipe cache.

## Reproducible checks

```
./gradlew build -PrecipeViewer=jei
./gradlew test -PrecipeViewer=none --rerun-tasks
python3 scripts/check_viewer_isolation.py
./gradlew dependencies --configuration runtimeClasspath -PrecipeViewer=none
./gradlew runClient -PrecipeViewer=jei
./gradlew runClient -PrecipeViewer=none
```

The added tests check the existing custom ingredient's minimum-through-maximum enchanted-book alternatives and exact native output enchantment level. The archive check verifies optional linkage and absence of bundled viewer classes. These checks alone do not substitute for a real client or dedicated server startup.

Manual checklist: JEI-only startup; infusion category and both catalysts; ordinary book and prerequisite enchanted books; output recipe/usage focus; 1–8 ingredient ring and a datapack simple infusion; data reload; clean no-viewer startup. Record actual results before release.

## Automated verification (2026-10-07)

- Java 21 / NeoForge 21.1.252 full `build -PrecipeViewer=jei`: passed.
- 17 JUnit tests: passed, zero failures/errors/skips. All 17 also passed with `-PrecipeViewer=none --rerun-tasks`.
- Release JAR optional-linkage/bundling check: passed.
- No-viewer `runtimeClasspath` dependency report: no JEI, EMI or REI runtime dependencies.

## Real-client verification (2026-10-07)

The JEI-only NeoForge 21.1.252 client successfully loaded the category via the infusion table’s usages and showed both catalysts. The world exposed 87 default recipes plus eight external QA-only simple infusion recipes (95 in total). The test datapack is not part of the release JAR or repository recipe resources.

Verified by actual GUI interaction:

- Respiration II output recipe focus narrowed to the matching single infusion recipe; prerequisite enchanted-book tooltip candidates retained levels.
- The input’s usages opened its matching infusion recipes.
- All eight simple infusion fixtures displayed the 1–8 pedestal ring layouts, central input, and correct output counts.
- After `/reload`, focusing the inventory’s Efficiency I book still opened its single matching recipe with a plain central book and four pedestal ingredients.

Evidence (game F2 screenshots, client-local filenames):

- `2026-10-06_20.34.25.png`: exact Respiration II output focus.
- `2026-10-06_20.34.35.png`: prerequisite native enchanted-book tooltip.
- `2026-10-06_20.36.28.png`: one-ingredient simple infusion.
- `2026-10-06_20.36.29.png`: eight-ingredient simple infusion.
- `2026-10-06_20.37.41.png`: Efficiency I after data reload.

Screenshots are separate QA evidence, not mod assets. The environment’s unavailable audio device and Yggdrasil network errors did not block this local-world test. No dedicated-server or full REI runtime validation is claimed.

### Optional runtime and coexistence

- Actual `recipeViewer=none` client reached the main menu with only Minecraft, NeoForge and Enchantment Infusion, then quit cleanly. This complements the no-viewer test/dependency and archive checks.
- The JEI+EMI client loaded its world and both viewers. Actual native EMI infusion UI showed exactly 95 recipe pages (87 defaults plus eight QA fixtures), both catalysts and the correct ring/output, with no infusion duplicates. EMI’s own `PluginCallerMixin` skips JEI plugins for namespaces already handled natively by EMI, so the existing native EMI integration owns the infusion category in this profile.
- The coexistence log contained 344 duplicate-ID messages for upstream JEI tag-view imports (`jei:/c/*` and `jei:/minecraft/*`). None referred to `enchantment_infusion` or the QA recipe namespace. This is disclosed rather than changing existing EMI code or unrelated viewer behavior.
