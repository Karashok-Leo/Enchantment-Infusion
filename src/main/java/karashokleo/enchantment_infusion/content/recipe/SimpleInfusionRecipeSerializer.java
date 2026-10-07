package karashokleo.enchantment_infusion.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.enchantment_infusion.api.util.SerialUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SimpleInfusionRecipeSerializer implements RecipeSerializer<SimpleInfusionRecipe>
{
    public static final MapCodec<SimpleInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SimpleInfusionRecipe::input),
        SerialUtil.INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(SimpleInfusionRecipe::ingredients),
        ItemStack.CODEC.fieldOf("output").forGetter(SimpleInfusionRecipe::output),
        Codec.BOOL.optionalFieldOf("copy_nbt", true).forGetter(SimpleInfusionRecipe::copyNbt)
    ).apply(instance, SimpleInfusionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleInfusionRecipe> PACKET_CODEC = StreamCodec.of(
        SimpleInfusionRecipeSerializer::write, SimpleInfusionRecipeSerializer::read
    );

    @Override
    public MapCodec<SimpleInfusionRecipe> codec()
    {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SimpleInfusionRecipe> streamCodec()
    {
        return PACKET_CODEC;
    }

    private static SimpleInfusionRecipe read(RegistryFriendlyByteBuf buf)
    {
        Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        var ingredients = SerialUtil.ingredientsFromPacket(buf);
        ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
        boolean copyNbt = buf.readBoolean();
        return new SimpleInfusionRecipe(input, ingredients, output, copyNbt);
    }

    private static void write(RegistryFriendlyByteBuf buf, SimpleInfusionRecipe recipe)
    {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input());
        SerialUtil.ingredientsToPacket(buf, recipe.ingredients());
        ItemStack.STREAM_CODEC.encode(buf, recipe.output());
        buf.writeBoolean(recipe.copyNbt());
    }
}
