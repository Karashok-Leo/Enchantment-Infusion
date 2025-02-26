package karashokleo.enchantment_infusion.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Optional;

public class SerialUtil
{
    public static String enchantmentToString(Enchantment enchantment)
    {
        return Optional
                .ofNullable(Registries.ENCHANTMENT.getId(enchantment))
                .orElseThrow(() -> new IllegalArgumentException("Enchantment " + enchantment + " is not registered"))
                .toString();
    }

    public static Enchantment enchantmentFromString(String id)
    {
        return Registries.ENCHANTMENT
                .getOrEmpty(new Identifier(id))
                .orElseThrow(() -> new IllegalArgumentException("Unknown enchantment '" + id + "'"));
    }

    public static ItemStack itemStackFromJson(JsonElement json)
    {
        return Util.getResult(
                ItemStack.CODEC.decode(JsonOps.INSTANCE, json),
                JsonParseException::new
        ).getFirst();
    }

    public static JsonElement itemStackToJson(ItemStack stack)
    {
        return Util.getResult(
                ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stack),
                JsonParseException::new
        );
    }

    public static JsonArray ingredientsToJsonArray(List<Ingredient> ingredients)
    {
        JsonArray json = new JsonArray();
        for (Ingredient ingredient : ingredients)
            json.add(ingredient.toJson());
        return json;
    }

    public static DefaultedList<Ingredient> ingredientsFromJsonArray(JsonArray json)
    {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        for (int i = 0; i < json.size(); ++i)
        {
            Ingredient ingredient = Ingredient.fromJson(json.get(i), false);
            if (ingredient.isEmpty()) continue;
            ingredients.add(ingredient);
        }
        if (ingredients.isEmpty())
            throw new JsonParseException("No ingredients for enchantment infusion recipe");
        if (ingredients.size() > 8)
            throw new JsonParseException("Too many ingredients for enchantment infusion recipe");
        return ingredients;
    }

    public static void ingredientsToPacket(PacketByteBuf buf, DefaultedList<Ingredient> ingredients)
    {
        buf.writeVarInt(ingredients.size());
        for (Ingredient ingredient : ingredients)
            ingredient.write(buf);
    }

    public static DefaultedList<Ingredient> ingredientsFromPacket(PacketByteBuf buf)
    {
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(buf.readVarInt(), Ingredient.EMPTY);
        ingredients.replaceAll(ingredient -> Ingredient.fromPacket(buf));
        return ingredients;
    }
}
