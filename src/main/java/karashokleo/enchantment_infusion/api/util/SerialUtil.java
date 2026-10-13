package karashokleo.enchantment_infusion.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class SerialUtil
{
    public static final Codec<DefaultedList<Ingredient>> INGREDIENTS_CODEC = Ingredient.DISALLOW_EMPTY_CODEC
        .listOf()
        .comapFlatMap(SerialUtil::validateIngredients, ingredients -> ingredients);

    private static DataResult<DefaultedList<Ingredient>> validateIngredients(List<Ingredient> values)
    {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        for (Ingredient ingredient : values)
        {
            if (!ingredient.isEmpty())
            {
                ingredients.add(ingredient);
            }
        }
        if (ingredients.isEmpty())
        {
            return DataResult.error(() -> "No ingredients for enchantment infusion recipe");
        }
        if (ingredients.size() > 8)
        {
            return DataResult.error(() -> "Too many ingredients for enchantment infusion recipe");
        }
        return DataResult.success(ingredients);
    }

    public static void ingredientsToPacket(RegistryByteBuf buf, DefaultedList<Ingredient> ingredients)
    {
        buf.writeVarInt(ingredients.size());
        for (Ingredient ingredient : ingredients)
        {
            Ingredient.PACKET_CODEC.encode(buf, ingredient);
        }
    }

    public static DefaultedList<Ingredient> ingredientsFromPacket(RegistryByteBuf buf)
    {
        int size = buf.readVarInt();
        if (size < 1 || size > 8)
        {
            throw new IllegalArgumentException("Infusion recipes must have between 1 and 8 ingredients");
        }
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);
        ingredients.replaceAll(ingredient -> Ingredient.PACKET_CODEC.decode(buf));
        return ingredients;
    }
}
