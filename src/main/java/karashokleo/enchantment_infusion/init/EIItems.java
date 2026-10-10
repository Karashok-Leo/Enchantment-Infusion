package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class EIItems
{
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnchantmentInfusion.MOD_ID);

    public static final DeferredItem<BlockItem> INFUSION_TABLE_ITEM = ITEMS.register("enchantment_infusion_table",
        () -> new BlockItem(EIBlocks.INFUSION_TABLE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> INFUSION_PEDESTAL_ITEM = ITEMS.register("enchantment_infusion_pedestal",
        () -> new BlockItem(EIBlocks.INFUSION_PEDESTAL.get(), new Item.Properties()));

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }

    public static void addCreativeItems(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS))
        {
            event.insertAfter(Items.ENCHANTING_TABLE.getDefaultInstance(), INFUSION_TABLE_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(INFUSION_TABLE_ITEM.get().getDefaultInstance(), INFUSION_PEDESTAL_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
