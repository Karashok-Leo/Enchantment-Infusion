package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.featuretoggle.FeatureFlags;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends BlockLootTableGenerator implements DataProvider
{
    private final DataOutput.PathResolver pathResolver;

    public BlockLootTableProvider(DataOutput output)
    {
        super(Set.of(), FeatureFlags.FEATURE_MANAGER.getFeatureSet());
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "loot_tables");
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer)
    {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Preserve the original loot tables without adding a named random sequence.
        accept((id, builder) -> futures.add(DataProvider.writeToPath(
            writer,
            LootGsons.getTableGsonBuilder().create().toJsonTree(builder.type(LootContextTypes.BLOCK).build(), LootTable.class),
            pathResolver.resolveJson(id)
        )));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName()
    {
        return "Enchantment Infusion Block Loot Tables";
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return List.of(EIBlocks.INFUSION_TABLE.get(), EIBlocks.INFUSION_PEDESTAL.get());
    }

    @Override
    public void generate()
    {
        addDrop(EIBlocks.INFUSION_TABLE.get(), this::nameableContainerDrops);
        addDrop(EIBlocks.INFUSION_PEDESTAL.get(), this::nameableContainerDrops);
    }
}
