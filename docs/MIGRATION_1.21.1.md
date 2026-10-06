# Enchantment Infusion 1.21.1 移植说明

本次修改以提交 `2cec737`（`Fix EMI compat Update loom Reformat`）为基线，将 Fabric 1.20.1 版本移植到 Minecraft 1.21.1、Yarn `1.21.1+build.3`。范围为版本适配、默认数据迁移及对应验证，没有新增玩法或调整默认配方成本。

源码编译、成品构建、15 个测试及本次约定的实机验收均已通过。87 个灌注配方和 2 个方块合成配方共 89 个，已逐项核对配方 ID、原料、数量、前置附魔和输出等级。实机验收使用真实放置的灌注台与基座，完成一次内置效率 I 配方；具体证据和未覆盖项目见下文。

## 依赖与构建配置

| 项目 | 基线 | 1.21.1 版本 |
| --- | --- | --- |
| Minecraft | 1.20.1 | 1.21.1 |
| Yarn | 1.20.1+build.10 | 1.21.1+build.3 |
| Java | 17 | 21 |
| Fabric Loader | 0.15.11 | 0.16.14 |
| Fabric API | 0.92.2+1.20.1 | 0.102.1+1.21.1 |
| EMI | 1.1.22+1.20.1 | 1.1.22+1.21.1 |
| REI | 12.1.785 | 16.0.799 |

涉及文件：

- `gradle.properties`：更新以上游戏与依赖版本；模组版本仍为 `1.3.4`。
- `build.gradle`：编译目标与源代码兼容级别改为 Java 21；加入 JUnit Jupiter、Fabric Loader JUnit 测试依赖与测试配置；显式请求 `runDatagen` 时让 `sourcesJar` 在其后运行，避免 Gradle 对生成目录的任务顺序报错。
- `src/main/resources/fabric.mod.json`：声明 Minecraft `~1.21.1`、Java `>=21`、Fabric Loader `>=0.16.14`。
- `README.md`：补充 Minecraft 1.21.1 / Java 21 运行要求与本文链接；简单灌注示例使用小写 `count`，移除普通物品原料中无意义的 `min_level`，并将 `copy_nbt` 说明更新为组件和自定义数据复制。

EMI 和 REI 仍为可选兼容，不写入模组的强制运行依赖。原有开发环境 EMI 本地运行配置保留。

## Java 源码变更

以下路径均相对于 `src/main/java/karashokleo/enchantment_infusion/`。

### 注册与方块构造

- `fabric/EnchantmentInfusion.java`：使用 `Identifier.of`，替代不再公开的 `Identifier` 构造方法。
- `init/EIItems.java`：`FabricItemSettings` 改为原版 `Item.Settings`；方块物品 ID 与创造模式物品栏位置保留。
- `content/block/EnchantmentInfusionTableBlock.java`：使用原版 `Settings` 与 `NoteBlockInstrument`；新增接收 `Settings` 的构造方法、`MapCodec` 和 `getCodec()`；服务端 ticker 校验改为 `validateTicker`。
- `content/block/EnchantmentInfusionPedestalBlock.java`：同样补齐 `Settings` 构造方法和 `MapCodec`，更新设置及乐器类型。

两个方块的形状、硬度、抗爆性、工具要求和灌注亮度保持原值；无参数构造方法继续使用原来的默认设置。

### 方块交互与方块实体

