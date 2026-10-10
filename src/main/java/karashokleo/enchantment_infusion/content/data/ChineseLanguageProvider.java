package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EITexts;
import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ChineseLanguageProvider extends LanguageProvider
{
    public ChineseLanguageProvider(PackOutput output)
    {
        super(output, EnchantmentInfusion.MOD_ID, "zh_cn");
    }

    @Override
    protected void addTranslations()
    {
        add(EIBlocks.INFUSION_TABLE.get(), "魔咒灌注台");
        add(EIBlocks.INFUSION_PEDESTAL.get(), "魔咒灌注基座");
        add(EITexts.PNF.key, "未找到基座！");
        add(EITexts.RNF.key, "未找到配方！");
        add(EITexts.EII.key, "灌注中断！");
        add(EITexts.CATEGORY.key, "魔咒灌注");
    }
}
