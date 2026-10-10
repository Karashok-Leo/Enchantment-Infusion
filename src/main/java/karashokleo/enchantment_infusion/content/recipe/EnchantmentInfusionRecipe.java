package karashokleo.enchantment_infusion.content.recipe;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * @param input       The enchantment that table stack should have
 * @param ingredients Ingredients on the pedestals
 * @param enchantment The output enchantment
 * @param level       The output level
 * @param force       Ignore enchantment target matching and enchantment compatibility
 */
public record EnchantmentInfusionRecipe(
    @Nullable EnchantmentIngredient input,
    NonNullList<Ingredient> ingredients,
    Holder<Enchantment> enchantment,
    int level,
    boolean force
) implements InfusionRecipe
{
    @Override
    public Ingredient getTableIngredient()
    {
        return input == null ? Ingredient.of(Items.BOOK) : input.toVanilla();
    }

    @Override
    public NonNullList<Ingredient> getPedestalIngredient()
    {
        return ingredients;
    }

    @Override
    public ItemStack infuse(ItemStack tableStack)
    {
        ItemStack stack = tableStack.is(Items.BOOK) ? Items.ENCHANTED_BOOK.getDefaultInstance() : tableStack;

        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(stack));
        if (input != null)
        {
            enchantments.removeIf(input.enchantment()::equals);
        }
        enchantments.set(enchantment, level);

        stack.remove(DataComponents.ENCHANTMENTS);
        stack.remove(DataComponents.STORED_ENCHANTMENTS);
        EnchantmentHelper.setEnchantments(stack, enchantments.toImmutable());
        return stack;
    }

    @Override
    public boolean matchTableStack(ItemStack stack)
    {
        boolean acceptable = stack.is(Items.BOOK) ||
            stack.is(Items.ENCHANTED_BOOK) ||
            enchantment.value().canEnchant(stack);

        Set<Holder<Enchantment>> existing = new HashSet<>(EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet());
        if (this.input != null)
        {
            existing.remove(this.input.enchantment());
        }
        boolean compatible = EnchantmentHelper.isEnchantmentCompatible(existing, enchantment);

        boolean flag = force || (acceptable && compatible);

        boolean input = this.input == null || this.input.test(stack);

        boolean upgrade = EnchantmentHelper.getEnchantmentsForCrafting(stack).getLevel(enchantment) < level;

        return flag && input && upgrade;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryManager)
    {
        return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level));
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return EIRecipes.EI_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return EIRecipes.INFUSION_RECIPE_TYPE.get();
    }
}
