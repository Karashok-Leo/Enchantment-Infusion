package karashokleo.enchantment_infusion.api.block.entity;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.ticks.ContainerSingleItem;

public class InfusionInventory implements RecipeInput
{
    private static final int SIZE = 8;
    private final AbstractInfusionTile tableInventory;
    private final NonNullList<AbstractInfusionTile> pedestalInventory;

    public InfusionInventory(AbstractInfusionTile tableInventory, NonNullList<AbstractInfusionTile> pedestalInventory)
    {
        this.tableInventory = tableInventory;
        this.pedestalInventory = pedestalInventory;
    }

    public ItemStack getTableStack()
    {
        return tableInventory.getTheItem();
    }

    public List<ItemStack> getPedestalStacks()
    {
        return pedestalInventory.stream().map(ContainerSingleItem::getTheItem).toList();
    }

    public void setRemainder(NonNullList<ItemStack> remainder)
    {
        for (int i = 0; i < SIZE; i++)
        {
            setStack(i, remainder.get(i));
        }
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return getStack(slot);
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
        return pedestalInventory.get(slot).getTheItem();
    }

    public ItemStack removeStack(int slot, int amount)
    {
        return pedestalInventory.get(slot).splitTheItem(amount);
    }

    public ItemStack removeStack(int slot)
    {
        return pedestalInventory.get(slot).removeTheItem();
    }

    public void setStack(int slot, ItemStack stack)
    {
        pedestalInventory.get(slot).setTheItem(stack);
    }

    public void markDirty()
    {
        tableInventory.setChanged();
        pedestalInventory.forEach(BlockEntity::setChanged);
    }

    public boolean canPlayerUse(Player player)
    {
        return false;
    }

    public void clear()
    {
        pedestalInventory.forEach(ContainerSingleItem::clearContent);
    }
}
