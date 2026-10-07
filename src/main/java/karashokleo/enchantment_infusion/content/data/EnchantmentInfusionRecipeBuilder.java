package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;

public class EnchantmentInfusionRecipeBuilder
{
    private EnchantmentIngredient input = null;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private boolean force = false;

    public EnchantmentInfusionRecipeBuilder withTableIngredient(Holder<Enchantment> enchantment, int min_level)
    {
        return this.withTableIngredient(new EnchantmentIngredient(enchantment, min_level));
    }

    public EnchantmentInfusionRecipeBuilder withTableIngredient(EnchantmentIngredient input)
    {
        this.input = input;
        return this;
    }

    public EnchantmentInfusionRecipeBuilder withPedestalItem(int count, ItemLike item)
    {
        return withPedestalItem(count, Ingredient.of(item));
    }

    public EnchantmentInfusionRecipeBuilder withPedestalItem(int count, Ingredient ingredient)
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

    public EnchantmentInfusionRecipeBuilder force()
    {
        return force(true);
    }

    public EnchantmentInfusionRecipeBuilder force(boolean force)
    {
        this.force = force;
        return this;
    }

    public void offerTo(RecipeOutput exporter, ResourceLocation recipeId, Holder<Enchantment> enchantment, int level)
    {
        if (ingredients.isEmpty())
        {
            throw new IllegalArgumentException("No ingredients for enchantment infusion recipe");
        }
        if (ingredients.size() > 8)
        {
            throw new IllegalArgumentException("Too many ingredients for enchantment infusion recipe");
        }
        exporter.accept(recipeId, new EnchantmentInfusionRecipe(input, ingredients, enchantment, level, force), null);
    }
}
