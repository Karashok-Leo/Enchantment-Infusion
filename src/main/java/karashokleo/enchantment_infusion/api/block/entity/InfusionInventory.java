package karashokleo.enchantment_infusion.api.block.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    @NotNull
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

    public void setStack(int slot, ItemStack stack)
    {
        pedestalInventory.get(slot).setTheItem(stack);
    }
}
