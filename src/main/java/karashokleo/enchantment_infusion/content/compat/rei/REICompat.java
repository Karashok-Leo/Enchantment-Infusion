package karashokleo.enchantment_infusion.content.compat.rei;

import karashokleo.enchantment_infusion.api.compat.rei.AbstractREIInfusionDisplay;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EIRecipes;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class REICompat implements REIClientPlugin
{
    public static final CategoryIdentifier<AbstractREIInfusionDisplay> EI = CategoryIdentifier.of(EIRecipes.getId());

    @Override
    public void registerCategories(CategoryRegistry registry)
    {
        registry.add(new REIEICategory());
        registry.addWorkstations(
                EI,
                EntryStacks.of(EIBlocks.INFUSION_TABLE),
                EntryStacks.of(EIBlocks.INFUSION_PEDESTAL)
        );
    }

    @Override
    public void registerDisplays(DisplayRegistry registry)
    {
        registry.getRecipeManager().listAllOfType(EIRecipes.EI_TYPE).forEach(recipe -> registry.add(new REIEIDisplay(recipe)));
    }
}
