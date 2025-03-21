package karashokleo.enchantment_infusion.fabric;

import karashokleo.enchantment_infusion.api.render.InfusionTableTileRenderer;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class EnchantmentInfusionClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderLayer.getCutout(),
                EIBlocks.INFUSION_TABLE,
                EIBlocks.INFUSION_PEDESTAL
        );

        BlockEntityRendererFactories.register(EIBlocks.INFUSION_TABLE_TILE, ctx -> new InfusionTableTileRenderer<>(1.3F, ctx));
        BlockEntityRendererFactories.register(EIBlocks.INFUSION_PEDESTAL_TILE, ctx -> new InfusionTableTileRenderer<>(0.85F, ctx));
    }
}
