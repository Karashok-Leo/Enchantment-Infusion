package karashokleo.enchantment_infusion.content.block;

import karashokleo.enchantment_infusion.api.block.AbstractInfusionBlock;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionPedestalTile;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
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
public class EnchantmentInfusionPedestalBlock extends AbstractInfusionBlock
{
    protected static final VoxelShape SHAPE = Shapes.or(
        // top
        Block.box(3.5, 6.0, 3.5, 12.5, 8.5, 12.5),
        // middle
        Block.box(4.0, 4.0, 4.0, 12.0, 6.0, 12.0),
        // bottom
        Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0)
    );

    public static final MapCodec<EnchantmentInfusionPedestalBlock> CODEC = simpleCodec(EnchantmentInfusionPedestalBlock::new);

    public EnchantmentInfusionPedestalBlock()
    {
        this(
            Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(5.0f, 1200.0f)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .lightLevel(state -> state.getValue(EIBlocks.INFUSING) ? 10 : 0)
        );
    }

    public EnchantmentInfusionPedestalBlock(Properties settings)
    {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(EIBlocks.INFUSING, false));
    }

    @Override
    protected MapCodec<EnchantmentInfusionPedestalBlock> codec()
    {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(EIBlocks.INFUSING);
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new EnchantmentInfusionPedestalTile(pos, state);
    }
}
