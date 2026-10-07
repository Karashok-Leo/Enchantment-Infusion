package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Set;

public class BlockLootTableProvider extends BlockLootSubProvider
{
    public BlockLootTableProvider(HolderLookup.Provider registries)
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate()
    {
        add(EIBlocks.INFUSION_TABLE, this::createNameableBlockEntityTable);
        add(EIBlocks.INFUSION_PEDESTAL, this::createNameableBlockEntityTable);
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return List.of(EIBlocks.INFUSION_TABLE, EIBlocks.INFUSION_PEDESTAL);
    }
}
