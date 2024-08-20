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

public record EnchantmentInfusionRecipe(
        Identifier id,
        @Nullable EnchantmentIngredient input,
        DefaultedList<Ingredient> ingredients,
        Enchantment enchantment,
        Mode mode,
        int level,
        boolean force
) implements InfusionRecipe
{
    public enum Mode
    {
        ADD, SET
    }

    @Override
    public Ingredient getTableIngredient()
    {
        if (input != null) return input.toVanilla();
        else return switch (mode)
        {
            case ADD -> EnchantmentIngredient.of(enchantment, level - 1);
            case SET -> Ingredient.ofItems(Items.BOOK);
        };
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
        enchantments.put(enchantment, level);
        EnchantmentHelper.set(enchantments, stack);
        return stack;
    }

    @Override
    public boolean matchTableStack(ItemStack stack)
    {
        boolean acceptable = stack.isOf(Items.BOOK) ||
                stack.isOf(Items.ENCHANTED_BOOK) ||
                enchantment.isAcceptableItem(stack);

        boolean input = this.input != null && this.input.test(stack);

        boolean compatible = EnchantmentHelper.isCompatible(EnchantmentHelper.get(stack).keySet(), enchantment);

        boolean flag = force || (acceptable && input && compatible);

        int current_level = EnchantmentHelper.get(stack).getOrDefault(enchantment, 0);
        return switch (mode)
        {
            case ADD -> flag && current_level == level - 1;
            case SET -> flag && current_level < level;
        };
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
