package karashokleo.enchantment_infusion.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class SerialUtil
{
    public static final Codec<NonNullList<Ingredient>> INGREDIENTS_CODEC = Ingredient.CODEC_NONEMPTY
        .listOf()
        .comapFlatMap(SerialUtil::validateIngredients, ingredients -> ingredients);

    private static DataResult<NonNullList<Ingredient>> validateIngredients(List<Ingredient> values)
    {
        NonNullList<Ingredient> ingredients = NonNullList.create();
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

    public static void ingredientsToPacket(RegistryFriendlyByteBuf buf, NonNullList<Ingredient> ingredients)
    {
        buf.writeVarInt(ingredients.size());
        for (Ingredient ingredient : ingredients)
        {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
        }
    }

    public static NonNullList<Ingredient> ingredientsFromPacket(RegistryFriendlyByteBuf buf)
    {
        int size = buf.readVarInt();
        if (size < 1 || size > 8)
        {
            throw new IllegalArgumentException("Infusion recipes must have between 1 and 8 ingredients");
        }
        NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
        ingredients.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
        return ingredients;
    }
}
