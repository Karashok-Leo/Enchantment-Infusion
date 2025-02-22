package karashokleo.enchantment_infusion.api.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractInfusionTile extends NameableSingleStackTile
{
    protected AbstractInfusionTile(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void onUse(ServerWorld world, BlockPos pos, PlayerEntity player)
    {
        swapStack(player.getInventory());
    }

    private void swapStack(PlayerInventory playerInv)
    {
        ItemStack player2tile = playerInv.removeStack(playerInv.selectedSlot, 1);
        ItemStack tile2player = this.removeStack();
        this.setStack(player2tile);
        playerInv.offerOrDrop(tile2player);
    }
}
