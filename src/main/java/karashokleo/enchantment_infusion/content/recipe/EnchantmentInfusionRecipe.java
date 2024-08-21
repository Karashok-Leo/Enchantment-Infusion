package karashokleo.enchantment_infusion.content.recipe;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @param id          Recipe id
 * @param input       The enchantment that table stack should have
 * @param ingredients Ingredients on the pedestals
 * @param enchantment The output enchantment
 * @param level       The output level
 * @param force       Ignore enchantment target matching and enchantment compatibility
 */
public record EnchantmentInfusionRecipe(
        Identifier id,
        @Nullable EnchantmentIngredient input,
        DefaultedList<Ingredient> ingredients,
        Enchantment enchantment,
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

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        if (input != null) enchantments.remove(input.enchantment());
        enchantments.put(enchantment, level);

        tableStack.removeSubNbt("Enchantments");
        tableStack.removeSubNbt("StoredEnchantments");
        EnchantmentHelper.set(enchantments, stack);
        return stack;
    }

    @Override
    public boolean matchTableStack(ItemStack stack)
    {
        boolean acceptable = stack.isOf(Items.BOOK) ||
                stack.isOf(Items.ENCHANTED_BOOK) ||
                enchantment.isAcceptableItem(stack);

        boolean compatible = EnchantmentHelper.isCompatible(EnchantmentHelper.get(stack).keySet(), enchantment);

        boolean flag = force || (acceptable && compatible);

        boolean input = this.input == null || this.input.test(stack);

        boolean upgrade = EnchantmentHelper.get(stack).getOrDefault(enchantment, 0) < level;

        return flag && input && upgrade;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager)
    {
        return infuse(Items.BOOK.getDefaultStack());
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return EIRecipes.EI_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return EIRecipes.EI_TYPE;
    }
}
