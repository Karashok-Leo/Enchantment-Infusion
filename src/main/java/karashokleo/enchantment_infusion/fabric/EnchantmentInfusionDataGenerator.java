package karashokleo.enchantment_infusion.fabric;

import karashokleo.enchantment_infusion.content.data.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EnchantmentInfusionDataGenerator implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(EnglishLanguageProvider::new);
        pack.addProvider(ChineseLanguageProvider::new);
        pack.addProvider(ModelProvider::new);
        pack.addProvider(BlockLootTableProvider::new);
        pack.addProvider(BlockTagProvider::new);
    }
}
