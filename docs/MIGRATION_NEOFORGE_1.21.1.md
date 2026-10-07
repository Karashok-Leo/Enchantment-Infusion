# NeoForge 1.21.1 移植说明

对比基线为原 Fabric 1.20.1 `master` 提交 `2cec737573188c7ed28fab0c60bc43bfa1d959bc`。本分支复用已完成的 Fabric 1.21.1 适配提交 `7589f9289a4d06935fc458a88e74ce222529100a` 中必要的游戏 API 变化，再替换加载器接口；不改变灌注玩法、方块参数、配方成本或默认配方 ID。

## 构建与加载器

- Minecraft 1.20.1 → 1.21.1，Java 17 → 21；模组版本仍为 1.3.4。
- Fabric Loom / Loader / Fabric API 替换为官方 ModDevGradle 2.0.148、NeoForge 21.1.252。构建使用 Mojang 官方映射，故几乎所有 Java 文件中的 Minecraft 类名、方法名及导入路径需要对应改名。
- 保持独立 NeoForge 分支，不引入跨加载器框架。`fabric` 启动包改为 `neoforge`；`fabric.mod.json` 改为 `META-INF/neoforge.mods.toml`，补充资源包元数据。
- Java 21 编译、源码 JAR、现有许可证打包及 Maven publication 保留。增加 NeoForge 的 JUnit 启动支持以运行回归测试。
- 可选配方查看器仍为 EMI 1.1.22+1.21.1、REI 16.0.799，使用各自 NeoForge API。EMI 仍供开发客户端本地运行；两者都不是本模组的强制依赖。

## Java 源码的必要变化

路径相对于 `src/main/java/karashokleo/enchantment_infusion/`。

### 平台入口与注册

- `neoforge/EnchantmentInfusion.java`：`@Mod` 构造入口绑定 NeoForge mod event bus；标识符改用 `ResourceLocation.fromNamespaceAndPath`。
- `init/EIBlocks.java`、`EIItems.java`、`EIRecipes.java`：将启动时直接注册安排到对应的 `RegisterEvent` 中；保留原来的公开实例字段，避免把整个代码库重写为 Supplier 调用。方块实体改用原版 `BlockEntityType.Builder`。
- 创造模式物品使用 `BuildCreativeModeTabContentsEvent`，仍放在功能方块栏附魔台之后，灌注台在前、基座在后。
- `neoforge/EnchantmentInfusionClient.java`：客户端初始化与方块实体渲染器注册改用 NeoForge 客户端事件；保留 cutout 渲染、物品旋转和原偏移量。
- `api/event/InfusionCompleteCallback.java`：Fabric 回调接口改为 NeoForge 游戏事件总线上的非取消事件。`EnchantmentInfusionTableTile` 在原完成位置发布事件；仍包含 world、pos、output、inventory、recipe，可通过对应 getter 读取。其他模组若监听此 API，需要改为 NeoForge 事件监听方式。
- `api/recipe/EnchantmentIngredient.java`：Fabric CustomIngredient/serializer 改为 NeoForge `ICustomIngredient` 与 `IngredientType`，保留附魔等级测试与附魔书预览；非简单原料使用注册表感知网络 codec。通过通用 Ingredient JSON 使用此类型时，NeoForge 使用 `type` 字段。

### 方块与方块实体

- 两个具体方块补齐 1.21.1 要求的 Properties 构造参数与 MapCodec；形状、强度、工具要求、发光状态不变。
- `api/block/AbstractInfusionBlock.java`：适配新版有物品/空手交互拆分、寻路签名及自定义名称组件；保留主手操作与副手排除。
- `api/block/entity/NameableSingleStackTile.java`：适配单槽容器方法、注册表感知物品/NBT/名称读写、初始区块同步；各种取出路径继续触发更新。空栈通过 `ItemStack.parseOptional` 读取；名称通过隐式组件桥接，配合新版掉落表复制名称。
- `InfusionInventory.java` 实现 1.21.1 `RecipeInput`；继续保留库存访问、扣减、余物和脏标记功能。它不再是原版 1.20.1 Inventory 接口的子类型；外部调用方若依赖旧接口需适配。
- `EnchantmentInfusionTableTile.java`：匹配结果通过 `RecipeHolder` 解包；更新保存/读取与容器方法。八个基座的位置、100 tick 灌注时间、粒子和声音保留。

### 配方、附魔及物品数据

- `InfusionRecipe.java`：使用 1.21.1 `assemble` 与注册表查询参数；在灌注计算前复制中央栈，避免直接修改输入。
- `EnchantmentInfusionRecipe.java`：附魔引用改为动态注册表 `Holder<Enchantment>`；附魔及附魔书等级读取/修改使用物品组件；前置附魔在成功灌注时消耗。`force` 仍只绕过适用性和冲突，不能绕过前置附魔或等级提升要求。
- `SimpleInfusionRecipe.java`：继续保留 `copy_nbt` 名称和默认 true；在 1.21.1 复制栈的组件变更，并递归合并自定义数据，保留输出物品的默认组件。false 时不复制输入数据。
- 配方 ID 由原版 `RecipeHolder` 持有；配方记录不再自带 ID。
- 两个 serializer、`EnchantmentIngredient` 与 `SerialUtil` 使用 MapCodec / StreamCodec；保持可选前置附魔、默认 force=false、默认 copy_nbt=true、1–8 个基座原料限制。输出附魔等级范围为新组件支持的 1–255。
- 简单灌注 `output` 使用 1.21.1 ItemStack JSON：例如 `{"id":"minecraft:diamond","count":1}`，复杂数据使用 components。旧版 Count/NBT 结构及旧存档不承诺未经升级即可直接使用。

