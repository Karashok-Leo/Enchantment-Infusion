package karashokleo.enchantment_infusion.content.compat.jei;

import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class JEIInfusionCategory implements IRecipeCategory<InfusionRecipe>
{
    private final IDrawable icon;
    private final IDrawable arrow;

    public JEIInfusionCategory(IGuiHelper guiHelper)
    {
        icon = guiHelper.createDrawableItemStack(new ItemStack(EIBlocks.INFUSION_TABLE));
        arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<InfusionRecipe> getRecipeType() { return JEICompat.INFUSION; }

    @Override
    public Text getTitle() { return EITexts.CATEGORY.get(); }

    @Override
    public IDrawable getIcon() { return icon; }

    @Override
    public int getWidth() { return 138; }

    @Override
    public int getHeight() { return 84; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InfusionRecipe recipe, IFocusGroup focuses)
    {
        var world = MinecraftClient.getInstance().world;
        if (world != null)
            populateLayout(builder, recipe, recipe.getOutput(world.getRegistryManager()));
    }

    static void populateLayout(IRecipeLayoutBuilder builder, InfusionRecipe recipe, ItemStack output)
    {
        var ingredients = recipe.getPedestalIngredient();
        for (int i = 0; i < ingredients.size(); i++)
        {
            float angle = i * 1.0F / ingredients.size() * 2 * MathHelper.PI;
            int x = Math.round(40 - 29 * MathHelper.sin(angle));
            int y = Math.round(42 - 29 * MathHelper.cos(angle));
            builder.addSlot(RecipeIngredientRole.INPUT, x - 8, y - 8)
                .setStandardSlotBackground()
                .addItemStacks(Arrays.asList(ingredients.get(i).getMatchingStacks()));
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 32, 34)
            .setStandardSlotBackground()
            .addItemStacks(Arrays.asList(recipe.getTableIngredient().getMatchingStacks()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 34)
            .setOutputSlotBackground()
            .addItemStack(output);
    }

    @Override
    public void draw(InfusionRecipe recipe, IRecipeSlotsView slots, DrawContext context, double mouseX, double mouseY)
    {
        arrow.draw(context, 81, 34);
    }
}
