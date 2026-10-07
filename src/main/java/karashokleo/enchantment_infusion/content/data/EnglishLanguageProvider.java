package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EITexts;
import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnglishLanguageProvider extends LanguageProvider
{
    public EnglishLanguageProvider(PackOutput output)
    {
        super(output, EnchantmentInfusion.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations()
    {
        add(EIBlocks.INFUSION_TABLE, "Enchantment Infusion Table");
        add(EIBlocks.INFUSION_PEDESTAL, "Enchantment Infusion Pedestal");
        add(EITexts.PNF.key, "Pedestal not found!");
        add(EITexts.RNF.key, "Recipe not found!");
        add(EITexts.EII.key, "Enchantment infusion interrupted!");
        add(EITexts.CATEGORY.key, "Enchantment Infusion");
    }
}
