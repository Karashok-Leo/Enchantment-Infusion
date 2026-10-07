package karashokleo.enchantment_infusion.content.block;

import karashokleo.enchantment_infusion.api.block.AbstractInfusionBlock;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionTableTile;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class EnchantmentInfusionTableBlock extends AbstractInfusionBlock
{
    protected static final VoxelShape SHAPE = Shapes.or(
        // top
        Block.box(1.0, 4.0, 1.0, 15.0, 13.0, 15.0),
        // bottom
        Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
        // corner
        Block.box(0.05, 12.0, 0.05, 3.05, 14.0, 3.05),
        Block.box(0.05, 12.0, 12.95, 3.0, 14.0, 15.95),
        Block.box(12.95, 12.0, 12.95, 15.95, 14.0, 15.95),
        Block.box(12.95, 12.0, 0.0, 15.95, 14.0, 3.0)
    );

    public static final MapCodec<EnchantmentInfusionTableBlock> CODEC = simpleCodec(EnchantmentInfusionTableBlock::new);

    public EnchantmentInfusionTableBlock()
    {
        this(
            Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(5.0f, 1200.0f)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .lightLevel(state -> state.getValue(EIBlocks.INFUSING) ? 12 : 0)
        );
    }

    public EnchantmentInfusionTableBlock(Properties settings)
    {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(EIBlocks.INFUSING, false));
    }

    @Override
    protected MapCodec<EnchantmentInfusionTableBlock> codec()
    {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(EIBlocks.INFUSING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new EnchantmentInfusionTableTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type)
    {
        return world.isClientSide ? null : createTickerHelper(type, EIBlocks.INFUSION_TABLE_TILE, EnchantmentInfusionTableTile::serverTick);
    }
}
