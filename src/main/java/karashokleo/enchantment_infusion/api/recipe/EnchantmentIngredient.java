package karashokleo.enchantment_infusion.api.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.enchantment_infusion.fabric.EnchantmentInfusion;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record EnchantmentIngredient(
    RegistryEntry<Enchantment> enchantment,
    int min_level
) implements CustomIngredient
{
    public static final MapCodec<EnchantmentIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(EnchantmentIngredient::enchantment),
        Codec.INT.fieldOf("min_level").forGetter(EnchantmentIngredient::min_level)
    ).apply(instance, EnchantmentIngredient::new));

    public static final PacketCodec<RegistryByteBuf, EnchantmentIngredient> PACKET_CODEC = PacketCodec.tuple(
        Enchantment.ENTRY_PACKET_CODEC, EnchantmentIngredient::enchantment,
        PacketCodecs.INTEGER, EnchantmentIngredient::min_level,
        EnchantmentIngredient::new
    );

    @SuppressWarnings("unused")
    public static Ingredient of(RegistryEntry<Enchantment> enchantment, int min_level)
    {
        return new EnchantmentIngredient(enchantment, min_level).toVanilla();
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return EnchantmentHelper.getEnchantments(stack).getLevel(this.enchantment) >= this.min_level;
    }

    @Override
    public List<ItemStack> getMatchingStacks()
    {
        return min_level > 0 ?
            this.getBookStacks() :
            List.of(Items.BOOK.getDefaultStack());
    }

    private List<ItemStack> getBookStacks()
    {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = min_level; i <= enchantment.value().getMaxLevel(); i++)
        {
            stacks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i)));
        }
        return stacks;
    }

    @Override
    public boolean requiresTesting()
    {
        return true;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer()
    {
        return EIRecipes.ENCHANTMENT_INGREDIENT_SERIALIZER;
    }

    public static class Serializer implements CustomIngredientSerializer<EnchantmentIngredient>
    {
        private final Identifier id = EnchantmentInfusion.id("enchantment");

        @Override
        public Identifier getIdentifier()
        {
            return id;
        }

        @Override
        public MapCodec<EnchantmentIngredient> getCodec(boolean allowEmpty)
        {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, EnchantmentIngredient> getPacketCodec()
        {
            return PACKET_CODEC;
        }
    }
}
