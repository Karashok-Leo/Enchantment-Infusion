package karashokleo.enchantment_infusion.content.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.util.EnchantmentSerial;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
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
                throw new UnsupportedOperationException();
        }
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId, Enchantment enchantment, EnchantmentInfusionRecipe.Mode mode, int level)
    {
        offerTo(exporter, recipeId, enchantment, mode, level, false);
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId, Enchantment enchantment, EnchantmentInfusionRecipe.Mode mode, int level, boolean force)
    {
        if (ingredients.isEmpty())
            throw new JsonParseException("No ingredients for enchantment infusion recipe");
        if (ingredients.size() > 8)
            throw new JsonParseException("Too many ingredients for enchantment infusion recipe");
        exporter.accept(new EnchantmentInfusionRecipeJsonProvider(recipeId, input, ingredients, enchantment, mode, level, force));
    }

    public record EnchantmentInfusionRecipeJsonProvider(
            Identifier recipeId,
            @Nullable EnchantmentIngredient input,
            List<Ingredient> ingredients,
            Enchantment enchantment,
            EnchantmentInfusionRecipe.Mode mode,
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
            JsonArray jsonArray = new JsonArray();
            for (Ingredient ingredient : ingredients)
                jsonArray.add(ingredient.toJson());
            json.add("ingredients", jsonArray);
            json.addProperty("enchantment", EnchantmentSerial.encode(enchantment));
            json.addProperty("mode", mode.name());
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
