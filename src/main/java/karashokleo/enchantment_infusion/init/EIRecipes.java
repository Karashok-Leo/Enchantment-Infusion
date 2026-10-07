package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipeSerializer;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipeSerializer;
import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class EIRecipes
{
    public static final ResourceLocation INFUSION_ID = EnchantmentInfusion.id("infusion");

    public static final RecipeType<InfusionRecipe> INFUSION_RECIPE_TYPE = new RecipeType<>()
    {
        @Override
        public String toString()
        {
            return INFUSION_ID.getPath();
        }
    };

    public static final RecipeSerializer<EnchantmentInfusionRecipe> EI_SERIALIZER = new EnchantmentInfusionRecipeSerializer();
    public static final RecipeSerializer<SimpleInfusionRecipe> SI_SERIALIZER = new SimpleInfusionRecipeSerializer();

    public static final IngredientType<EnchantmentIngredient> ENCHANTMENT_INGREDIENT_SERIALIZER = new IngredientType<>(EnchantmentIngredient.CODEC, EnchantmentIngredient.PACKET_CODEC);

    public static void register(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(Registries.RECIPE_TYPE))
        {
            Registry.register(BuiltInRegistries.RECIPE_TYPE, INFUSION_ID, INFUSION_RECIPE_TYPE);
        }
        if (event.getRegistryKey().equals(Registries.RECIPE_SERIALIZER))
        {
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, EnchantmentInfusion.id(EnchantmentInfusion.MOD_ID), EI_SERIALIZER);
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, EnchantmentInfusion.id("simple_infusion"), SI_SERIALIZER);
        }
        event.register(NeoForgeRegistries.Keys.INGREDIENT_TYPES, helper -> helper.register(EnchantmentInfusion.id("enchantment"), ENCHANTMENT_INGREDIENT_SERIALIZER));
    }
}
