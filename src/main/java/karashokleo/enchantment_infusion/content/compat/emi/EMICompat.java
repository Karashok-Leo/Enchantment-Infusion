package karashokleo.enchantment_infusion.content.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EIRecipes;
import karashokleo.enchantment_infusion.init.EITexts;
import net.minecraft.text.Text;

@dev.emi.emi.api.EmiEntrypoint
public class EMICompat implements EmiPlugin
{
    public static EmiRecipeCategory getCategory()
    {
        return CategoryHolder.EI_CATEGORY;
    }

    private static class CategoryHolder
    {
        private static final EmiRecipeCategory EI_CATEGORY = new EmiRecipeCategory(EIRecipes.INFUSION_ID, EmiStack.of(EIBlocks.INFUSION_TABLE.get()))
        {
            @Override
            public Text getName()
            {
                return EITexts.CATEGORY.get();
            }
        };
    }

    @Override
    public void register(EmiRegistry registry)
    {
        registry.addCategory(getCategory());
        registry.addWorkstation(getCategory(), EmiStack.of(EIBlocks.INFUSION_TABLE.get()));
        registry.addWorkstation(getCategory(), EmiStack.of(EIBlocks.INFUSION_PEDESTAL.get()));
        registry.getRecipeManager().listAllOfType(EIRecipes.INFUSION_RECIPE_TYPE.get()).forEach(recipe -> registry.addRecipe(new EMIEIRecipe(recipe)));
    }
}
