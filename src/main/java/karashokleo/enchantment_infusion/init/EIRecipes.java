package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipeSerializer;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipeSerializer;
import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
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

    public static final EnchantmentIngredient.Serializer ENCHANTMENT_INGREDIENT_SERIALIZER = new EnchantmentIngredient.Serializer();

    public static void register(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(net.minecraft.registry.RegistryKeys.RECIPE_TYPE))
            event.register(net.minecraft.registry.RegistryKeys.RECIPE_TYPE, INFUSION_ID, () -> INFUSION_RECIPE_TYPE);
        if (!event.getRegistryKey().equals(net.minecraft.registry.RegistryKeys.RECIPE_SERIALIZER)) return;

        event.register(net.minecraft.registry.RegistryKeys.RECIPE_SERIALIZER, EnchantmentInfusion.id(EnchantmentInfusion.MOD_ID), () -> EI_SERIALIZER);
        event.register(net.minecraft.registry.RegistryKeys.RECIPE_SERIALIZER, EnchantmentInfusion.id("simple_infusion"), () -> SI_SERIALIZER);

        CraftingHelper.register(EnchantmentInfusion.id("enchantment"), ENCHANTMENT_INGREDIENT_SERIALIZER);
    }
}
