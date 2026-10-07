package karashokleo.enchantment_infusion.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.util.SerialUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.Optional;

public class EnchantmentInfusionRecipeSerializer implements RecipeSerializer<EnchantmentInfusionRecipe>
{
    public static final MapCodec<EnchantmentInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        EnchantmentIngredient.CODEC.codec().optionalFieldOf("input").forGetter(recipe -> Optional.ofNullable(recipe.input())),
        SerialUtil.INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(EnchantmentInfusionRecipe::ingredients),
        Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentInfusionRecipe::enchantment),
        Codec.intRange(1, 255).fieldOf("level").forGetter(EnchantmentInfusionRecipe::level),
        Codec.BOOL.optionalFieldOf("force", false).forGetter(EnchantmentInfusionRecipe::force)
    ).apply(instance, (input, ingredients, enchantment, level, force) ->
        new EnchantmentInfusionRecipe(input.orElse(null), ingredients, enchantment, level, force)));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentInfusionRecipe> PACKET_CODEC = StreamCodec.of(
        EnchantmentInfusionRecipeSerializer::write, EnchantmentInfusionRecipeSerializer::read
    );

    @Override
    public MapCodec<EnchantmentInfusionRecipe> codec()
    {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EnchantmentInfusionRecipe> streamCodec()
    {
        return PACKET_CODEC;
    }

    private static EnchantmentInfusionRecipe read(RegistryFriendlyByteBuf buf)
    {
        EnchantmentIngredient input = buf.readBoolean() ? EnchantmentIngredient.PACKET_CODEC.decode(buf) : null;
        var ingredients = SerialUtil.ingredientsFromPacket(buf);
        var enchantment = Enchantment.STREAM_CODEC.decode(buf);
        int level = buf.readInt();
        boolean force = buf.readBoolean();
        return new EnchantmentInfusionRecipe(input, ingredients, enchantment, level, force);
    }

    private static void write(RegistryFriendlyByteBuf buf, EnchantmentInfusionRecipe recipe)
    {
        buf.writeBoolean(recipe.input() != null);
        if (recipe.input() != null)
        {
            EnchantmentIngredient.PACKET_CODEC.encode(buf, recipe.input());
        }
        SerialUtil.ingredientsToPacket(buf, recipe.ingredients());
        Enchantment.STREAM_CODEC.encode(buf, recipe.enchantment());
        buf.writeInt(recipe.level());
        buf.writeBoolean(recipe.force());
    }
}
