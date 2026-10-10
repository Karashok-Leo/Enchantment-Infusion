package karashokleo.enchantment_infusion.api.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import java.util.ArrayList;
import java.util.List;

public record EnchantmentIngredient(
    Holder<Enchantment> enchantment,
    int min_level
) implements ICustomIngredient
{
    public static final MapCodec<EnchantmentIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentIngredient::enchantment),
        Codec.INT.fieldOf("min_level").forGetter(EnchantmentIngredient::min_level)
    ).apply(instance, EnchantmentIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentIngredient> PACKET_CODEC = StreamCodec.composite(
        Enchantment.STREAM_CODEC, EnchantmentIngredient::enchantment,
        ByteBufCodecs.INT, EnchantmentIngredient::min_level,
        EnchantmentIngredient::new
    );

    @SuppressWarnings("unused")
    public static Ingredient of(Holder<Enchantment> enchantment, int min_level)
    {
        return new EnchantmentIngredient(enchantment, min_level).toVanilla();
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return EnchantmentHelper.getEnchantmentsForCrafting(stack).getLevel(this.enchantment) >= this.min_level;
    }

    @Override
    public Stream<ItemStack> getItems()
    {
        return min_level > 0 ?
            this.getBookStacks().stream() :
            Stream.of(Items.BOOK.getDefaultInstance());
    }

    private List<ItemStack> getBookStacks()
    {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = min_level; i <= enchantment.value().getMaxLevel(); i++)
        {
            stacks.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, i)));
        }
        return stacks;
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    @Override
    public IngredientType<?> getType()
    {
        return EIRecipes.ENCHANTMENT_INGREDIENT_SERIALIZER.get();
    }
}
