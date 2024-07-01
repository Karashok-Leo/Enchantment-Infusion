package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.fabric.EnchantmentInfusion;
import karashokleo.enchantment_infusion.content.recipe.*;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EIRecipes
{
    public static final String NAME = EnchantmentInfusion.MOD_ID;
    public static final RecipeSerializer<EnchantmentInfusionRecipe> EI_SERIALIZER = new EnchantmentInfusionRecipeSerializer();
    public static final RecipeType<EnchantmentInfusionRecipe> EI_TYPE = new RecipeType<>()
    {
        @Override
        public String toString()
        {
            return NAME;
        }
    };
    public static final CustomIngredientSerializer<EnchantmentIngredient> ENCHANTMENT_INGREDIENT_SERIALIZER = new EnchantmentIngredient.Serializer();

    public static void register()
    {
        Registry.register(Registries.RECIPE_SERIALIZER, getId(), EI_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, getId(), EI_TYPE);
        CustomIngredientSerializer.register(ENCHANTMENT_INGREDIENT_SERIALIZER);
    }

    public static Identifier getId()
    {
        return EnchantmentInfusion.id(NAME);
    }
}
