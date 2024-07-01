package karashokleo.enchantment_infusion.content.block.entity;

import karashokleo.enchantment_infusion.api.block.entity.AbstractInfusionTile;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class EnchantmentInfusionPedestalTile extends AbstractInfusionTile
{
    public EnchantmentInfusionPedestalTile(BlockPos pos, BlockState state)
    {
        super(EIBlocks.INFUSION_PEDESTAL_TILE, pos, state);
    }
}
