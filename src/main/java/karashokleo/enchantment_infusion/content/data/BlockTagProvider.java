package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, ExistingFileHelper existingFileHelper)
    {
        super(output, registriesFuture, EnchantmentInfusion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries)
    {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIBlocks.INFUSION_TABLE.get(), EIBlocks.INFUSION_PEDESTAL.get());
    }
}
