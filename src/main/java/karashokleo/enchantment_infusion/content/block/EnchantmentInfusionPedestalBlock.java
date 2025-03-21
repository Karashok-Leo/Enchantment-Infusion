package karashokleo.enchantment_infusion.content.block;

import karashokleo.enchantment_infusion.api.block.AbstractInfusionBlock;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionPedestalTile;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class EnchantmentInfusionPedestalBlock extends AbstractInfusionBlock
{
    protected static final VoxelShape SHAPE = VoxelShapes.union(
            // top
            Block.createCuboidShape(3.5, 6.0, 3.5, 12.5, 8.5, 12.5),
            // middle
            Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 6.0, 12.0),
            // bottom
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0)
    );

    public EnchantmentInfusionPedestalBlock()
    {
        super(
                FabricBlockSettings.create()
                        .mapColor(MapColor.BLACK)
                        .instrument(Instrument.BASEDRUM)
                        .strength(5.0f, 1200.0f)
                        .requiresTool()
                        .nonOpaque()
                        .luminance(state -> state.get(EIBlocks.INFUSING) ? 10 : 0)
        );
        this.setDefaultState(this.stateManager.getDefaultState().with(EIBlocks.INFUSING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(EIBlocks.INFUSING);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new EnchantmentInfusionPedestalTile(pos, state);
    }
}
