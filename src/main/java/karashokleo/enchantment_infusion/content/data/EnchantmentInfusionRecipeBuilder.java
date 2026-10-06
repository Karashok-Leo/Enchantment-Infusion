package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class EnchantmentInfusionRecipeBuilder
{
    private EnchantmentIngredient input = null;
    private final DefaultedList<Ingredient> ingredients = DefaultedList.of();
    private boolean force = false;

    public EnchantmentInfusionRecipeBuilder withTableIngredient(RegistryEntry<Enchantment> enchantment, int min_level)
    {
        return this.withTableIngredient(new EnchantmentIngredient(enchantment, min_level));
    }

    public EnchantmentInfusionRecipeBuilder withTableIngredient(EnchantmentIngredient input)
    {
        this.input = input;
        return this;
    }

    public EnchantmentInfusionRecipeBuilder withPedestalItem(int count, ItemConvertible item)
    {
        return withPedestalItem(count, Ingredient.ofItems(item));
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

    public void offerTo(RecipeExporter exporter, Identifier recipeId, RegistryEntry<Enchantment> enchantment, int level)
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
