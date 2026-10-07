# Enchantment Infusion：Minecraft 1.20.1 Forge 移植说明

基线为 Fabric 1.20.1 `master` 提交 `2cec737573188c7ed28fab0c60bc43bfa1d959bc`。本分支面向 Minecraft 1.20.1 Forge，保留模组版本 1.3.4。不增加玩法，不调整默认配方、方块属性、基座位置或灌注时间。

## 依赖和构建

- Minecraft：1.20.1
- Forge：47.4.22（要求 47.4 系列）
- 游戏、数据生成和编译目标：Java 17；Gradle 运行环境：Java 21
- Gradle：8.14.3；Architectury Loom：1.10.455
- Yarn：保留 `1.20.1+build.10`
- EMI：保留 `1.1.22+1.20.1`，切换为 Forge 构件
- REI：保留 `12.1.785`，切换为 Forge API 构件

使用 Architectury Loom 仅为了在 Forge 上继续使用原有 Yarn 名称，避免对所有业务代码进行 Mojang 映射重命名。模组本身不依赖 Architectury API。REI 的编译期传递依赖不构成本模组的强制运行依赖。

官方参考：[Forge 下载页](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)、[Architectury Loom 对 Forge 与 Yarn 的支持](https://docs.architectury.dev/loom/introduction/)。

构建命令：

```sh
./gradlew runData
./gradlew build
python3 tools/verify_master_data.py --baseline 2cec737573188c7ed28fab0c60bc43bfa1d959bc
./gradlew runClient
```

生成的成品文件名称包含 `forge-1.20.1`，以免与 Fabric 或 NeoForge 版本混淆。此分支只能安装到对应 Forge 游戏环境，不能当作 Fabric 模组加载。

## 相对 master 的源码差异

### 加载器入口和注册

- `fabric/` 入口迁移到 `forge/`，相关引用同步更新。
- 主入口改用 `@Mod` 与 Forge MOD 事件总线。
- `EIBlocks`、`EIItems`、`EIRecipes` 在相应 `RegisterEvent` 注册。保留所有原有注册 ID 及公共静态字段。
- 方块实体构造器改为原版 `BlockEntityType.Builder`；方块和物品设置改用原版 `Settings`，数值不变。
- 创造模式功能方块栏使用 `BuildCreativeModeTabContentsEvent`，仍排列在附魔台后。
- Fabric 元数据替换为 `META-INF/mods.toml`，新增原版资源包描述。

### 客户端

- 渲染层设置改在 `FMLClientSetupEvent` 执行。
- 方块实体渲染器改由 `EntityRenderersEvent.RegisterRenderers` 注册。
- 保留两个渲染器高度（1.3 和 0.85）、模型、贴图、粒子和原有业务渲染代码。
- 客户端入口限制在 `Dist.CLIENT`，服务端不主动加载客户端渲染类。

### 自定义原料与回调

- `EnchantmentIngredient` 保留 record、`enchantment()`、`min_level()`、匹配规则及原有 JSON/网络字段。
- Forge 自定义原料需要继承 `AbstractIngredient`，因此 `toVanilla()` 返回一个薄适配对象；不再实现 Fabric `CustomIngredient` 接口。
- `Serializer` 实现 Forge `IIngredientSerializer`，通过 `CraftingHelper` 注册；原有用于本模组配方 `input` 字段的 `read`/`write` 方法继续保留。
- 作为普通 Minecraft 原料单独序列化时，Forge 使用 `type: "enchantment_infusion:enchantment"` 标识自定义原料。本模组默认配方的 `input` 字段结构保持原样。
- `InfusionCompleteCallback.EVENT` 替换为不依赖 Fabric 的轻量回调容器，保留 `register`、`invoker`、回调参数和注册顺序。其类型不再是 Fabric `Event`；依赖 Fabric 专有事件阶段功能的其他模组需要自行适配。

### 可选配方查看器

- EMI 增加 Forge 使用的 `@EmiEntrypoint`。
- REI 增加 Forge 使用的 `@REIPluginClient`。
- 两种查看器继续保留原有分类、工作台、显示布局和配方遍历代码，均为可选集成。

### 数据生成和验证设施

- Fabric 数据生成入口替换为 Forge `GatherDataEvent`。
- 配方、语言、模型、标签和掉落表 provider 改用 Forge 或原版等效接口。
- 配方使用本版本原版 provider；掉落表使用原版构建器与数据写出接口，避免新增原版通用掉落 provider 自动附加的 `random_sequence` 字段，保留原有生成内容。
- 加入独立 `validation` source set 的 10 项配方回归断言，由真实 Forge 数据生成生命周期运行，测试代码不打包进成品。另有 1 项 JUnit 回调顺序测试，以及严格资源比较脚本。比较脚本要求 87 个附魔灌注配方、2 个方块合成配方，以及模型、语言、掉落、进度、标签和贴图与基线一致。唯一归一化是标签中省略的 `replace` 与原先显式 `false` 等价（原版 codec 的默认值）；不会通过删掉配方或宽松忽略字段掩盖差异。
- CI 使用 Java 21 运行 Gradle，安装并显式使用 Java 17 编译及运行游戏工具，执行构建、数据生成和资源比较。

## 保持不变的玩法代码

中央灌注台及八个基座坐标算法、100 tick 灌注时长、材料消耗及余物、方块交互、NBT 存档、附魔适用性/冲突/前置等级判断、`force`、`copy_nbt` 和两类配方序列化业务逻辑均以 master 为准。Minecraft 版本未变，因此没有采用 1.21 的组件、动态附魔注册表或单数资源目录格式。

## 验证状态

2026-10-07 UTC 已完成：

- `clean runData` 成功；真实 Forge 47.4.22 生命周期内 10 项配方回归检查通过，报告为 `passed: 10, failed: 0`。
- `build`、`remapJar`、`remapSourcesJar` 成功；1 项 JUnit 回调顺序/重复调用测试通过，0 失败、0 跳过。
- 数据生成后，107 个 JSON 和 6 个二进制资源的比较通过。87 个附魔灌注配方及 2 个合成配方完整保留，唯一格式归一化为标签省略 `replace:false`。
- 成品 JAR 不含 validation/JUnit 测试类，不含生成器 `.cache` 文件。

回归断言覆盖：书本与适用装备、前置等级、升级、强制附魔边界、无序基座原料匹配及数量、自定义原料 Forge 适配、JSON 与网络往返、无效原料列表、简单灌注 NBT 复制/关闭复制及输出模板不变。

### 真实游戏验收

使用单独创建的创造模式超平坦测试世界，实际启动 Forge 1.20.1 客户端并完成以下流程：

1. 中央台位于 `(0,100,0)`；八个基座分别位于 `(0,100,-3)`、`(2,100,-2)`、`(3,100,0)`、`(2,100,2)`、`(0,100,3)`、`(-2,100,2)`、`(-3,100,0)`、`(-2,100,-2)`，同一高度。
2. 放入糖、铁镐、两个紫水晶碎片；其余四个基座为空。玩家持有普通书。
3. 玩家真实右键中央台，触发原有 100 tick 灌注流程，完成内置 `enchantment_infusion:efficiency/1` 配方。
4. 四份基座材料全部消耗，中央台出现附魔书。取回物品后，真实物品栏提示确认 `Efficiency I`。
5. 保存并重新进入世界，结构及效率 I 附魔书仍保留。
6. 打开 EMI 配方查看界面，确认 Enchantment Infusion 分类、该配方的原料布局和效率 I 输出提示。

准备命令只用于放置方块、提供原材料和普通书、设置测试场景及相机。没有使用命令生成、替换或修改最终附魔产物。

截图文件（游戏客户端原始 F2 截图，文件名使用客户端本地时钟）：

- `2026-10-06_19.37.23.png`：Forge 版本、坐标和实际结构。
- `2026-10-06_19.37.24.png`：中央台、八基座和材料总览。
- `2026-10-06_19.37.59.png`：已启动灌注，普通书与灌注状态方块。
- `2026-10-06_19.38.08.png`：灌注产物及空基座。
- `2026-10-06_19.39.09.png`：取回产物的效率 I 提示。
- `2026-10-06_19.39.37.png`：EMI 配方页面与效率 I 输出。
- `2026-10-06_19.41.31.png`：重进存档后的效率 I 提示。

### 覆盖范围与已知环境限制

- REI 已通过 Forge API 编译，但未安装到此次运行环境进行界面验收。
- EMI 的本模组配方页面已实际验证；EMI 开发模式同时显示 160 条其自身 `emi:brewing/...` 合成酿造配方 ID 的检查提示，并非本模组灌注配方加载失败。
- 云端环境无可用音频设备，未验证听觉效果；开发客户端的 Mojang 在线公钥/档案请求出现网络错误，未影响单人游戏、灌注或存档重载。
- 未逐项实机测试专用服务器、失败中断、所有 87 条配方、命名方块掉落或容器余物。上述范围不应视为已全面验收。
