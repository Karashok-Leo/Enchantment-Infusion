package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

public class EIItems
{
    public static BlockItem INFUSION_TABLE_ITEM;
    public static BlockItem INFUSION_PEDESTAL_ITEM;

    public static void register(RegisterEvent event)
    {
        if (!event.getRegistryKey().equals(net.minecraft.registry.RegistryKeys.ITEM)) return;
        INFUSION_TABLE_ITEM = new BlockItem(EIBlocks.INFUSION_TABLE, new Item.Settings());
        INFUSION_PEDESTAL_ITEM = new BlockItem(EIBlocks.INFUSION_PEDESTAL, new Item.Settings());
        event.register(net.minecraft.registry.RegistryKeys.ITEM, helper -> {
            helper.register(EnchantmentInfusion.id("enchantment_infusion_table"), INFUSION_TABLE_ITEM);
            helper.register(EnchantmentInfusion.id("enchantment_infusion_pedestal"), INFUSION_PEDESTAL_ITEM);
        });
    }

    public static void addCreativeEntries(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey().equals(ItemGroups.FUNCTIONAL))
        {
            event.getEntries().putAfter(Items.ENCHANTING_TABLE.getDefaultStack(), INFUSION_TABLE_ITEM.getDefaultStack(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
            event.getEntries().putAfter(INFUSION_TABLE_ITEM.getDefaultStack(), INFUSION_PEDESTAL_ITEM.getDefaultStack(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