- `api/block/AbstractInfusionBlock.java`：适配 1.21.1 拆分后的 `onUseWithItem` 与空手 `onUse`；保留主手交互处理和副手排除；物品自定义名称读取改用 `DataComponentTypes.CUSTOM_NAME`；更新寻路方法签名。
- `api/block/entity/AbstractInfusionTile.java`：单槽物品移出调用由 `removeStack()` 改为 `emptyStack()`。
- `api/block/entity/NameableSingleStackTile.java`：适配 `SingleStackInventory` 的 `getStack()`、`setStack(ItemStack)`，将 `decreaseStack` 转发至带更新通知的移出方法，确保扣减、清空和取出继续同步方块实体；NBT 读写和初始区块同步增加 `RegistryWrapper.WrapperLookup`；物品使用注册表感知的 `ItemStack` 编解码，空栈通过 `fromNbtOrEmpty` 读取，避免正常空槽产生无效物品日志，名称使用 `Text.Serialization`；通过 `readComponents`、`addComponents`、`removeFromCopiedStackNbt` 桥接自定义名称，配合新版掉落表的组件复制。
- `api/block/entity/InfusionInventory.java`：改为实现 `RecipeInput`，提供 `getStackInSlot`、`getSize`；原有公共库存访问、扣减、余物和脏标记方法保留，单槽扣减改用 `decreaseStack`/`emptyStack`。
- `content/block/entity/EnchantmentInfusionTableTile.java`：配方匹配结果改为 `RecipeEntry<InfusionRecipe>`，通过 `.value()` 获得实际配方；更新单槽移出、NBT 保存和读取签名。灌注计时、基座位置、粒子、声音及完成回调逻辑保持原设计。

`InfusionInventory` 不再是原版 `Inventory` 的子类型。这是必须明确的 API 变化：在当前 Yarn 映射下，同时实现 `Inventory` 与 `RecipeInput` 会让同名 `isEmpty()` 对应两个不同的 intermediary 方法，导致成品 JAR 重映射失败。依赖此模组 API 的其他模组若把该对象传给只接受 `Inventory` 的接口，需要同步适配。

### 配方与附魔运行时

- `api/recipe/InfusionRecipe.java`：`craft` 参数改为 `RegistryWrapper.WrapperLookup`；在附魔处理前复制中央物品，避免配方计算直接修改输入栈。
- `api/recipe/EnchantmentIngredient.java`：附魔改用 `RegistryEntry<Enchantment>`；附魔等级读取及附魔书展示使用组件 API；自定义原料序列化改为 `MapCodec` 和 `PacketCodec<RegistryByteBuf, ...>`。
- `content/recipe/EnchantmentInfusionRecipe.java`：移除配方记录内部的 ID，附魔改用注册表条目；通过 `ItemEnchantmentsComponent` 读取、移除前置附魔并设置输出等级；适配新的兼容性检查及 `getResult(WrapperLookup)`。
- `content/recipe/SimpleInfusionRecipe.java`：同样移除内部 ID 并更新预览方法；保留 `copy_nbt` 配置名称，使用物品组件变更复制中央物品的数据，并递归合并 `CUSTOM_DATA`，同时保留输出物品自身的默认组件。

配方 ID 由原版 `RecipeEntry` 持有。附魔灌注仍要求满足输入附魔和等级提升条件；`force` 仍用于绕过物品适用性与附魔冲突，不能绕过前置附魔或等级要求。简单灌注的 `copy_nbt` 默认仍为 `true`，关闭时不复制输入物品数据。

### JSON 与网络序列化

- `content/recipe/EnchantmentInfusionRecipeSerializer.java`：手写 JSON/字节缓冲区入口改为 `RecipeSerializer.codec()` 和 `packetCodec()`；附魔通过注册表感知的条目 codec 编解码；`input` 保持可选，`force` 默认 `false`；附魔等级使用 1.21 组件可表示的 `1..255` 范围。
- `content/recipe/SimpleInfusionRecipeSerializer.java`：使用原料、物品组件和注册表感知的 codec；`copy_nbt` 默认保持 `true`。
- `api/util/SerialUtil.java`：附魔查找改用动态注册表及 `RegistryEntry`；物品与原料 JSON 使用带注册表上下文的 `JsonOps`；网络数据改用 `RegistryByteBuf` 和原版 packet codec；保留 1 至 8 个基座原料限制，并在网络读取时检查数量。

