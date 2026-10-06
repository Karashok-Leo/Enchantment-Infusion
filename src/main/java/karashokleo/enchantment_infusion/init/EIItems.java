package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.fabric.EnchantmentInfusion;
import net.minecraft.item.Item;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EIItems
{
    public static BlockItem INFUSION_TABLE_ITEM;
    public static BlockItem INFUSION_PEDESTAL_ITEM;

    public static void register()
    {
        INFUSION_TABLE_ITEM = Registry.register(
            Registries.ITEM,
            EnchantmentInfusion.id("enchantment_infusion_table"),
            new BlockItem(EIBlocks.INFUSION_TABLE, new Item.Settings())
        );
        INFUSION_PEDESTAL_ITEM = Registry.register(
            Registries.ITEM,
            EnchantmentInfusion.id("enchantment_infusion_pedestal"),
            new BlockItem(EIBlocks.INFUSION_PEDESTAL, new Item.Settings())
        );
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> content.addAfter(Items.ENCHANTING_TABLE, INFUSION_TABLE_ITEM, INFUSION_PEDESTAL_ITEM));
    }
}
