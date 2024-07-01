package karashokleo.enchantment_infusion.content.compat.rei;

import karashokleo.enchantment_infusion.api.compat.rei.AbstractREIInfusionDisplay;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class REIEIDisplay extends AbstractREIInfusionDisplay
{
    public REIEIDisplay(InfusionRecipe recipe)
    {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return REICompat.EI;
    }
}
