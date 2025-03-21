# Enchantment Infusion

## 🪄Introduction

This mod adds a directional enchanting method, inspired by the Enchanting Apparatus recipes from [Ars Nouveau](https://www.curseforge.com/minecraft/mc-mods/ars-nouveau).

## 🪚Recipe

### Enchantment Infusion

This mod adds a recipe type (`enchantment_infusion:enchantment_infusion`). The JSON file for a single recipe is as follows:

```
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
| `ingredients` | Required | the ingredients of the recipe, is up to 8, the order does not matter (this is a shapeless recipe)               |
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

Since mod version `1.2.0`, a new recipe type (`enchantment_infusion:simple_infusion`) has been added. The JSON file for a single recipe is as follows:

```
{
  "type": "enchantment_infusion:simple_infusion",
  "ingredients": [
    {
      "item": "minecraft:redstone"
    },
    {
      "tag": "spell-dimension:essence/0"
    }
  ],
  "input": {
    "item": "minecraft:leather_boots",
    "min_level": 4
  },
  "output": {
    "Count": 1,
    "id": "artifacts:running_shoes"
  },
  "copy_nbt": false
}
```

| Field       | Required | Description                                                                                                            |
| ----------- | -------- | ---------------------------------------------------------------------------------------------------------------------- |
| type        | Required | specify the recipe type as simple infusion                                                                             |
| ingredients | Required | the ingredients of the recipe, is up to 8, the order does not matter (this is a shapeless recipe)                      |
| input       | Required | input ingredient for the central infusion table                                                                        |
| output      | Required | output item                                                                                                            |
| copy_nbt    | Optional | whether or not to copy the NBT of the input item in the central infusion table to the output item, defaults to `true`. |

## 🧩Compatibility

This mod has built-in REI and EMI compatible plug-ins, which means you can use either to view all recipes in this mod.

## ✉️Feedback

This mod has not been fully tested yet, so it is still in Beta.

If there are any bugs or suggestions, please provide feedback to the issue page.