package karashokleo.enchantment_infusion.api.recipe;

import karashokleo.enchantment_infusion.api.block.entity.InfusionInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public interface InfusionRecipe extends Recipe<InfusionInventory>
{
    Ingredient getTableIngredient();

    DefaultedList<Ingredient> getPedestalIngredient();

    ItemStack infuse(ItemStack tableStack);

    default boolean matchTableStack(ItemStack stack)
    {
        return getTableIngredient().test(stack);
    }

    default boolean matchPedestalStacks(List<ItemStack> stacks)
    {
        RecipeMatcher recipeMatcher = new RecipeMatcher();
        int i = 0;
        for (ItemStack itemStack : stacks)
        {
            if (itemStack.isEmpty()) continue;
            ++i;
            recipeMatcher.addInput(itemStack, 1);
        }
        return i == getIngredients().size() && recipeMatcher.match(this, null);
    }

    @Override
    default boolean matches(InfusionInventory inventory, World world)
    {
        return this.matchTableStack(inventory.getTableStack()) &&
                this.matchPedestalStacks(inventory.getPedestalStacks());
    }

    @Override
    default ItemStack craft(InfusionInventory inventory, DynamicRegistryManager registryManager)
    {
        return infuse(inventory.getTableStack());
    }

    @Override
    default DefaultedList<Ingredient> getIngredients()
    {
        return getPedestalIngredient();
    }

    @Override
    default boolean fits(int width, int height)
    {
        return false;
    }

    @Override
    default boolean isIgnoredInRecipeBook()
    {
        return true;
    }
}
