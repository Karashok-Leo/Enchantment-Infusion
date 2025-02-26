package karashokleo.enchantment_infusion.content.recipe;

import com.google.gson.JsonObject;
import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.util.SerialUtil;
import karashokleo.enchantment_infusion.init.EIRecipes;
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
        EnchantmentIngredient input = null;
        if (JsonHelper.hasElement(json, "input"))
        {
            JsonObject inputJson = JsonHelper.getObject(json, "input");
            input = EIRecipes.ENCHANTMENT_INGREDIENT_SERIALIZER.read(inputJson);
        }
        DefaultedList<Ingredient> ingredients = SerialUtil.ingredientsFromJsonArray(JsonHelper.getArray(json, "ingredients"));
        Enchantment enchantment = SerialUtil.enchantmentFromString(JsonHelper.getString(json, "enchantment"));
        int level = JsonHelper.getInt(json, "level");
        boolean force = JsonHelper.getBoolean(json, "force", false);
        return new EnchantmentInfusionRecipe(id, input, ingredients, enchantment, level, force);
    }

    @Override
    public EnchantmentInfusionRecipe read(Identifier id, PacketByteBuf buf)
    {
        EnchantmentIngredient input = null;
        if (buf.readBoolean())
            input = EIRecipes.ENCHANTMENT_INGREDIENT_SERIALIZER.read(buf);
        DefaultedList<Ingredient> ingredients = SerialUtil.ingredientsFromPacket(buf);
        Enchantment enchantment = SerialUtil.enchantmentFromString(buf.readString());
        int level = buf.readInt();
        boolean force = buf.readBoolean();
        return new EnchantmentInfusionRecipe(id, input, ingredients, enchantment, level, force);
    }

    @Override
    public void write(PacketByteBuf buf, EnchantmentInfusionRecipe recipe)
    {
        buf.writeBoolean(recipe.input() != null);
        if (recipe.input() != null)
            EIRecipes.ENCHANTMENT_INGREDIENT_SERIALIZER.write(buf, recipe.input());
        SerialUtil.ingredientsToPacket(buf, recipe.ingredients());
        buf.writeString(SerialUtil.enchantmentToString(recipe.enchantment()));
        buf.writeInt(recipe.level());
        buf.writeBoolean(recipe.force());
    }
}
