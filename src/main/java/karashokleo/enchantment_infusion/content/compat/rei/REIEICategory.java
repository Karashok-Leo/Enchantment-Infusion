package karashokleo.enchantment_infusion.content.compat.rei;

import karashokleo.enchantment_infusion.api.compat.rei.AbstractREIInfusionCategory;
import karashokleo.enchantment_infusion.api.compat.rei.AbstractREIInfusionDisplay;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EITexts;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;

public class REIEICategory extends AbstractREIInfusionCategory
{
    @Override
    public CategoryIdentifier<? extends AbstractREIInfusionDisplay> getCategoryIdentifier()
    {
        return REICompat.EI;
    }

    @Override
    public Component getTitle()
    {
        return EITexts.CATEGORY.get();
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(EIBlocks.INFUSION_TABLE);
    }
}
