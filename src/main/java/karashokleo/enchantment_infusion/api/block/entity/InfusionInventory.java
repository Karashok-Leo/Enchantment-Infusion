package karashokleo.enchantment_infusion.api.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class InfusionInventory implements Inventory
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
            setStack(i, remainder.get(i));
    }

    @Override
    public int size()
    {
        return SIZE;
    }

    @Override
    public boolean isEmpty()
    {
        for (AbstractInfusionTile inv : this.pedestalInventory)
            if (!inv.isEmpty()) return false;
        return tableInventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return pedestalInventory.get(slot).getStack();
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        return pedestalInventory.get(slot).removeStack(amount);
    }

    @Override
    public ItemStack removeStack(int slot)
    {
        return pedestalInventory.get(slot).removeStack();
    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {
        pedestalInventory.get(slot).setStack(stack);
    }

    @Override
    public void markDirty()
    {
        tableInventory.markDirty();
        pedestalInventory.forEach(BlockEntity::markDirty);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        return false;
    }

    @Override
    public void clear()
    {
        pedestalInventory.forEach(SingleStackInventory::clear);
    }
}
