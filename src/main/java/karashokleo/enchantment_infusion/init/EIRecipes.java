package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipeSerializer;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipeSerializer;
import karashokleo.enchantment_infusion.fabric.EnchantmentInfusion;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EIRecipes
{
    public static final Identifier INFUSION_ID = EnchantmentInfusion.id("infusion");

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

    public static final CustomIngredientSerializer<EnchantmentIngredient> ENCHANTMENT_INGREDIENT_SERIALIZER = new EnchantmentIngredient.Serializer();

    public static void register()
    {
        Registry.register(Registries.RECIPE_TYPE, INFUSION_ID, INFUSION_RECIPE_TYPE);

        Registry.register(Registries.RECIPE_SERIALIZER, EnchantmentInfusion.id(EnchantmentInfusion.MOD_ID), EI_SERIALIZER);
        Registry.register(Registries.RECIPE_SERIALIZER, EnchantmentInfusion.id("simple_infusion"), SI_SERIALIZER);

        CustomIngredientSerializer.register(ENCHANTMENT_INGREDIENT_SERIALIZER);
    }
}
