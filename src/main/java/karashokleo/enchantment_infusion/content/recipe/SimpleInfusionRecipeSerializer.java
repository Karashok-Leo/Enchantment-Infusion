package karashokleo.enchantment_infusion.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.enchantment_infusion.api.util.SerialUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

public class SimpleInfusionRecipeSerializer implements RecipeSerializer<SimpleInfusionRecipe>
{
    public static final MapCodec<SimpleInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter(SimpleInfusionRecipe::input),
        SerialUtil.INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(SimpleInfusionRecipe::ingredients),
        ItemStack.CODEC.fieldOf("output").forGetter(SimpleInfusionRecipe::output),
        Codec.BOOL.optionalFieldOf("copy_nbt", true).forGetter(SimpleInfusionRecipe::copyNbt)
    ).apply(instance, SimpleInfusionRecipe::new));

    public static final PacketCodec<RegistryByteBuf, SimpleInfusionRecipe> PACKET_CODEC = PacketCodec.ofStatic(
        SimpleInfusionRecipeSerializer::write, SimpleInfusionRecipeSerializer::read
    );

    @Override
    public MapCodec<SimpleInfusionRecipe> codec()
    {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, SimpleInfusionRecipe> packetCodec()
    {
        return PACKET_CODEC;
    }

    private static SimpleInfusionRecipe read(RegistryByteBuf buf)
    {
        Ingredient input = Ingredient.PACKET_CODEC.decode(buf);
        var ingredients = SerialUtil.ingredientsFromPacket(buf);
        ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
        boolean copyNbt = buf.readBoolean();
        return new SimpleInfusionRecipe(input, ingredients, output, copyNbt);
    }

    private static void write(RegistryByteBuf buf, SimpleInfusionRecipe recipe)
    {
        Ingredient.PACKET_CODEC.encode(buf, recipe.input());
        SerialUtil.ingredientsToPacket(buf, recipe.ingredients());
        ItemStack.PACKET_CODEC.encode(buf, recipe.output());
        buf.writeBoolean(recipe.copyNbt());
    }
}
