package karashokleo.enchantment_infusion.api.compat.rei;

import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractREIInfusionDisplay implements Display
{
    private final EntryIngredient tableIngredient;
    private final List<EntryIngredient> pedestalIngredients;
    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;

    @SuppressWarnings("all")
    public AbstractREIInfusionDisplay(InfusionRecipe recipe)
    {
        this(
                EntryIngredients.ofIngredient(recipe.getTableIngredient()),
                EntryIngredients.ofIngredients(recipe.getPedestalIngredient()),
                Collections.singletonList(EntryIngredients.of(recipe.getOutput(BasicDisplay.registryAccess())))
        );
    }

    public AbstractREIInfusionDisplay(EntryIngredient tableIngredient, List<EntryIngredient> pedestalIngredients, List<EntryIngredient> output)
    {
        this.tableIngredient = tableIngredient;
        this.pedestalIngredients = pedestalIngredients;
        this.input = new ArrayList<>(pedestalIngredients);
        this.input.add(tableIngredient);
        this.output = output;
    }

    public EntryIngredient getTableIngredient()
    {
        return tableIngredient;
    }

    @Override
    public List<EntryIngredient> getInputEntries()
    {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries()
    {
        return output;
    }
}