为该版本编写数据包时，简单灌注的 `output` 应使用 1.21.1 `ItemStack` 结构，例如 `{"id":"minecraft:diamond","count":1}`，复杂物品数据使用 `components`。旧版大写 `Count` 和传统物品 NBT 结构不能直接视为新版本格式。此变更不承诺旧版存档或第三方旧版数据包无需升级即可直接使用。

### 数据生成

- `content/data/BlockLootTableProvider.java`：构造方法接收注册表查询 future。
- `content/data/EnglishLanguageProvider.java`、`content/data/ChineseLanguageProvider.java`：构造方法与翻译生成入口加入注册表查询参数；翻译内容保持不变。
- `content/data/RecipeProvider.java`：使用 `RecipeExporter` 和注册表查询 future，在生成时解析附魔条目；87 个灌注配方的材料、数量与等级保留。
- `content/data/EnchantmentInfusionRecipeBuilder.java`、`content/data/SimpleInfusionRecipeBuilder.java`：移除旧 `RecipeJsonProvider` 实现，直接向 `RecipeExporter` 提交实际配方对象，由统一 codec 生成 JSON；继续限制基座原料数量。
- `api/util/EIRecipeUtil.java`：辅助入口同步改用 `RegistryEntry<Enchantment>` 和 `RecipeExporter`。

`fabric/EnchantmentInfusionDataGenerator.java`、`content/data/ModelProvider.java`、`content/data/BlockTagProvider.java` 不需要修改；原有 provider 注册和模型/标签定义继续使用。

### 可选配方查看器

- `api/compat/emi/AbstractEMIInfusionRecipe.java`：构造入口接收 `RecipeEntry`，通过 `.id()` 获取稳定配方 ID，通过 `.value()` 读取内容，预览改用 `getResult`。
- `content/compat/emi/EMIEIRecipe.java`：同步构造方法类型。
- `api/compat/rei/AbstractREIInfusionDisplay.java`：输出预览改用 `getResult`。
- `content/compat/rei/REICompat.java`：配方列表中的 `RecipeEntry` 解包后创建显示项。

EMI/REI 的分类、工作台、圆形基座布局与槽位尺寸没有调整。编译兼容并不等于已经完成两个查看器的实机显示验收。

## 生成资源迁移

1.21.1 使用单数形式的数据目录。重新运行数据生成后，旧目录中的文件已由新目录替代，旧空目录已清理：

| 原目录 | 新目录 | 文件数 |
| --- | --- | --- |
| `src/main/generated/data/enchantment_infusion/recipes/` | `src/main/generated/data/enchantment_infusion/recipe/` | 89 |
| `src/main/generated/data/enchantment_infusion/advancements/` | `src/main/generated/data/enchantment_infusion/advancement/` | 2 |
| `src/main/generated/data/enchantment_infusion/loot_tables/` | `src/main/generated/data/enchantment_infusion/loot_table/` | 2 |
| `src/main/generated/data/minecraft/tags/blocks/` | `src/main/generated/data/minecraft/tags/block/` | 1 |

具体格式变化：

- 原版横扫之刃键由 `minecraft:sweeping` 更新为 `minecraft:sweeping_edge`，涉及 `recipe/sweeping/1.json`、`2.json`、`3.json` 中的目标与前置附魔；模组配方 ID 仍为 `enchantment_infusion:sweeping/1`、`/2`、`/3`，保留已有覆盖目标。
- 方块合成结果由 `result.item` 改为 `result.id`/`result.count`；默认 `show_notification=true` 可由 codec 省略。
- 灌注配方的默认 `force=false` 可由 codec 省略，语义不变。
- 两个方块掉落表的名称复制由旧 `copy_name` 改为 `copy_components`，包含 `minecraft:custom_name`。
- 生成器缓存 `src/main/generated/.cache/` 随版本、文件路径和内容哈希更新。

生成的语言、方块状态及物品模型资源与基线内容一致。全部 94 个数据 JSON 文件以及生成资源中的其他 JSON 均可解析。

