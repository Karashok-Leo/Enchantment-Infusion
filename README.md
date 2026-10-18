# Enchantment Infusion

## 🪄Introduction

This mod adds a directional enchanting method, inspired by the Enchanting Apparatus recipes from [Ars Nouveau](https://www.curseforge.com/minecraft/mc-mods/ars-nouveau).

### Versions and Installation

| Minecraft | Loader | Java | Additional mod dependencies |
| --------- | ------ | ---- | --------------------------- |
| 1.20.1 | Fabric | 17 | Fabric API |
| 1.20.1 | Forge | 17 | None |
| 1.21.1 | Fabric | 21 | Fabric API |
| 1.21.1 | NeoForge | 21 | None |

Choose the mod file matching your Minecraft version and loader. For multiplayer, install the mod on both the client and server.

### How to enchant ?

Two new blocks have been added to: the Enchantment Infusion Table and the Enchantment Infusion Pedestal. Use item to right-click them to put it in, and empty-handed right-click to remove item from them.

You need to place an enchantment infusion table (centre position) and enchantment infusion pedestals (surrounding position) like the picture below, then place the corresponding items on the pedestals according to the recipe (the order doesn't matter), and finally place the item you want to enchant on the infusion table in the centre, and wait for seconds for it to finish enchanting.

![image](assets/enchantment-infusion/machine.png)

### Crafting Recipes

![image](assets/enchantment-infusion/recipe.png)

## 🪚Recipe

When adding or changing recipes through a data pack, use the directory for your Minecraft version:

- 1.20.1: `data/<namespace>/recipes/<recipe_name>.json`
- 1.21.1: `data/<namespace>/recipe/<recipe_name>.json`

The enchantment infusion recipe below works in both versions. Simple infusion uses a different output item format in each version.

### Enchantment Infusion

This mod adds a recipe type (`enchantment_infusion:enchantment_infusion`). The JSON file for a single recipe is as follows:

```json
{
  "type": "enchantment_infusion:enchantment_infusion",
  "enchantment": "minecraft:efficiency",
  "force": false,
  "ingredients": [
    {
      "item": "minecraft:redstone_block"
    },
    {
      "item": "minecraft:redstone_block"
    },
    {
      "item": "minecraft:diamond_pickaxe"
    },
    {
      "item": "minecraft:diamond_shovel"
    },
    {
      "item": "minecraft:amethyst_shard"
    },
    {
      "item": "minecraft:amethyst_shard"
    },
    {
      "item": "minecraft:lapis_block"
    },
    {
      "item": "minecraft:lapis_block"
    }
  ],
  "input": {
    "enchantment": "minecraft:efficiency",
    "min_level": 4
  },
  "level": 5
}
```

| Field         | Required | Description                                                                                                     |
| ------------- | -------- | --------------------------------------------------------------------------------------------------------------- |
| `type`        | Required | specify the recipe type as enchantment infusion                                                                 |
| `enchantment` | Required | the identifier of the target enchantment                                                                        |
| `level`       | Required | the level of the target enchantment                                                                             |
| `ingredients` | Required | 1–8 recipe ingredients; the order does not matter (this is a shapeless recipe)               |
| `input`       | Optional | the existing enchantment needed, will be consumed when infused                                                  |
| `force`       | Optional | whether to ignore enchantment compatibility and force a match for the item to be enchanted, defaults to `false` |

### Default Recipes

This mod adds some default recipes that are derived from Ars Nouveau, but use vanilla items instead of those in Ars Nouveau:

| Original Item        | Replacement      |
| -------------------- | ---------------- |
| Source Gem           | Amethyst Shard   |
| Water Essence        | Water Bucket     |
| Air Essence          | Phantom Membrane |
| Fire Essence         | Fire Charge      |
| Abjuration Essence   | Golden Apple     |
| Earth Essence        | Leaves           |
| Manipulation Essence | Clock            |
| Wilden Spike         | Sweet Berries    |

### Simple Infusion

Simple infusion (`enchantment_infusion:simple_infusion`) uses the pedestal ingredients to turn the central item into an output item. This custom recipe example uses only vanilla items and targets Minecraft 1.20.1 (Fabric / Forge):

```json
{
  "type": "enchantment_infusion:simple_infusion",
  "ingredients": [
    {
      "item": "minecraft:redstone"
    },
    {
      "item": "minecraft:iron_ingot"
    }
  ],
  "input": {
    "item": "minecraft:leather_boots"
  },
  "output": {
    "Count": 1,
    "id": "minecraft:iron_boots"
  },
  "copy_nbt": false
}
```

For Minecraft 1.21.1 (Fabric / NeoForge), replace `output` with the following object and leave the other fields unchanged. The quantity field is lowercase `count`, and additional item data uses `components` instead of the old `Count` / `tag` format:

```json
{
  "id": "minecraft:iron_boots",
  "count": 1
}
```

| Field       | Required | Description                                                                                                            |
| ----------- | -------- | ---------------------------------------------------------------------------------------------------------------------- |
| type        | Required | specify the recipe type as simple infusion                                                                             |
| ingredients | Required | 1–8 recipe ingredients; the order does not matter (this is a shapeless recipe)                      |
| input       | Required | input ingredient for the central infusion table                                                                        |
| output      | Required | output item                                                                                                            |
| copy_nbt    | Optional | whether to copy input item data, defaults to `true`; copies NBT in 1.20.1, or component changes and merged custom data in 1.21.1 |

## 🧩Compatibility

All versions listed above include optional JEI, REI and EMI integrations for viewing enchantment infusion and simple infusion recipes. Install a viewer version matching your Minecraft version and loader.

## ✉️Feedback

Report bugs or suggestions on [GitHub Issues](https://github.com/Karashok-Leo/Enchantment-Infusion/issues), including your Minecraft version, loader and mod version.
