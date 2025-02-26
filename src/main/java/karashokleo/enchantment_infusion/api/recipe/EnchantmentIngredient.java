package karashokleo.enchantment_infusion.api.recipe;

import com.google.gson.JsonObject;
import karashokleo.enchantment_infusion.api.util.SerialUtil;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public record EnchantmentIngredient(
        Enchantment enchantment,
        int min_level
) implements CustomIngredient
{
    @SuppressWarnings("unused")
    public static Ingredient of(Enchantment enchantment, int min_level)
    {
        return new EnchantmentIngredient(enchantment, min_level).toVanilla();
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return EnchantmentHelper.get(stack).getOrDefault(this.enchantment, 0) >= this.min_level;
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
        for (int i = min_level; i <= enchantment.getMaxLevel(); i++)
            stacks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i)));
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
        public EnchantmentIngredient read(JsonObject json)
        {
            Enchantment enchantment = SerialUtil.enchantmentFromString(JsonHelper.getString(json, "enchantment"));
            int min_level = JsonHelper.getInt(json, "min_level");
            return new EnchantmentIngredient(enchantment, min_level);
        }

        @Override
        public void write(JsonObject json, EnchantmentIngredient ingredient)
        {
            json.addProperty("enchantment", SerialUtil.enchantmentToString(ingredient.enchantment));
            json.addProperty("min_level", ingredient.min_level);
        }

        @Override
        public EnchantmentIngredient read(PacketByteBuf buf)
        {
            Enchantment enchantment = SerialUtil.enchantmentFromString(buf.readString());
            int min_level = buf.readInt();
            return new EnchantmentIngredient(enchantment, min_level);
        }

        @Override
        public void write(PacketByteBuf buf, EnchantmentIngredient ingredient)
        {
            buf.writeString(SerialUtil.enchantmentToString(ingredient.enchantment));
            buf.writeInt(ingredient.min_level);
        }
    }
}
