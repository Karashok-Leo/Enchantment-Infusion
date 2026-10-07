package karashokleo.enchantment_infusion.api.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractInfusionTile extends NameableSingleStackTile
{
    protected AbstractInfusionTile(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void onUse(ServerLevel world, BlockPos pos, Player player)
    {
        swapStack(player.getInventory());
    }

    private void swapStack(Inventory playerInv)
    {
        ItemStack player2tile = playerInv.removeItem(playerInv.selected, 1);
        ItemStack tile2player = this.removeTheItem();
        this.setTheItem(player2tile);
        playerInv.placeItemBackInInventory(tile2player);
    }
}
