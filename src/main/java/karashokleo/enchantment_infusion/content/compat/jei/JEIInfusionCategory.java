package karashokleo.enchantment_infusion.content.compat.jei;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EITexts;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class JEIInfusionCategory implements IRecipeCategory<JEIInfusionRecipe>
{
    private final IDrawable icon;
    private final IDrawable arrow;

    public JEIInfusionCategory(IGuiHelper gui)
    {
        icon = gui.createDrawableItemStack(new ItemStack(EIBlocks.INFUSION_TABLE));
        arrow = gui.getRecipeArrow();
    }

    @Override
    public RecipeType<JEIInfusionRecipe> getRecipeType() { return JEICompat.INFUSION; }

    @Override
    public Text getTitle() { return EITexts.CATEGORY.get(); }

    @Override
    public IDrawable getIcon() { return icon; }

    @Override
    public int getWidth() { return 138; }

    @Override
    public int getHeight() { return 84; }

    @Override
    public Identifier getRegistryName(JEIInfusionRecipe recipe) { return recipe.id(); }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JEIInfusionRecipe recipe, IFocusGroup focuses)
    {
        int count = recipe.pedestals().size();
        for (int i = 0; i < count; i++)
        {
            float rad = i * 1.0F / count * 2 * MathHelper.PI;
            int x = Math.round(40 - 29 * MathHelper.sin(rad));
            int y = Math.round(42 - 29 * MathHelper.cos(rad));
            builder.addSlot(RecipeIngredientRole.INPUT, x - 8, y - 8)
                .addItemStacks(recipe.pedestals().get(i)).setStandardSlotBackground();
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 32, 34).addItemStacks(recipe.table()).setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 34).addItemStack(recipe.output()).setOutputSlotBackground();
    }

    @Override
    public void draw(JEIInfusionRecipe recipe, IRecipeSlotsView slots, DrawContext context, double mouseX, double mouseY)
    {
        arrow.draw(context, 81, 34);
    }
}
