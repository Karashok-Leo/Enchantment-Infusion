package karashokleo.enchantment_infusion.api.recipe;

import com.google.gson.JsonObject;
import karashokleo.enchantment_infusion.api.util.EnchantmentSerial;
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
                List.of(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, min_level))) :
                List.of(Items.BOOK.getDefaultStack());
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
            Enchantment enchantment = EnchantmentSerial.decode(JsonHelper.getString(json, "enchantment"));
            int min_level = JsonHelper.getInt(json, "min_level");
            return new EnchantmentIngredient(enchantment, min_level);
        }

        @Override
        public void write(JsonObject json, EnchantmentIngredient ingredient)
        {
            json.addProperty("enchantment", EnchantmentSerial.encode(ingredient.enchantment));
            json.addProperty("min_level", ingredient.min_level);
        }

        @Override
        public EnchantmentIngredient read(PacketByteBuf buf)
        {
            Enchantment enchantment = EnchantmentSerial.decode(buf.readString());
            int min_level = buf.readInt();
            return new EnchantmentIngredient(enchantment, min_level);
        }

        @Override
        public void write(PacketByteBuf buf, EnchantmentIngredient ingredient)
        {
            buf.writeString(EnchantmentSerial.encode(ingredient.enchantment));
            buf.writeInt(ingredient.min_level);
        }
    }
}
