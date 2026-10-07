# JEI compatibility checks (Fabric 1.20.1)

JEI is optional. Its API is compile-only and its plugin is loaded through the
`jei_mod_plugin` entrypoint. The development runtime can be selected with:

- `bash gradlew runClient` or `-PrecipeViewer=emi`: existing EMI default
- `bash gradlew runClient -PrecipeViewer=jei`: JEI only
- `bash gradlew runClient -PrecipeViewer=none`: neither viewer
- `bash gradlew runClient -PrecipeViewer=coexist`: EMI and JEI
- `bash gradlew runClient -PrecipeViewer=rei`: REI only

JEI 15.20.0.106 is pinned for Minecraft 1.20.1. Its runtime requires Fabric
Loader 0.16.3 or newer, so the development loader is set to 0.16.3. The
published mod retains its original loader baseline when JEI is not installed. No JEI runtime is bundled or
required by the published mod. No transfer handlers or gameplay changes are added.

## Automated checks

Run `python3 tests/verify_jei.py` and `bash gradlew build -PrecipeViewer=jei`.
The Python checks inspect optional dependency wiring, registration of both recipe
implementations, both catalysts, retention of actual ingredient/output stacks,
and slot bounds/non-overlap for one through eight pedestal ingredients. They are
static checks, not proof of in-game rendering or JEI search behavior. This
branch has no Java unit-test harness; Gradle reports `test NO-SOURCE`. Do not
interpret that task as a passing JEI runtime test.

## Client runtime checklist

In a local test world with JEI only:

1. Open recipes through both the infusion table and the pedestal. Check the
   translated category title and table icon.
2. View enchantment infusion and simple infusion recipes. The center input,
   one through eight ring inputs, arrow, and output must fit the 138×84 area.
   Use a test datapack for counts not present in the standard recipes.
3. Inspect the lowest-level ordinary book input and higher-level enchanted book
   inputs. Confirm cycling and tooltips retain enchantment type and level.
4. Use JEI recipe/usage searches on books with different enchantments and levels.
   JEI's native enchanted-book subtype handling must distinguish them. No custom
   subtype handler replaces vanilla behavior.
5. Check simple infusion item counts/NBT in the output and tag alternatives in
   the inputs. JEI displays the existing recipe output; it does not simulate
   arbitrary copied input NBT.
6. Reload recipes and reconnect to the world. Check no duplicate registration
   or stale recipe results.
7. Repeat startup with `none`, `emi`, and `coexist`; check logs for missing JEI
   classes when absent and duplicate/broken category behavior when together.

Record client results separately; a successful build alone does not satisfy
this checklist. All recipes, ingredient matching, EMI, and REI remain unchanged.

## Verification record

- Full Gradle build passed with the JEI profile.
- Headless `runDatagen -PrecipeViewer=none` passed. Generated recipe/resource
  content was unchanged; only local generator cache timestamps/path separators
  changed and were discarded.
- Resolved runtime classpaths: `none` has neither viewer; `jei` has JEI only;
  `emi` has EMI only; `coexist` has both. Compile-only JEI APIs are absent from
  no-JEI runtimes and the published mod does not bundle JEI classes.
- All five Python static checks passed. Client checks are recorded separately.
