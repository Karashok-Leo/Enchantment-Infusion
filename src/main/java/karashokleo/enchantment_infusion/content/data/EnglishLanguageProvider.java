package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EITexts;
import net.minecraft.data.DataOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class EnglishLanguageProvider extends LanguageProvider
{
    public EnglishLanguageProvider(DataOutput dataOutput)
    {
        super(dataOutput, EnchantmentInfusion.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations()
    {
        add(EIBlocks.INFUSION_TABLE.get(), "Enchantment Infusion Table");
        add(EIBlocks.INFUSION_PEDESTAL.get(), "Enchantment Infusion Pedestal");
        add(EITexts.PNF.key, "Pedestal not found!");
        add(EITexts.RNF.key, "Recipe not found!");
        add(EITexts.EII.key, "Enchantment infusion interrupted!");
        add(EITexts.CATEGORY.key, "Enchantment Infusion");
    }
}
