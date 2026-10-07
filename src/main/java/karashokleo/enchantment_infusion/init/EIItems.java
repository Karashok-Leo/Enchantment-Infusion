package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class EIItems
{
    public static BlockItem INFUSION_TABLE_ITEM;
    public static BlockItem INFUSION_PEDESTAL_ITEM;

    public static void register(RegisterEvent event)
    {
        if (!event.getRegistryKey().equals(Registries.ITEM)) return;
        INFUSION_TABLE_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            EnchantmentInfusion.id("enchantment_infusion_table"),
            new BlockItem(EIBlocks.INFUSION_TABLE, new Item.Properties())
        );
        INFUSION_PEDESTAL_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            EnchantmentInfusion.id("enchantment_infusion_pedestal"),
            new BlockItem(EIBlocks.INFUSION_PEDESTAL, new Item.Properties())
        );
    }

    public static void addCreativeItems(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS))
        {
            event.insertAfter(Items.ENCHANTING_TABLE.getDefaultInstance(), INFUSION_TABLE_ITEM.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(INFUSION_TABLE_ITEM.getDefaultInstance(), INFUSION_PEDESTAL_ITEM.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
