package karashokleo.enchantment_infusion.content.block;

import karashokleo.enchantment_infusion.api.block.AbstractInfusionBlock;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionTableTile;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class EnchantmentInfusionTableBlock extends AbstractInfusionBlock
{
    protected static final VoxelShape TOP = Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 12.0, 16.0);
    protected static final VoxelShape MID_TOP = Block.createCuboidShape(2.0, 9.0, 2.0, 14.0, 10.0, 14.0);
    protected static final VoxelShape BODY = Block.createCuboidShape(4.0, 3.0, 4.0, 12.0, 9.0, 12.0);
    protected static final VoxelShape MID_BOTTOM = Block.createCuboidShape(3.0, 2.0, 3.0, 13.0, 3.0, 13.0);
    protected static final VoxelShape BOTTOM = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0);
    protected static final VoxelShape CORNER_1 = Block.createCuboidShape(0.0, 12.0, 13.0, 3.0, 13.0, 16.0);
    protected static final VoxelShape CORNER_2 = Block.createCuboidShape(13.0, 12.0, 13.0, 16.0, 13.0, 16.0);
    protected static final VoxelShape CORNER_3 = Block.createCuboidShape(0.0, 12.0, 0.0, 3.0, 13.0, 3.0);
    protected static final VoxelShape CORNER_4 = Block.createCuboidShape(13.0, 12.0, 0.0, 16.0, 13.0, 3.0);
    protected static final VoxelShape SHAPE = VoxelShapes.union(TOP, MID_TOP, BODY, MID_BOTTOM, BOTTOM, CORNER_1, CORNER_2, CORNER_3, CORNER_4);

    public EnchantmentInfusionTableBlock()
    {
        super(
                FabricBlockSettings.create()
                        .mapColor(MapColor.BLACK)
                        .instrument(Instrument.BASEDRUM)
                        .strength(5.0f, 1200.0f)
                        .requiresTool()
                        .nonOpaque()
        );
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
