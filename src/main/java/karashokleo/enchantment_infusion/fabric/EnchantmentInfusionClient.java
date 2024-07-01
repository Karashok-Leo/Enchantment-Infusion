package karashokleo.enchantment_infusion.fabric;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.api.render.InfusionTableTileRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class EnchantmentInfusionClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BlockEntityRendererFactories.register(EIBlocks.INFUSION_TABLE_TILE, ctx -> new InfusionTableTileRenderer<>(1.2F, ctx));
        BlockEntityRendererFactories.register(EIBlocks.INFUSION_PEDESTAL_TILE, ctx -> new InfusionTableTileRenderer<>(1.05F, ctx));
    }
}
