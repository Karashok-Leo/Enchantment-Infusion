package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, ExistingFileHelper existingFileHelper)
    {
        super(output, registriesFuture, EnchantmentInfusion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
            .add(EIBlocks.INFUSION_TABLE.get(), EIBlocks.INFUSION_PEDESTAL.get());
    }
}
