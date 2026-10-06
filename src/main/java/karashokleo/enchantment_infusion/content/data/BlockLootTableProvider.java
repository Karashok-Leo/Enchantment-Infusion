package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider
{
    public BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate()
    {
        addDrop(EIBlocks.INFUSION_TABLE, this::nameableContainerDrops);
        addDrop(EIBlocks.INFUSION_PEDESTAL, this::nameableContainerDrops);
    }
}
