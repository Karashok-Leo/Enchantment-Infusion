package karashokleo.enchantment_infusion.content.data;

import com.google.gson.JsonObject;
import karashokleo.enchantment_infusion.api.util.SerialUtil;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleInfusionRecipeBuilder
{
    private Ingredient input = null;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private boolean copyNbt = true;

    public SimpleInfusionRecipeBuilder withTableIngredient(Ingredient input)
    {
        this.input = input;
        return this;
    }

    public SimpleInfusionRecipeBuilder withPedestalItem(int count, ItemConvertible item)
    {
        return withPedestalItem(count, Ingredient.ofItems(item));
    }

    public SimpleInfusionRecipeBuilder withPedestalItem(int count, Ingredient ingredient)
    {
        for (int i = 0; i < count; i++)
        {
            ingredients.add(ingredient);
            if (ingredients.size() > 8)
                throw new UnsupportedOperationException();
        }
        return this;
    }

    public SimpleInfusionRecipeBuilder copyNbt(boolean copyNbt)
    {
        this.copyNbt = copyNbt;
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId, ItemStack output)
    {
        if (ingredients.isEmpty())
            throw new IllegalArgumentException("No ingredients for enchantment infusion recipe");
        if (ingredients.size() > 8)
            throw new IllegalArgumentException("Too many ingredients for enchantment infusion recipe");
        exporter.accept(new SimpleInfusionRecipeJsonProvider(recipeId, input, ingredients, output, copyNbt));
    }

    public record SimpleInfusionRecipeJsonProvider(
            Identifier recipeId,
            Ingredient input,
            List<Ingredient> ingredients,
            ItemStack output,
            boolean copyNbt
    ) implements RecipeJsonProvider
    {
        @Override
        public void serialize(JsonObject json)
        {
            json.add("input", input.toJson());
            json.add("ingredients", SerialUtil.ingredientsToJsonArray(ingredients));
            json.add("output", SerialUtil.itemStackToJson(output));
            json.addProperty("copy_nbt", copyNbt);
        }

        @Override
        public Identifier getRecipeId()
        {
            return recipeId;
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return EIRecipes.SI_SERIALIZER;
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
