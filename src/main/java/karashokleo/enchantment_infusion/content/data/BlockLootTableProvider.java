package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class BlockLootTableProvider extends FabricBlockLootTableProvider
{
    public BlockLootTableProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput);
    }

    @Override
    public void generate()
    {
        addDrop(EIBlocks.INFUSION_TABLE, this::nameableContainerDrops);
        addDrop(EIBlocks.INFUSION_PEDESTAL, this::nameableContainerDrops);
    }
}
