package karashokleo.enchantment_infusion.content.block;

import karashokleo.enchantment_infusion.api.block.AbstractInfusionBlock;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionTableTile;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class EnchantmentInfusionTableBlock extends AbstractInfusionBlock
{
    protected static final VoxelShape SHAPE = VoxelShapes.union(
            // top
            Block.createCuboidShape(1.0, 4.0, 1.0, 15.0, 13.0, 15.0),
            // bottom
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            // corner
            Block.createCuboidShape(0.05, 12.0, 0.05, 3.05, 14.0, 3.05),
            Block.createCuboidShape(0.05, 12.0, 12.95, 3.0, 14.0, 15.95),
            Block.createCuboidShape(12.95, 12.0, 12.95, 15.95, 14.0, 15.95),
            Block.createCuboidShape(12.95, 12.0, 0.0, 15.95, 14.0, 3.0)
    );

    public EnchantmentInfusionTableBlock()
    {
        super(
                FabricBlockSettings.create()
                        .mapColor(MapColor.BLACK)
                        .instrument(Instrument.BASEDRUM)
                        .strength(5.0f, 1200.0f)
                        .requiresTool()
                        .nonOpaque()
                        .luminance(state -> state.get(EIBlocks.INFUSING) ? 12 : 0)
        );
        this.setDefaultState(this.stateManager.getDefaultState().with(EIBlocks.INFUSING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(EIBlocks.INFUSING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new EnchantmentInfusionTableTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return world.isClient ? null : checkType(type, EIBlocks.INFUSION_TABLE_TILE, EnchantmentInfusionTableTile::serverTick);
    }
}
