package karashokleo.enchantment_infusion.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;

public class SerialUtil
{
    public static final Codec<NonNullList<Ingredient>> INGREDIENTS_CODEC = Ingredient.CODEC_NONEMPTY.listOf()
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

    public static String enchantmentToString(Holder<Enchantment> enchantment)
    {
        return enchantment.unwrapKey()
            .orElseThrow(() -> new IllegalArgumentException("Enchantment " + enchantment + " is not registered"))
            .location().toString();
    }

    public static Holder<Enchantment> enchantmentFromString(String id, HolderLookup.Provider lookup)
    {
        return lookup.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse(id)));
    }

    public static ItemStack itemStackFromJson(JsonElement json, HolderLookup.Provider lookup)
    {
        return ItemStack.CODEC.parse(lookup.createSerializationContext(JsonOps.INSTANCE), json).getOrThrow(JsonParseException::new);
    }

    public static JsonElement itemStackToJson(ItemStack stack, HolderLookup.Provider lookup)
    {
        return ItemStack.CODEC.encodeStart(lookup.createSerializationContext(JsonOps.INSTANCE), stack).getOrThrow(JsonParseException::new);
    }

    public static JsonArray ingredientsToJsonArray(List<Ingredient> ingredients, HolderLookup.Provider lookup)
    {
        return Ingredient.CODEC_NONEMPTY.listOf()
            .encodeStart(lookup.createSerializationContext(JsonOps.INSTANCE), ingredients).getOrThrow(JsonParseException::new).getAsJsonArray();
    }

    public static NonNullList<Ingredient> ingredientsFromJsonArray(JsonArray json, HolderLookup.Provider lookup)
    {
        return INGREDIENTS_CODEC.parse(lookup.createSerializationContext(JsonOps.INSTANCE), json).getOrThrow(JsonParseException::new);
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
