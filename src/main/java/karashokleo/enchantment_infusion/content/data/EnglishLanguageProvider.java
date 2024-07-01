package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EITexts;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class EnglishLanguageProvider extends FabricLanguageProvider
{
    public EnglishLanguageProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder)
    {
        translationBuilder.add(EIBlocks.INFUSION_TABLE, "Enchantment Infusion Table");
        translationBuilder.add(EIBlocks.INFUSION_PEDESTAL, "Enchantment Infusion Pedestal");
        translationBuilder.add(EITexts.PNF.key, "Pedestal not found!");
        translationBuilder.add(EITexts.RNF.key, "Recipe not found!");
        translationBuilder.add(EITexts.EII.key, "Enchantment infusion interrupted!");
        translationBuilder.add(EITexts.CATEGORY.key, "Enchantment Infusion");
    }
}
