package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.fabric.EnchantmentInfusion;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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
                new BlockItem(EIBlocks.INFUSION_TABLE, new FabricItemSettings())
        );
        INFUSION_PEDESTAL_ITEM = Registry.register(
                Registries.ITEM,
                EnchantmentInfusion.id("enchantment_infusion_pedestal"),
                new BlockItem(EIBlocks.INFUSION_PEDESTAL, new FabricItemSettings())
        );
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> content.addAfter(Items.ENCHANTING_TABLE, INFUSION_TABLE_ITEM, INFUSION_PEDESTAL_ITEM));
    }
}
