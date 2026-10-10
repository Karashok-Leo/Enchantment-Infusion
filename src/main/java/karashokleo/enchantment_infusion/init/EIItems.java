package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

public class EIItems
{
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(RegistryKeys.ITEM, EnchantmentInfusion.MOD_ID);

    public static final RegistryObject<BlockItem> INFUSION_TABLE_ITEM = ITEMS.register("enchantment_infusion_table",
        () -> new BlockItem(EIBlocks.INFUSION_TABLE.get(), new Item.Settings()));
    public static final RegistryObject<BlockItem> INFUSION_PEDESTAL_ITEM = ITEMS.register("enchantment_infusion_pedestal",
        () -> new BlockItem(EIBlocks.INFUSION_PEDESTAL.get(), new Item.Settings()));

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }

    public static void addCreativeEntries(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey().equals(ItemGroups.FUNCTIONAL))
        {
            event.getEntries().putAfter(Items.ENCHANTING_TABLE.getDefaultStack(), INFUSION_TABLE_ITEM.get().getDefaultStack(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
            event.getEntries().putAfter(INFUSION_TABLE_ITEM.get().getDefaultStack(), INFUSION_PEDESTAL_ITEM.get().getDefaultStack(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
