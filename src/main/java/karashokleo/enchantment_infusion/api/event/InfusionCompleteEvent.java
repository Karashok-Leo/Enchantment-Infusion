package karashokleo.enchantment_infusion.api.event;

import karashokleo.enchantment_infusion.api.block.entity.InfusionInventory;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class InfusionCompleteEvent extends Event
{
    private final ServerLevel world;
    private final BlockPos pos;
    private final ItemStack output;
    private final InfusionInventory inventory;
    private final InfusionRecipe recipe;

    public InfusionCompleteEvent(ServerLevel world, BlockPos pos, ItemStack output, InfusionInventory inventory, InfusionRecipe recipe)
    {
        this.world = world;
        this.pos = pos;
        this.output = output;
        this.inventory = inventory;
        this.recipe = recipe;
    }

    public ServerLevel getWorld()
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
