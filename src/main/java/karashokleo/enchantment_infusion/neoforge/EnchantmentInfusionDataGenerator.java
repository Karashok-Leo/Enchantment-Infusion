package karashokleo.enchantment_infusion.neoforge;

import karashokleo.enchantment_infusion.content.data.BlockLootTableProvider;
import karashokleo.enchantment_infusion.content.data.BlockTagProvider;
import karashokleo.enchantment_infusion.content.data.ChineseLanguageProvider;
import karashokleo.enchantment_infusion.content.data.EnglishLanguageProvider;
import karashokleo.enchantment_infusion.content.data.ModelProvider;
import karashokleo.enchantment_infusion.content.data.RecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EnchantmentInfusionDataGenerator
{
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new RecipeProvider(output, registries));
        generator.addProvider(event.includeClient(), new EnglishLanguageProvider(output));
        generator.addProvider(event.includeClient(), new ChineseLanguageProvider(output));
        generator.addProvider(event.includeClient(), new ModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(),
            List.of(new LootTableProvider.SubProviderEntry(BlockLootTableProvider::new, LootContextParamSets.BLOCK)), registries));
        generator.addProvider(event.includeServer(), new BlockTagProvider(output, registries, existingFileHelper));
    }
}
