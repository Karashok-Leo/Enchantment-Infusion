package karashokleo.enchantment_infusion.content.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EIRecipes;
import karashokleo.enchantment_infusion.init.EITexts;
import net.minecraft.text.Text;

public class EMICompat implements EmiPlugin
{
    public static final EmiRecipeCategory EI_CATEGORY = new EmiRecipeCategory(EIRecipes.INFUSION_ID, EmiStack.of(EIBlocks.INFUSION_TABLE))
    {
        @Override
        public Text getName()
        {
            return EITexts.CATEGORY.get();
        }
    };

    @Override
    public void register(EmiRegistry registry)
    {
        registry.addCategory(EI_CATEGORY);
        registry.addWorkstation(EI_CATEGORY, EmiStack.of(EIBlocks.INFUSION_TABLE));
        registry.addWorkstation(EI_CATEGORY, EmiStack.of(EIBlocks.INFUSION_PEDESTAL));
        registry.getRecipeManager().listAllOfType(EIRecipes.INFUSION_RECIPE_TYPE).forEach(recipe -> registry.addRecipe(new EMIEIRecipe(recipe)));
    }
}