## 验证状态

已确认：

- Java 源码编译通过，包括 EMI 与 REI 可选 API。
- `runDatagen` 完成，新的单数目录资源生成成功。
- 最后一轮 `test` 于 2026-10-06 17:51:41 UTC 运行：15 个测试通过（10 个配方测试、5 个方块实体测试），0 失败、0 跳过；`build`、`remapJar` 和 `remapSourcesJar` 完成，构建用时约 12 秒。此轮已包含名称组件桥接及空栈读取修正。
- 89 个默认配方与基线逐项语义比较通过；比较只归一化 1.21.1 的物品结果格式、默认值省略和原版横扫之刃名称变化。
- 旧复数数据目录已清理，生成 JSON 解析检查通过。

新增测试文件：

- `src/test/java/karashokleo/enchantment_infusion/content/recipe/InfusionRecipeTest.java`：覆盖书本/装备适用性、强制附魔边界、前置附魔消耗、附魔升级、组件及自定义数据复制、无序且按数量匹配的基座原料、JSON/网络往返和无效输入。
- `src/test/java/karashokleo/enchantment_infusion/api/block/entity/NameableSingleStackTileTest.java`：覆盖单槽物品与名称的保存/恢复、区块同步、自定义名称组件往返、空栈、各物品移出路径及基座余物写回。

## 实机验收结果

2026-10-06 17:54 UTC，在 Fabric Minecraft 1.21.1 客户端的真实单人世界中完成约定验收：模组方块实际存在，玩家交互触发并成功完成一条内置配方。

### 测试结构与操作

- 中央灌注台坐标：`(0, 100, 0)`。
- 八个基座坐标：`(0, 100, -3)`、`(2, 100, -2)`、`(3, 100, 0)`、`(2, 100, 2)`、`(0, 100, 3)`、`(-2, 100, 2)`、`(-3, 100, 0)`、`(-2, 100, -2)`，与中央台同一高度。
- 使用内置配方 `enchantment_infusion:efficiency/1`：中央物品为普通书；基座分别放入糖、铁镐和两个紫水晶碎片，其余基座为空。
- 测试准备命令仅用于放置方块和原料，没有用命令生成或替换最终附魔产物。
- 通过实际右键交互启动灌注，经过原设计的 100 tick 后得到效率 I 附魔书。
- 配方原料全部消耗；将产物从灌注台取回物品栏后，查看提示文本确认是效率 I 附魔书。

### 额外确认与证据

- 保存并重新进入世界后，放置的结构及物品栏中的已合成附魔书仍然保留。
- 此次最终游戏验证中没有新增无效物品解析错误日志。
- EMI 插件重载日志正常；该日志仅证明插件重载，不作为配方界面验收。

本次客户端截图文件名：

- `2026-10-06_19.53.36.png`：实际测试结构与原料布置。
- `2026-10-06_19.54.02.png`：灌注进行中。
- `2026-10-06_19.54.27.png`：灌注台上的产物。
- `2026-10-06_19.54.42.png`：取回物品后的效率 I 附魔书提示文本。

这些是本次运行时证据文件，不属于模组运行资源，也不会打包进成品 JAR。

### 未覆盖与环境限制

- EMI 的配方查看界面尚未实际验收；REI 未做运行时测试。两个查看器的源码均已通过对应版本 API 编译。
- 失败/中断、命名方块实际破坏后的掉落、含容器原料的现场余物流程，未作为额外游戏用例逐项验收。其中名称组件、物品保存/恢复、空栈及余物写回已有自动化测试覆盖；自动化测试不等同于这些具体游戏流程已测。
- 云端测试环境无法初始化 OpenAL 音频，未验证实际听觉效果；开发客户端的在线档案请求出现连接拒绝，单人世界及本次灌注流程可正常运行。

以上未测项目不是新增发布门槛。本次约定的“真实放置方块并至少成功完成一次灌注”验收已完成。
