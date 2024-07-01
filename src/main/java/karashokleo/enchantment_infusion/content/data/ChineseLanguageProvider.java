package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EITexts;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ChineseLanguageProvider extends FabricLanguageProvider
{
    public ChineseLanguageProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput, "zh_cn");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder)
    {
        translationBuilder.add(EIBlocks.INFUSION_TABLE, "魔咒灌注台");
        translationBuilder.add(EIBlocks.INFUSION_PEDESTAL, "魔咒灌注基座");
        translationBuilder.add(EITexts.PNF.key, "未找到基座！");
        translationBuilder.add(EITexts.RNF.key, "未找到配方！");
        translationBuilder.add(EITexts.EII.key, "灌注中断！");
        translationBuilder.add(EITexts.CATEGORY.key, "魔咒灌注");
    }
}
