package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.fabric.EnchantmentInfusion;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public enum EITexts
{
    PNF("text","pedestal_not_found"),
    RNF("text","recipe_not_found"),
    EII("text","enchantment_infusion_interrupt"),
    CATEGORY("compat", "enchantment_infusion_title")
    ;

    public final String key;

    EITexts(String type, String path)
    {
        this(Util.createTranslationKey(type, EnchantmentInfusion.id(path)));
    }

    EITexts(String key)
    {
        this.key = key;
    }

    public MutableText get()
    {
        return Text.translatable(key);
    }
}
