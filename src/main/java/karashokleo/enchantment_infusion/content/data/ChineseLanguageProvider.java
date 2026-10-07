package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EITexts;
import net.minecraft.data.DataOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ChineseLanguageProvider extends LanguageProvider
{
    public ChineseLanguageProvider(DataOutput dataOutput)
    {
        super(dataOutput, EnchantmentInfusion.MOD_ID, "zh_cn");
    }

    @Override
    protected void addTranslations()
    {
        add(EIBlocks.INFUSION_TABLE, "魔咒灌注台");
        add(EIBlocks.INFUSION_PEDESTAL, "魔咒灌注基座");
        add(EITexts.PNF.key, "未找到基座！");
        add(EITexts.RNF.key, "未找到配方！");
        add(EITexts.EII.key, "灌注中断！");
        add(EITexts.CATEGORY.key, "魔咒灌注");
    }
}
