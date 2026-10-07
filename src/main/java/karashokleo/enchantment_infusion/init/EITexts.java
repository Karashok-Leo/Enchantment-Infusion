package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum EITexts
{
    PNF("text", "pedestal_not_found"),
    RNF("text", "recipe_not_found"),
    EII("text", "enchantment_infusion_interrupt"),
    CATEGORY("compat", "enchantment_infusion_title");

    public final String key;

    EITexts(String type, String path)
    {
        this(Util.makeDescriptionId(type, EnchantmentInfusion.id(path)));
    }

    EITexts(String key)
    {
        this.key = key;
    }

    public MutableComponent get()
    {
        return Component.translatable(key);
    }
}
