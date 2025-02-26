package karashokleo.enchantment_infusion.content.recipe;

import com.google.gson.JsonObject;
import karashokleo.enchantment_infusion.api.util.SerialUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class SimpleInfusionRecipeSerializer implements RecipeSerializer<SimpleInfusionRecipe>
{
    @Override
    public SimpleInfusionRecipe read(Identifier id, JsonObject json)
    {
        Ingredient input = Ingredient.fromJson(JsonHelper.getElement(json, "input"));
        DefaultedList<Ingredient> ingredients = SerialUtil.ingredientsFromJsonArray(JsonHelper.getArray(json, "ingredients"));
        ItemStack output = SerialUtil.itemStackFromJson(JsonHelper.getElement(json, "output"));
        boolean copyNbt = JsonHelper.getBoolean(json, "copy_nbt", true);
        return new SimpleInfusionRecipe(id, input, ingredients, output, copyNbt);
    }

    @Override
    public SimpleInfusionRecipe read(Identifier id, PacketByteBuf buf)
    {
        Ingredient input = Ingredient.fromPacket(buf);
        DefaultedList<Ingredient> ingredients = SerialUtil.ingredientsFromPacket(buf);
        ItemStack output = buf.readItemStack();
        boolean copyNbt = buf.readBoolean();
        return new SimpleInfusionRecipe(id, input, ingredients, output, copyNbt);
    }

    @Override
    public void write(PacketByteBuf buf, SimpleInfusionRecipe recipe)
    {
        recipe.input().write(buf);
        SerialUtil.ingredientsToPacket(buf, recipe.ingredients());
        buf.writeItemStack(recipe.output());
        buf.writeBoolean(recipe.copyNbt());
    }
}
