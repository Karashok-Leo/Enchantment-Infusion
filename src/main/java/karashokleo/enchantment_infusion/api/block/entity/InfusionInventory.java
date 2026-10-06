package karashokleo.enchantment_infusion.api.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class InfusionInventory implements RecipeInput
{
    private static final int SIZE = 8;
    private final AbstractInfusionTile tableInventory;
    private final DefaultedList<AbstractInfusionTile> pedestalInventory;

    public InfusionInventory(AbstractInfusionTile tableInventory, DefaultedList<AbstractInfusionTile> pedestalInventory)
    {
        this.tableInventory = tableInventory;
        this.pedestalInventory = pedestalInventory;
    }

    public ItemStack getTableStack()
    {
        return tableInventory.getStack();
    }

    public List<ItemStack> getPedestalStacks()
    {
        return pedestalInventory.stream().map(SingleStackInventory::getStack).toList();
    }

    public void setRemainder(DefaultedList<ItemStack> remainder)
    {
        for (int i = 0; i < SIZE; i++)
        {
            setStack(i, remainder.get(i));
        }
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return getStack(slot);
    }

    @Override
    public int getSize()
    {
        return size();
    }

    public int size()
    {
        return SIZE;
    }

    @Override
    public boolean isEmpty()
    {
        for (AbstractInfusionTile inv : this.pedestalInventory)
        {
            if (!inv.isEmpty())
            {
                return false;
            }
        }
        return tableInventory.isEmpty();
    }

    public ItemStack getStack(int slot)
    {
        return pedestalInventory.get(slot).getStack();
    }

    public ItemStack removeStack(int slot, int amount)
    {
        return pedestalInventory.get(slot).decreaseStack(amount);
    }

    public ItemStack removeStack(int slot)
    {
        return pedestalInventory.get(slot).emptyStack();
    }

    public void setStack(int slot, ItemStack stack)
    {
        pedestalInventory.get(slot).setStack(stack);
    }

    public void markDirty()
    {
        tableInventory.markDirty();
        pedestalInventory.forEach(BlockEntity::markDirty);
    }

    public boolean canPlayerUse(PlayerEntity player)
    {
        return false;
    }

    public void clear()
    {
        pedestalInventory.forEach(SingleStackInventory::clear);
    }
}
