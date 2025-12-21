package karashokleo.enchantment_infusion.content.data;

import com.google.gson.JsonObject;
import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.util.SerialUtil;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnchantmentInfusionRecipeBuilder
{
    private EnchantmentIngredient input = null;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private boolean force = false;

    public EnchantmentInfusionRecipeBuilder withTableIngredient(Enchantment enchantment, int min_level)
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

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId, Enchantment enchantment, int level)
    {
        if (ingredients.isEmpty())
        {
            throw new IllegalArgumentException("No ingredients for enchantment infusion recipe");
        }
        if (ingredients.size() > 8)
        {
            throw new IllegalArgumentException("Too many ingredients for enchantment infusion recipe");
        }
        exporter.accept(new EnchantmentInfusionRecipeJsonProvider(recipeId, input, ingredients, enchantment, level, force));
    }

    public record EnchantmentInfusionRecipeJsonProvider(
        Identifier recipeId,
        @Nullable EnchantmentIngredient input,
        List<Ingredient> ingredients,
        Enchantment enchantment,
        int level,
        boolean force
    ) implements RecipeJsonProvider
    {
        @Override
        public void serialize(JsonObject json)
        {
            if (input != null)
            {
                JsonObject inputJson = new JsonObject();
                EIRecipes.ENCHANTMENT_INGREDIENT_SERIALIZER.write(inputJson, input);
                json.add("input", inputJson);
            }
            json.add("ingredients", SerialUtil.ingredientsToJsonArray(ingredients));
            json.addProperty("enchantment", SerialUtil.enchantmentToString(enchantment));
            json.addProperty("level", level);
            json.addProperty("force", force);
        }

        @Override
        public Identifier getRecipeId()
        {
            return recipeId;
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return EIRecipes.EI_SERIALIZER;
        }

        @Nullable
        @Override
        public JsonObject toAdvancementJson()
        {
            return null;
        }

        @Nullable
        @Override
        public Identifier getAdvancementId()
        {
            return null;
        }
    }
}
