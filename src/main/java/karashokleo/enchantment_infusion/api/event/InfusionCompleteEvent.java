package karashokleo.enchantment_infusion.api.event;

import karashokleo.enchantment_infusion.api.block.entity.InfusionInventory;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event;

public class InfusionCompleteEvent extends Event
{
    private final ServerWorld world;
    private final BlockPos pos;
    private final ItemStack output;
    private final InfusionInventory inventory;
    private final InfusionRecipe recipe;

    public InfusionCompleteEvent(
        ServerWorld world,
        BlockPos pos,
        ItemStack output,
        InfusionInventory inventory,
        InfusionRecipe recipe)
    {
        this.world = world;
        this.pos = pos.toImmutable();
        this.output = output;
        this.inventory = inventory;
        this.recipe = recipe;
    }

    public ServerWorld getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public ItemStack getOutput()
    {
        return output;
    }

    public InfusionInventory getInventory()
    {
        return inventory;
    }

    public InfusionRecipe getRecipe()
    {
        return recipe;
    }
}
