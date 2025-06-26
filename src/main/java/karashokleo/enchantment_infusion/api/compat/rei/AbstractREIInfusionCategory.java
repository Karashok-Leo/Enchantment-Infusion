package karashokleo.enchantment_infusion.api.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractREIInfusionCategory implements DisplayCategory<AbstractREIInfusionDisplay>
{
    private static final int INPUT_OFFSET = -28;
    private static final int OUTPUT_OFFSET = 58;
    private static final int RADIUS_OFFSET = -16;
    private static final int ARROW_OFFSET = 16;

    @Override
    public int getDisplayWidth(AbstractREIInfusionDisplay display)
    {
        return 160;
    }

    @Override
    public int getDisplayHeight()
    {
        return 96;
    }

    @Override
    public List<Widget> setupDisplay(AbstractREIInfusionDisplay display, Rectangle bounds)
    {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        int centerX = bounds.getCenterX();
        int centerY = bounds.getCenterY();
        int radius = Math.min(bounds.getWidth(), bounds.getHeight()) / 2 + RADIUS_OFFSET;
        List<EntryIngredient> ingredients = display.getPedestalIngredients();
        int size = ingredients.size();
        for (int i = 0; i < size; i++)
        {
            float rad = i * 1.0F / size * 2 * MathHelper.PI;
            int x = Math.round(centerX + INPUT_OFFSET - radius * MathHelper.sin(rad));
            int y = Math.round(centerY - radius * MathHelper.cos(rad));
            widgets.add(Widgets.createSlot(new Point(x - 9, y - 9)).entries(ingredients.get(i)).markInput());
        }
        widgets.add(Widgets.createSlot(new Point(centerX + INPUT_OFFSET - 9, centerY - 9)).entries(display.getTableIngredient()).markInput());
        widgets.add(Widgets.createArrow(new Point(centerX + ARROW_OFFSET, centerY - 9)));
        widgets.add(Widgets.createResultSlotBackground(new Point(centerX + OUTPUT_OFFSET - 9, centerY - 9)));
        widgets.add(Widgets.createSlot(new Point(centerX + OUTPUT_OFFSET - 9, centerY - 9)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }
}
