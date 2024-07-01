package karashokleo.enchantment_infusion.api.block;

import karashokleo.enchantment_infusion.api.block.entity.AbstractInfusionTile;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public abstract class AbstractInfusionBlock extends BlockWithEntity
{
    protected AbstractInfusionBlock(Settings settings)
    {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (hand == Hand.OFF_HAND)
            return ActionResult.PASS;
        if (world instanceof ServerWorld serverWorld && world.getBlockEntity(pos) instanceof AbstractInfusionTile tile)
            tile.onUse(serverWorld, pos, player);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName() && world.getBlockEntity(pos) instanceof AbstractInfusionTile tile) {
           tile.setCustomName(itemStack.getName());
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (state.isOf(newState.getBlock())) return;
        if (world.getBlockEntity(pos) instanceof AbstractInfusionTile tile)
        {
            if (world instanceof ServerWorld)
                ItemScatterer.spawn(world, pos, tile);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos)
    {
        return (world.getBlockEntity(pos) instanceof SingleStackInventory inventory && !inventory.getStack().isEmpty()) ? 15 : 0;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type)
    {
        return false;
    }
}