### 数据生成与可选查看器

- 所有数据生成器改为 NeoForge/原版 provider，并由 `GatherDataEvent` 注册；输出仍为 `src/main/generated`。两个配方 builder 改为向 RecipeOutput 交付实际配方，由统一 codec 编码。
- `RecipeProvider` 在生成时解析附魔 Holder；87 条灌注定义与两条方块合成定义保留。
- EMI / REI 的发现方式分别改为 `@EmiEntrypoint` / `@REIPluginClient`；RecipeHolder 解包与预览接口适配新版。分类、工作台、圆形原料布局与槽位尺寸不变。
- 其余文件的主要变化是 Yarn → Mojang 名称映射（包括 EITexts、渲染器、辅助函数、各查看器 display/category 等），并非玩法重写。

## 数据文件变化

- `recipes` → `recipe`（89 个）、`advancements` → `advancement`（2 个）、`loot_tables` → `loot_table`（2 个）、`tags/blocks` → `tags/block`（1 个）。
- 原版附魔键 `minecraft:sweeping` → `minecraft:sweeping_edge`；模组配方 ID 保留 `sweeping/1`、`/2`、`/3`。
- 方块合成结果改为 result.id/result.count；默认 force=false、show_notification=true 可由 codec 省略。
- 方块掉落名称复制由 copy_name 改为 copy_components，包含 minecraft:custom_name。NeoForge/原版掉落生成器还在两份掉落表中添加标准 random_sequence 字段；固定的方块掉落结果、名称复制和爆炸条件不变。
- 贴图、手工方块模型、翻译文本、方块状态和物品模型保持原设计。生成器缓存可能更新，不属于玩法数据。

## 验证状态

已完成：

- NeoForge 21.1.252 / Java 21 的源码编译通过，包括 EMI 和 REI 可选 API。
- `runData` 六个 provider 全部完成；以 master 的完整 JSON 逐项比较 89 条配方，通过。仅归一化上文所列 1.21.1 格式、默认值省略和横扫之刃注册表名称变化。
- 全部 19 项原有贴图、模型、方块状态和翻译资源保持一致；模型/状态仅允许 JSON 排版差异。
- 15 个自动化测试全部通过（10 个配方测试、5 个方块实体测试），0 失败、0 错误、0 跳过。覆盖适用性、强制附魔边界、前置附魔消耗、升级、组件/自定义数据复制、无序且按数量匹配、JSON/网络往返、无效数据、名称与单栈保存/同步及余物写回。
- 完整 `build` 通过，JAR 中包含全部 89 条默认配方。

### 真实客户端验收

2026-10-07 01:25–01:27 UTC，在官方 NeoForge 开发客户端的独立创造模式超平坦世界中完成：

- 中央灌注台 `(0,100,0)`，八个同高基座 `(0,100,-3)`、`(2,100,-2)`、`(3,100,0)`、`(2,100,2)`、`(0,100,3)`、`(-2,100,2)`、`(-3,100,0)`、`(-2,100,-2)`。
- 内置 `enchantment_infusion:efficiency/1`：中央普通书，基座为糖、铁镐、两个紫水晶碎片，余下基座为空。
- 准备命令仅放置真实方块与原料、给予普通书并调整视角，没有生成或替换附魔产物。
- 实际右键启动灌注，经过原定 100 tick 后原料消耗并得到效率 I 附魔书。再次空手右键取回，物品栏提示文本确认 Efficiency I。
- 保存退出并重新进入世界后，结构与物品栏中的附魔书仍保留；再次查看提示文本确认效率 I 没有丢失。

游戏自带 F2 截图（文件名采用云端客户端时区）：

- `2026-10-06_19.25.57.png`：实际结构及原料。
- `2026-10-06_19.26.22.png`：灌注进行中的粒子和工作状态。
- `2026-10-06_19.26.46.png`：原料消耗后的灌注台产物。
- `2026-10-06_19.27.10.png`：取回后的效率 I 附魔书提示文本。

截图为本次运行的实际像素，不属于模组资源，也不打包进 JAR。

### 尚未覆盖及环境限制

- EMI 插件成功加载和重载；未据此声称配方查看界面已完整验收。REI 源码通过对应 API 编译，未做单独运行时验收。
- 本次实机重点为约定的方块结构与一条真实配方；中断、命名方块破坏掉落、容器原料余物等附加场景未逐一实机验收，其中相关数据路径有自动化测试覆盖。
- 云端 OpenAL 音频设备无法打开，未验证实际听觉效果。在线档案与 Yggdrasil 公钥请求连接被拒绝，未影响独立单人世界及本次灌注。
- 本次尚未完成多人/专用服务器实机测试，不以单人结果代替。

## 参考

- [NeoForge 1.21.1 开发文档](https://docs.neoforged.net/docs/1.21.1/gettingstarted/)
- [官方 ModDevGradle MDK](https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle)
- [NeoForge 注册时机](https://docs.neoforged.net/docs/1.21.1/concepts/registries/)
- [NeoForge 自定义原料](https://docs.neoforged.net/docs/1.21.1/resources/server/recipes/ingredients/)
