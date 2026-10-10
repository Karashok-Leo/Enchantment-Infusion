package karashokleo.enchantment_infusion.content.compat.jei;

import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EIRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Discovered by JEI only; common mod initialization never references this class. */
@JeiPlugin
public class JEICompat implements IModPlugin
{
    public static final RecipeType<RecipeHolder<InfusionRecipe>> EI = RecipeType.createRecipeHolderType(EIRecipes.INFUSION_ID);

    @Override
    public ResourceLocation getPluginUid()
    {
        return ResourceLocation.fromNamespaceAndPath("enchantment_infusion", "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        registration.addRecipeCategories(new JEIInfusionCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        var level = Minecraft.getInstance().level;
        if (level != null)
        {
            registration.addRecipes(EI, level.getRecipeManager().getAllRecipesFor(EIRecipes.INFUSION_RECIPE_TYPE.get()));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(EIBlocks.INFUSION_TABLE.get()), EI);
        registration.addRecipeCatalyst(new ItemStack(EIBlocks.INFUSION_PEDESTAL.get()), EI);
    }
}
