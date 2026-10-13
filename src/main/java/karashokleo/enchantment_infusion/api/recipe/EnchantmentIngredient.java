package karashokleo.enchantment_infusion.api.recipe;

import com.google.gson.JsonObject;
import karashokleo.enchantment_infusion.api.util.SerialUtil;
import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import karashokleo.enchantment_infusion.init.EIRecipes;
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
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record EnchantmentIngredient(
    Enchantment enchantment,
    int min_level
)
{
    @SuppressWarnings("unused")
    public static Ingredient of(Enchantment enchantment, int min_level)
    {
        return new EnchantmentIngredient(enchantment, min_level).toVanilla();
    }

    public boolean test(ItemStack stack)
    {
        return EnchantmentHelper.get(stack).getOrDefault(this.enchantment, 0) >= this.min_level;
    }

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
        {
            stacks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i)));
        }
        return stacks;
    }

    public Adapter toVanilla()
    {
        return new Adapter(this);
    }

    public static final class Adapter extends AbstractIngredient
    {
        private final EnchantmentIngredient ingredient;

        private Adapter(EnchantmentIngredient ingredient)
        {
            this.ingredient = ingredient;
        }

        @Override
        public boolean test(ItemStack stack)
        {
            return ingredient.test(stack);
        }

        @Override
        public boolean isSimple()
        {
            return false;
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public ItemStack[] getMatchingStacks()
        {
            return ingredient.getMatchingStacks().toArray(ItemStack[]::new);
        }

        @NotNull
        @Override
        public IIngredientSerializer<?> getSerializer()
        {
            return EIRecipes.ENCHANTMENT_INGREDIENT_SERIALIZER;
        }

        @NotNull
        @Override
        public JsonObject toJson()
        {
            JsonObject json = new JsonObject();
            json.addProperty("type", EnchantmentInfusion.id("enchantment").toString());
            EIRecipes.ENCHANTMENT_INGREDIENT_SERIALIZER.write(json, ingredient);
            return json;
        }
    }

    public static class Serializer implements IIngredientSerializer<Adapter>
    {
        @NotNull
        @Override
        public Adapter parse(@NotNull JsonObject json)
        {
            return read(json).toVanilla();
        }

        @NotNull
        @Override
        public Adapter parse(@NotNull PacketByteBuf buf)
        {
            return read(buf).toVanilla();
        }

        @Override
        public void write(@NotNull PacketByteBuf buf, Adapter ingredient)
        {
            write(buf, ingredient.ingredient);
        }

        private final Identifier id = EnchantmentInfusion.id("enchantment");

        public Identifier getIdentifier()
        {
            return id;
        }

        public EnchantmentIngredient read(JsonObject json)
        {
            Enchantment enchantment = SerialUtil.enchantmentFromString(JsonHelper.getString(json, "enchantment"));
            int min_level = JsonHelper.getInt(json, "min_level");
            return new EnchantmentIngredient(enchantment, min_level);
        }

        public void write(JsonObject json, EnchantmentIngredient ingredient)
        {
            json.addProperty("enchantment", SerialUtil.enchantmentToString(ingredient.enchantment));
            json.addProperty("min_level", ingredient.min_level);
        }

        public EnchantmentIngredient read(PacketByteBuf buf)
        {
            Enchantment enchantment = SerialUtil.enchantmentFromString(buf.readString());
            int min_level = buf.readInt();
            return new EnchantmentIngredient(enchantment, min_level);
        }

        public void write(PacketByteBuf buf, EnchantmentIngredient ingredient)
        {
            buf.writeString(SerialUtil.enchantmentToString(ingredient.enchantment));
            buf.writeInt(ingredient.min_level);
        }
    }
}
