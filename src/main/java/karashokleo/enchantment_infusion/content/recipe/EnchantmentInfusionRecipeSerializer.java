package karashokleo.enchantment_infusion.content.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import karashokleo.enchantment_infusion.api.util.EnchantmentSerial;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class EnchantmentInfusionRecipeSerializer implements RecipeSerializer<EnchantmentInfusionRecipe>
{
    @Override
    public EnchantmentInfusionRecipe read(Identifier id, JsonObject json)
    {
        Ingredient input = null;
        if (JsonHelper.hasElement(json, "input"))
        {
            JsonElement inputJson = JsonHelper.getElement(json, "input");
            input = Ingredient.fromJson(inputJson);
        }
        DefaultedList<Ingredient> ingredients = getIngredients(JsonHelper.getArray(json, "ingredients"));
        if (ingredients.isEmpty())
            throw new JsonParseException("No ingredients for enchantment infusion recipe");
        if (ingredients.size() > 8)
            throw new JsonParseException("Too many ingredients for enchantment infusion recipe");
        Enchantment enchantment = EnchantmentSerial.decode(JsonHelper.getString(json, "enchantment"));
        EnchantmentInfusionRecipe.Mode mode = EnchantmentInfusionRecipe.Mode.valueOf(JsonHelper.getString(json, "mode"));
        int level = JsonHelper.getInt(json, "level");
        boolean force = JsonHelper.getBoolean(json, "force", false);
        return new EnchantmentInfusionRecipe(id, input, ingredients, enchantment, mode, level, force);
    }

    private static DefaultedList<Ingredient> getIngredients(JsonArray json)
    {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        for (int i = 0; i < json.size(); ++i)
        {
            Ingredient ingredient = Ingredient.fromJson(json.get(i), false);
            if (ingredient.isEmpty()) continue;
            defaultedList.add(ingredient);
        }
        return defaultedList;
    }

    @Override
    public EnchantmentInfusionRecipe read(Identifier id, PacketByteBuf buf)
    {
        Ingredient input = null;
        if (buf.readBoolean())
            input = Ingredient.fromPacket(buf);
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(buf.readVarInt(), Ingredient.EMPTY);
        ingredients.replaceAll(ingredient -> Ingredient.fromPacket(buf));
        Enchantment enchantment = EnchantmentSerial.decode(buf.readString());
        EnchantmentInfusionRecipe.Mode mode = buf.readEnumConstant(EnchantmentInfusionRecipe.Mode.class);
        int level = buf.readInt();
        boolean force = buf.readBoolean();
        return new EnchantmentInfusionRecipe(id, input, ingredients, enchantment, mode, level, force);
    }

    @Override
    public void write(PacketByteBuf buf, EnchantmentInfusionRecipe recipe)
    {
        buf.writeBoolean(recipe.input() != null);
        if (recipe.input() != null)
            recipe.input().write(buf);
        buf.writeVarInt(recipe.ingredients().size());
        for (Ingredient ingredient : recipe.ingredients())
            ingredient.write(buf);
        buf.writeString(EnchantmentSerial.encode(recipe.enchantment()));
        buf.writeEnumConstant(recipe.mode());
        buf.writeInt(recipe.level());
        buf.writeBoolean(recipe.force());
    }
}
