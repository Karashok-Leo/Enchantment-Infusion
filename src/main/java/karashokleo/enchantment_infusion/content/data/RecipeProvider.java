package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.api.util.EIRecipeUtil;
import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider
{
    /*
     * SOURCE_GEM            ---->   AMETHYST_SHARD
     * WATER_ESSENCE         ---->   WATER_BUCKET
     * AIR_ESSENCE           ---->   PHANTOM_MEMBRANE
     * FIRE_ESSENCE          ---->   FIRE_CHARGE
     * ABJURATION_ESSENCE    ---->   GOLDEN_APPLE
     * EARTH_ESSENCE         ---->   LEAVES
     * MANIPULATION_ESSENCE  ---->   CLOCK
     * WILDEN_SPIKE          ---->   SWEET_BERRIES
     * */

    private final CompletableFuture<HolderLookup.Provider> registriesFuture;

    public RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture)
    {
        super(output, registriesFuture);
        this.registriesFuture = registriesFuture;
    }

    private void add(Consumer<EnchantmentInfusionRecipeBuilder> consumer, ResourceKey<Enchantment> enchantment, int level, RecipeOutput exporter)
    {
        EIRecipeUtil.add(
            consumer,
            registriesFuture.join().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment),
            level,
            exporter,
            EnchantmentInfusion.id((enchantment.equals(Enchantments.SWEEPING_EDGE) ? "sweeping" : enchantment.location().getPath()) + "/" + level)
        );
    }

    @Override
    public void buildRecipes(RecipeOutput exporter)
    {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.DECORATIONS, EIBlocks.INFUSION_TABLE.get())
            .pattern("DRD")
            .pattern(" P ")
            .pattern("OOO")
            .define('D', Items.DIAMOND)
            .define('R', Items.REDSTONE_BLOCK)
            .define('P', EIBlocks.INFUSION_PEDESTAL.get())
            .define('O', Items.OBSIDIAN)
            .unlockedBy(getHasName(EIBlocks.INFUSION_PEDESTAL.get()), has(EIBlocks.INFUSION_PEDESTAL.get()))
            .save(exporter);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.DECORATIONS, EIBlocks.INFUSION_PEDESTAL.get(), 3)
            .pattern("DRD")
            .pattern(" C ")
            .pattern("OOO")
            .define('D', Items.DIAMOND)
            .define('R', Items.REDSTONE_BLOCK)
            .define('C', Items.CRYING_OBSIDIAN)
            .define('O', Items.OBSIDIAN)
            .unlockedBy(getHasName(Items.OBSIDIAN), has(Items.OBSIDIAN))
            .save(exporter);

        add(
            builder -> builder
                .withPedestalItem(4, Ingredient.of(ItemTags.FISHES))
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.AQUA_AFFINITY, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.SPIDER_EYE)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.BANE_OF_ARTHROPODS, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.SPIDER_EYE)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.BANE_OF_ARTHROPODS, 2, exporter
        );


        add(
            builder -> builder
                .withPedestalItem(4, Items.FERMENTED_SPIDER_EYE)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.BANE_OF_ARTHROPODS, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.FERMENTED_SPIDER_EYE)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.BANE_OF_ARTHROPODS, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.FERMENTED_SPIDER_EYE)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.BANE_OF_ARTHROPODS, 5, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.IRON_BLOCK)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(0, Items.LAPIS_BLOCK),
            Enchantments.BLAST_PROTECTION, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.IRON_BLOCK)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.BLAST_PROTECTION, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.DIAMOND)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.OBSIDIAN),
            Enchantments.BLAST_PROTECTION, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.DIAMOND)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.GOLD_BLOCK),
            Enchantments.BLAST_PROTECTION, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.WATER_BUCKET)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.DEPTH_STRIDER, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.WATER_BUCKET)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.PRISMARINE_SHARD),
            Enchantments.DEPTH_STRIDER, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.WATER_BUCKET)
                .withPedestalItem(3, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.NAUTILUS_SHELL),
            Enchantments.DEPTH_STRIDER, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.SUGAR)
                .withPedestalItem(1, Items.IRON_PICKAXE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.EFFICIENCY, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.REDSTONE_BLOCK)
                .withPedestalItem(1, Items.GOLDEN_PICKAXE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.EFFICIENCY, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.REDSTONE_BLOCK)
                .withPedestalItem(1, Items.GOLDEN_PICKAXE)
                .withPedestalItem(3, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.OBSIDIAN),
            Enchantments.EFFICIENCY, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.REDSTONE_BLOCK)
                .withPedestalItem(1, Items.DIAMOND_PICKAXE)
                .withPedestalItem(1, Items.IRON_SHOVEL)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.OBSIDIAN),
            Enchantments.EFFICIENCY, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.REDSTONE_BLOCK)
                .withPedestalItem(1, Items.DIAMOND_PICKAXE)
                .withPedestalItem(1, Items.DIAMOND_SHOVEL)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.EFFICIENCY, 5, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.SLIME_BALL)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.FEATHER_FALLING, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.SLIME_BALL)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.FEATHER_FALLING, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.SLIME_BALL)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.FEATHER_FALLING, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.FEATHER_FALLING, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.FIRE_CHARGE)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.FIRE_ASPECT, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.FIRE_CHARGE)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.FIRE_ASPECT, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.FIRE_CHARGE)
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.FIRE_PROTECTION, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.FIRE_CHARGE)
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.FIRE_PROTECTION, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.FIRE_CHARGE)
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(3, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.BLAZE_ROD),
            Enchantments.FIRE_PROTECTION, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.FIRE_CHARGE)
                .withPedestalItem(2, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.BLAZE_ROD),
            Enchantments.FIRE_PROTECTION, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.FIRE_CHARGE)
                .withPedestalItem(3, Items.GOLDEN_APPLE)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.BLAZE_ROD),
            Enchantments.FIRE_PROTECTION, 5, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.FIRE_CHARGE)
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.BLAZE_ROD),
            Enchantments.FLAME, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Ingredient.of(ItemTags.LEAVES))
                .withPedestalItem(1, Items.DIAMOND)
                .withPedestalItem(6, Items.AMETHYST_SHARD),
            Enchantments.FORTUNE, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Ingredient.of(ItemTags.LEAVES))
                .withPedestalItem(4, Items.DIAMOND)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.FORTUNE, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Ingredient.of(ItemTags.LEAVES))
                .withPedestalItem(1, Items.DIAMOND_BLOCK)
                .withPedestalItem(2, Items.LAPIS_BLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.FORTUNE, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(7, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.DIAMOND_BLOCK),
            Enchantments.INFINITY, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.CLOCK)
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.KNOCKBACK, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.CLOCK)
                .withPedestalItem(2, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.KNOCKBACK, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Ingredient.of(ItemTags.LEAVES))
                .withPedestalItem(1, Items.EMERALD)
                .withPedestalItem(6, Items.AMETHYST_SHARD),
            Enchantments.LOOTING, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Ingredient.of(ItemTags.LEAVES))
                .withPedestalItem(4, Items.EMERALD)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.LOOTING, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Ingredient.of(ItemTags.LEAVES))
                .withPedestalItem(1, Items.EMERALD_BLOCK)
                .withPedestalItem(2, Items.LAPIS_BLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.LOOTING, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.CLOCK)
                .withPedestalItem(5, Items.AMETHYST_SHARD),
            Enchantments.MULTISHOT, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.CLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.PIERCING, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.CLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.PIERCING, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.CLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.PIERCING, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.CLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.PIERCING, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.DIAMOND)
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.AMETHYST_SHARD),
            Enchantments.POWER, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.DIAMOND)
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.POWER, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.DIAMOND)
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.POWER, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.DIAMOND)
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.POWER, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(5, Items.DIAMOND)
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.POWER, 5, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.PROJECTILE_PROTECTION, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.PROJECTILE_PROTECTION, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(3, Items.AMETHYST_SHARD),
            Enchantments.PROJECTILE_PROTECTION, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.PROJECTILE_PROTECTION, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(3, Items.GOLDEN_APPLE)
                .withPedestalItem(1, Items.AMETHYST_SHARD),
            Enchantments.PROJECTILE_PROTECTION, 5, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.CLOCK)
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(1, Items.LAPIS_BLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.PROTECTION, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.CLOCK)
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.PROTECTION, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.CLOCK)
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(3, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.PROTECTION, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.CLOCK)
                .withPedestalItem(2, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.PROTECTION, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.CLOCK)
                .withPedestalItem(3, Items.GOLDEN_APPLE)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.DIAMOND_BLOCK),
            Enchantments.PROTECTION, 5, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.PUNCH, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.PUNCH, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.QUICK_CHARGE, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.QUICK_CHARGE, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.PHANTOM_MEMBRANE)
                .withPedestalItem(2, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.QUICK_CHARGE, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.WATER_BUCKET)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.RESPIRATION, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.WATER_BUCKET)
                .withPedestalItem(4, Items.AMETHYST_SHARD),
            Enchantments.RESPIRATION, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(6, Items.WATER_BUCKET)
                .withPedestalItem(2, Items.AMETHYST_SHARD),
            Enchantments.RESPIRATION, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.QUARTZ_BLOCK)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.SHARPNESS, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.QUARTZ_BLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.SHARPNESS, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.QUARTZ_BLOCK)
                .withPedestalItem(1, Items.DIAMOND)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.SHARPNESS, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.QUARTZ_BLOCK)
                .withPedestalItem(2, Items.DIAMOND)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.SHARPNESS, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.QUARTZ_BLOCK)
                .withPedestalItem(2, Items.DIAMOND)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.SHARPNESS, 5, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.EMERALD)
                .withPedestalItem(2, Items.DIAMOND)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.SILK_TOUCH, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.BONE_BLOCK)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.SMITE, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.BONE_BLOCK)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.SMITE, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.BONE_BLOCK)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.SMITE, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.BONE_BLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.SMITE, 4, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(4, Items.BONE_BLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.SMITE, 5, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.CLOCK)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.SWEEPING_EDGE, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.CLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.SWEEPING_EDGE, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.CLOCK)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(3, Items.LAPIS_BLOCK),
            Enchantments.SWEEPING_EDGE, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.SWEET_BERRIES)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.THORNS, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(2, Items.SWEET_BERRIES)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.THORNS, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(3, Items.SWEET_BERRIES)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(3, Items.LAPIS_BLOCK),
            Enchantments.THORNS, 3, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(1, Items.DIAMOND)
                .withPedestalItem(1, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.UNBREAKING, 1, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(2, Items.DIAMOND)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(1, Items.LAPIS_BLOCK),
            Enchantments.UNBREAKING, 2, exporter
        );

        add(
            builder -> builder
                .withPedestalItem(1, Items.GOLDEN_APPLE)
                .withPedestalItem(3, Items.DIAMOND)
                .withPedestalItem(2, Items.AMETHYST_SHARD)
                .withPedestalItem(2, Items.LAPIS_BLOCK),
            Enchantments.UNBREAKING, 3, exporter
        );
    }
}
