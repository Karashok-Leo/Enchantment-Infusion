package karashokleo.enchantment_infusion.api.recipe;

import karashokleo.enchantment_infusion.api.block.entity.InfusionInventory;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import java.util.List;

public interface InfusionRecipe extends Recipe<InfusionInventory>
{
    Ingredient getTableIngredient();

    NonNullList<Ingredient> getPedestalIngredient();

    ItemStack infuse(ItemStack tableStack);

    default boolean matchTableStack(ItemStack stack)
    {
        return getTableIngredient().test(stack);
    }

    default boolean matchPedestalStacks(List<ItemStack> stacks)
    {
        StackedContents recipeMatcher = new StackedContents();
        int i = 0;
        for (ItemStack itemStack : stacks)
        {
            if (itemStack.isEmpty())
            {
                continue;
            }
            ++i;
            recipeMatcher.accountStack(itemStack, 1);
        }
        return i == getIngredients().size() && recipeMatcher.canCraft(this, null);
    }

    @Override
    default boolean matches(InfusionInventory inventory, Level world)
    {
        return this.matchTableStack(inventory.getTableStack()) &&
            this.matchPedestalStacks(inventory.getPedestalStacks());
    }

    @Override
    default ItemStack assemble(InfusionInventory inventory, HolderLookup.Provider registryManager)
    {
        return infuse(inventory.getTableStack().copy());
    }

    @Override
    default NonNullList<Ingredient> getIngredients()
    {
        return getPedestalIngredient();
    }

    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return false;
    }

    @Override
    default boolean isSpecial()
    {
        return true;
    }
}
