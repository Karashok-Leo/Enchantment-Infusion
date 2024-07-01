package karashokleo.enchantment_infusion.api.compat.rei;

import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Collections;
import java.util.List;

public abstract class AbstractREIInfusionDisplay implements Display
{
    private final EntryIngredient tableIngredient;
    private final List<EntryIngredient> pedestalIngredients;
    private final List<EntryIngredient> output;

    @SuppressWarnings("all")
    public AbstractREIInfusionDisplay(InfusionRecipe recipe)
    {
        this(
                EntryIngredients.ofIngredient(recipe.getTableIngredient()),
                EntryIngredients.ofIngredients(recipe.getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.getOutput(BasicDisplay.registryAccess())))
        );
    }

    public AbstractREIInfusionDisplay(EntryIngredient tableIngredient, List<EntryIngredient> pedestalIngredients, List<EntryIngredient> output)
    {
        this.tableIngredient = tableIngredient;
        this.pedestalIngredients = pedestalIngredients;
        this.output = output;
    }

    public EntryIngredient getTableIngredient()
    {
        return tableIngredient;
    }

    @Override
    public List<EntryIngredient> getInputEntries()
    {
        return pedestalIngredients;
    }

    @Override
    public List<EntryIngredient> getOutputEntries()
    {
        return output;
    }
}
