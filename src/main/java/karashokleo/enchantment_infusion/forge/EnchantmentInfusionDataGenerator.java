package karashokleo.enchantment_infusion.forge;

import karashokleo.enchantment_infusion.content.data.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnchantmentInfusion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EnchantmentInfusionDataGenerator
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        DataOutput output = generator.getPackOutput();
        generator.addProvider(event.includeServer(), new RecipeProvider(output));
        generator.addProvider(event.includeClient(), new EnglishLanguageProvider(output));
        generator.addProvider(event.includeClient(), new ChineseLanguageProvider(output));
        generator.addProvider(event.includeClient(), new ModelProvider(output, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new BlockLootTableProvider(output));
        generator.addProvider(event.includeServer(), new BlockTagProvider(output, event.getLookupProvider(), event.getExistingFileHelper()));
    }
}
