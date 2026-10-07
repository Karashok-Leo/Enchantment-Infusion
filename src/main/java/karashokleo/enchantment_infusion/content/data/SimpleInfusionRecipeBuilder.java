package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class SimpleInfusionRecipeBuilder
{
    private Ingredient input = null;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private boolean copyNbt = true;

    public SimpleInfusionRecipeBuilder withTableIngredient(Ingredient input)
    {
        this.input = input;
        return this;
    }

    public SimpleInfusionRecipeBuilder withPedestalItem(int count, ItemLike item)
    {
        return withPedestalItem(count, Ingredient.of(item));
    }

    public SimpleInfusionRecipeBuilder withPedestalItem(int count, Ingredient ingredient)
    {
        for (int i = 0; i < count; i++)
        {
            ingredients.add(ingredient);
            if (ingredients.size() > 8)
            {
                throw new UnsupportedOperationException();
            }
        }
        return this;
    }

    public SimpleInfusionRecipeBuilder copyNbt(boolean copyNbt)
    {
        this.copyNbt = copyNbt;
        return this;
    }

    public void offerTo(RecipeOutput exporter, ResourceLocation recipeId, ItemStack output)
    {
        if (ingredients.isEmpty())
        {
            throw new IllegalArgumentException("No ingredients for enchantment infusion recipe");
        }
        if (ingredients.size() > 8)
        {
            throw new IllegalArgumentException("Too many ingredients for enchantment infusion recipe");
        }
        exporter.accept(recipeId, new SimpleInfusionRecipe(input, ingredients, output, copyNbt), null);
    }
}
