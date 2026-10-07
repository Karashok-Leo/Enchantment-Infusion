package karashokleo.enchantment_infusion.content.compat.jei;

import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EITexts;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/** The same central input, pedestal ring, and output as the EMI/REI views. */
public class JEIInfusionCategory implements IRecipeCategory<RecipeHolder<InfusionRecipe>>
{
    private final IDrawable icon;
    private final IDrawable arrow;

    public JEIInfusionCategory(IGuiHelper guiHelper)
    {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EIBlocks.INFUSION_TABLE));
        arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<RecipeHolder<InfusionRecipe>> getRecipeType() { return JEICompat.EI; }

    @Override
    public Component getTitle() { return EITexts.CATEGORY.get(); }

    @Override
    public IDrawable getIcon() { return icon; }

    @Override
    public int getWidth() { return 138; }

    @Override
    public int getHeight() { return 84; }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<InfusionRecipe> recipe) { return recipe.id(); }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<InfusionRecipe> holder, IFocusGroup focuses)
    {
        InfusionRecipe recipe = holder.value();
        var ingredients = recipe.getPedestalIngredient();
        for (int i = 0; i < ingredients.size(); i++)
        {
            float rad = i * 1.0F / ingredients.size() * 2 * Mth.PI;
            int x = Math.round(40 - 29 * Mth.sin(rad));
            int y = Math.round(42 - 29 * Mth.cos(rad));
            builder.addSlot(RecipeIngredientRole.INPUT, x - 8, y - 8)
                .setStandardSlotBackground().addIngredients(ingredients.get(i));
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 32, 34)
            .setStandardSlotBackground().addIngredients(recipe.getTableIngredient());
        var level = Minecraft.getInstance().level;
        if (level != null)
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 34)
                .setOutputSlotBackground().addItemStack(recipe.getResultItem(level.registryAccess()));
        }
    }

    @Override
    public void draw(RecipeHolder<InfusionRecipe> recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY)
    {
        arrow.draw(graphics, 81, 34);
    }
}
