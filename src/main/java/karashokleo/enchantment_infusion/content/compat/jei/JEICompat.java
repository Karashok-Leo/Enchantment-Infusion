package karashokleo.enchantment_infusion.content.compat.jei;

import karashokleo.enchantment_infusion.fabric.EnchantmentInfusion;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EIRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@JeiPlugin
public class JEICompat implements IModPlugin
{
    public static final RecipeType<JEIInfusionRecipe> INFUSION = new RecipeType<>(EIRecipes.INFUSION_ID, JEIInfusionRecipe.class);

    @Override
    public Identifier getPluginUid()
    {
        return EnchantmentInfusion.id("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        registration.addRecipeCategories(new JEIInfusionCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        var world = MinecraftClient.getInstance().world;
        if (world == null) return;
        registration.addRecipes(INFUSION, world.getRecipeManager().listAllOfType(EIRecipes.INFUSION_RECIPE_TYPE)
            .stream().map(recipe -> new JEIInfusionRecipe(recipe, world.getRegistryManager())).toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(EIBlocks.INFUSION_TABLE), INFUSION);
        registration.addRecipeCatalyst(new ItemStack(EIBlocks.INFUSION_PEDESTAL), INFUSION);
    }
}
