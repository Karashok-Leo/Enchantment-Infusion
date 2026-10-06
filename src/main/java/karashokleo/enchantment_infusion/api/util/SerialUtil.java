package karashokleo.enchantment_infusion.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class SerialUtil
{
    public static final Codec<DefaultedList<Ingredient>> INGREDIENTS_CODEC = Ingredient.DISALLOW_EMPTY_CODEC.listOf()
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

    public static String enchantmentToString(RegistryEntry<Enchantment> enchantment)
    {
        return enchantment.getKey()
            .orElseThrow(() -> new IllegalArgumentException("Enchantment " + enchantment + " is not registered"))
            .getValue().toString();
    }

    public static RegistryEntry<Enchantment> enchantmentFromString(String id, RegistryWrapper.WrapperLookup lookup)
    {
        return lookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(id)));
    }

    public static ItemStack itemStackFromJson(JsonElement json, RegistryWrapper.WrapperLookup lookup)
    {
        return ItemStack.CODEC.parse(lookup.getOps(JsonOps.INSTANCE), json).getOrThrow(JsonParseException::new);
    }

    public static JsonElement itemStackToJson(ItemStack stack, RegistryWrapper.WrapperLookup lookup)
    {
        return ItemStack.CODEC.encodeStart(lookup.getOps(JsonOps.INSTANCE), stack).getOrThrow(JsonParseException::new);
    }

    public static JsonArray ingredientsToJsonArray(List<Ingredient> ingredients, RegistryWrapper.WrapperLookup lookup)
    {
        return Ingredient.DISALLOW_EMPTY_CODEC.listOf()
            .encodeStart(lookup.getOps(JsonOps.INSTANCE), ingredients).getOrThrow(JsonParseException::new).getAsJsonArray();
    }

    public static DefaultedList<Ingredient> ingredientsFromJsonArray(JsonArray json, RegistryWrapper.WrapperLookup lookup)
    {
        return INGREDIENTS_CODEC.parse(lookup.getOps(JsonOps.INSTANCE), json).getOrThrow(JsonParseException::new);
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
