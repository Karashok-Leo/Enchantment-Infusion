package karashokleo.enchantment_infusion.api.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class AbstractEMIInfusionRecipe implements EmiRecipe
{
    private static final int INPUT_OFFSET = -29;
    private static final int OUTPUT_OFFSET = 47;
    private static final int RADIUS_OFFSET = -13;
    private static final int ARROW_OFFSET = 21;

    protected final Identifier id;
    protected final EmiIngredient tableIngredient;
    protected final List<EmiIngredient> pedestalIngredients;
    protected final List<EmiStack> output;

    @SuppressWarnings("all")
    public AbstractEMIInfusionRecipe(InfusionRecipe recipe)
    {
        this(
                recipe.getId(),
                recipe.getTableIngredient(),
                recipe.getPedestalIngredient(),
                recipe.getOutput(MinecraftClient.getInstance().world.getRegistryManager())
        );
    }

    public AbstractEMIInfusionRecipe(Identifier id, Ingredient tableIngredient, List<Ingredient> pedestalIngredients, ItemStack output)
    {
        this(
                id,
                EmiIngredient.of(tableIngredient),
                pedestalIngredients.stream().map(EmiIngredient::of).toList(),
                Collections.singletonList(EmiStack.of(output))
        );
    }

    public AbstractEMIInfusionRecipe(Identifier id, EmiIngredient tableIngredient, List<EmiIngredient> pedestalIngredients, List<EmiStack> output)
    {
        this.id = id;
        this.tableIngredient = tableIngredient;
        this.pedestalIngredients = pedestalIngredients;
        this.output = output;
    }

    @Override
    public @Nullable Identifier getId()
    {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs()
    {
        return pedestalIngredients;
    }

    @Override
    public List<EmiStack> getOutputs()
    {
        return output;
    }

    @Override
    public int getDisplayWidth()
    {
        return 138;
    }

    @Override
    public int getDisplayHeight()
    {
        return 84;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        int centerX = getDisplayWidth() / 2;
        int centerY = getDisplayHeight() / 2;
        int radius = Math.min(centerX, centerY) + RADIUS_OFFSET;
        for (int i = 0; i < pedestalIngredients.size(); i++)
        {
            float rad = i * 1.0F / pedestalIngredients.size() * 2 * MathHelper.PI;
            int x = Math.round(centerX + INPUT_OFFSET - radius * MathHelper.sin(rad));
            int y = Math.round(centerY - radius * MathHelper.cos(rad));
            widgets.addSlot(pedestalIngredients.get(i), x - 9, y - 9);
        }
        widgets.addSlot(tableIngredient, centerX - 9 + INPUT_OFFSET, centerY - 9);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, centerX - 9 + ARROW_OFFSET, centerY - 8);
        widgets.addSlot(output.get(0), centerX - 9 + OUTPUT_OFFSET, centerY - 13).large(true);
    }
}
