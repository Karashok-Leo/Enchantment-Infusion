package karashokleo.enchantment_infusion.forge;

import karashokleo.enchantment_infusion.api.render.InfusionTableTileRenderer;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EnchantmentInfusion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EnchantmentInfusionClient
{
    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            RenderLayers.setRenderLayer(EIBlocks.INFUSION_TABLE.get(), RenderLayer.getCutout());
            RenderLayers.setRenderLayer(EIBlocks.INFUSION_PEDESTAL.get(), RenderLayer.getCutout());
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(EIBlocks.INFUSION_TABLE_TILE.get(), ctx -> new InfusionTableTileRenderer<>(1.3F, ctx));
        event.registerBlockEntityRenderer(EIBlocks.INFUSION_PEDESTAL_TILE.get(), ctx -> new InfusionTableTileRenderer<>(0.85F, ctx));
    }
}
