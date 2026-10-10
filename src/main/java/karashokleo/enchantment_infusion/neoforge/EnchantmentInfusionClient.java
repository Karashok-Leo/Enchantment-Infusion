package karashokleo.enchantment_infusion.neoforge;

import karashokleo.enchantment_infusion.api.render.InfusionTableTileRenderer;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = EnchantmentInfusion.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EnchantmentInfusionClient
{
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(EIBlocks.INFUSION_TABLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(EIBlocks.INFUSION_PEDESTAL.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(EIBlocks.INFUSION_TABLE_TILE.get(), ctx -> new InfusionTableTileRenderer<>(1.3F, ctx));
        event.registerBlockEntityRenderer(EIBlocks.INFUSION_PEDESTAL_TILE.get(), ctx -> new InfusionTableTileRenderer<>(0.85F, ctx));
    }
}
