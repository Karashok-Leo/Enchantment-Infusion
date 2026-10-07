package karashokleo.enchantment_infusion.content.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import karashokleo.enchantment_infusion.api.compat.emi.AbstractEMIInfusionRecipe;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class EMIEIRecipe extends AbstractEMIInfusionRecipe
{
    public EMIEIRecipe(RecipeHolder<? extends InfusionRecipe> recipe)
    {
        super(recipe);
    }

    @Override
    public EmiRecipeCategory getCategory()
    {
        return EMICompat.EI_CATEGORY;
    }
}
