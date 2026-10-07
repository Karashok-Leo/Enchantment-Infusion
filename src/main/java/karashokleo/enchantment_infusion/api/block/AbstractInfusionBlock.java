package karashokleo.enchantment_infusion.api.block;

import karashokleo.enchantment_infusion.api.block.entity.AbstractInfusionTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.ContainerSingleItem;

@SuppressWarnings("deprecation")
public abstract class AbstractInfusionBlock extends BaseEntityBlock
{
    protected AbstractInfusionBlock(Properties settings)
    {
        super(settings);
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (hand == InteractionHand.OFF_HAND)
        {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (world instanceof ServerLevel serverWorld && world.getBlockEntity(pos) instanceof AbstractInfusionTile tile)
        {
            tile.onUse(serverWorld, pos, player);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
    {
        if (world instanceof ServerLevel serverWorld && world.getBlockEntity(pos) instanceof AbstractInfusionTile tile)
        {
            tile.onUse(serverWorld, pos, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack)
    {
        if (itemStack.has(DataComponents.CUSTOM_NAME) && world.getBlockEntity(pos) instanceof AbstractInfusionTile tile)
        {
            tile.setCustomName(itemStack.getHoverName());
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (state.is(newState.getBlock()))
        {
            return;
        }
        if (world.getBlockEntity(pos) instanceof AbstractInfusionTile tile)
        {
            if (world instanceof ServerLevel)
            {
                Containers.dropContents(world, pos, tile);
            }
            world.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos)
    {
        return (world.getBlockEntity(pos) instanceof ContainerSingleItem inventory && !inventory.getTheItem().isEmpty()) ? 15 : 0;
    }

    public boolean isPathfindable(BlockState state, PathComputationType type)
    {
        return false;
    }
}
