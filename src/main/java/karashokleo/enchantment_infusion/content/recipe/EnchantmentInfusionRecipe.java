package karashokleo.enchantment_infusion.content.recipe;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DefaultedList;
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
    DefaultedList<Ingredient> ingredients,
    RegistryEntry<Enchantment> enchantment,
    int level,
    boolean force
) implements InfusionRecipe
{
    @Override
    public Ingredient getTableIngredient()
    {
        return input == null ? Ingredient.ofItems(Items.BOOK) : input.toVanilla();
    }

    @Override
    public DefaultedList<Ingredient> getPedestalIngredient()
    {
        return ingredients;
    }

    @Override
    public ItemStack infuse(ItemStack tableStack)
    {
        ItemStack stack = tableStack.isOf(Items.BOOK) ? Items.ENCHANTED_BOOK.getDefaultStack() : tableStack;

        ItemEnchantmentsComponent.Builder enchantments = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(stack));
        if (input != null)
        {
            enchantments.remove(input.enchantment()::equals);
        }
        enchantments.set(enchantment, level);

        stack.remove(DataComponentTypes.ENCHANTMENTS);
        stack.remove(DataComponentTypes.STORED_ENCHANTMENTS);
        EnchantmentHelper.set(stack, enchantments.build());
        return stack;
    }

    @Override
    public boolean matchTableStack(ItemStack stack)
    {
        boolean acceptable = stack.isOf(Items.BOOK) ||
            stack.isOf(Items.ENCHANTED_BOOK) ||
            enchantment.value().isAcceptableItem(stack);

        Set<RegistryEntry<Enchantment>> existing = new HashSet<>(EnchantmentHelper.getEnchantments(stack).getEnchantments());
        if (this.input != null)
        {
            existing.remove(this.input.enchantment());
        }
        boolean compatible = EnchantmentHelper.isCompatible(existing, enchantment);

        boolean flag = force || (acceptable && compatible);

        boolean input = this.input == null || this.input.test(stack);

        boolean upgrade = EnchantmentHelper.getEnchantments(stack).getLevel(enchantment) < level;

        return flag && input && upgrade;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registryManager)
    {
        return EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level));
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return EIRecipes.EI_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return EIRecipes.INFUSION_RECIPE_TYPE;
    }
}
