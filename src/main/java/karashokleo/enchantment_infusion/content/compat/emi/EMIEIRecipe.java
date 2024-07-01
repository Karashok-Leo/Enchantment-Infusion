package karashokleo.enchantment_infusion.content.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import karashokleo.enchantment_infusion.api.compat.emi.AbstractEMIInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;

public class EMIEIRecipe extends AbstractEMIInfusionRecipe
{
    public EMIEIRecipe(EnchantmentInfusionRecipe recipe)
    {
        super(recipe);
    }

    @Override
    public EmiRecipeCategory getCategory()
    {
        return EMICompat.EI_CATEGORY;
    }
}
