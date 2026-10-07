package karashokleo.enchantment_infusion.api.block.entity;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameableSingleStackTileTest
{
    private static RegistryAccess registryManager;

    @BeforeAll
    static void bootstrap()
    {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        registryManager = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    }

    @Test
    void customNameSurvivesComponentsToBlockToDropRoundTrip()
    {
        TrackingTile placed = new TrackingTile();
        ItemStack namedBlock = Items.CHEST.getDefaultInstance();
        Component customName = Component.literal("Named infusion block");
        namedBlock.set(DataComponents.CUSTOM_NAME, customName);
        placed.applyComponentsFromItemStack(namedBlock);
        assertEquals(customName, placed.getCustomName());
        assertEquals(customName, placed.getName());

        DataComponentMap dropComponents = placed.collectComponents();
        assertEquals(customName, dropComponents.get(DataComponents.CUSTOM_NAME));
        ItemStack droppedBlock = Items.CHEST.getDefaultInstance();
        droppedBlock.applyComponents(dropComponents);
        TrackingTile replaced = new TrackingTile();
        replaced.applyComponentsFromItemStack(droppedBlock);
        assertEquals(customName, replaced.getCustomName());

        placed.applyComponentsFromItemStack(Items.CHEST.getDefaultInstance());
        assertNull(placed.getCustomName());
        assertNull(placed.collectComponents().get(DataComponents.CUSTOM_NAME));
    }

    @Test
    void customNameAndStoredItemSurviveSaveAndChunkSync()
    {
        TrackingTile original = new TrackingTile();
        original.setCustomName(Component.literal("Saved name"));
        ItemStack stored = Items.DIAMOND_SWORD.getDefaultInstance();
        stored.setDamageValue(12);
        stored.set(DataComponents.CUSTOM_NAME, Component.literal("Stored item name"));
        original.setTheItem(stored);

        CompoundTag saved = original.saveWithoutMetadata(registryManager);
        TrackingTile restored = new TrackingTile();
        restored.loadWithComponents(saved, registryManager);
        assertEquals(original.getCustomName(), restored.getCustomName());
        assertTrue(ItemStack.matches(stored, restored.getTheItem()));

        TrackingTile clientCopy = new TrackingTile();
        clientCopy.loadWithComponents(original.getUpdateTag(registryManager), registryManager);
        assertEquals(original.getCustomName(), clientCopy.getCustomName());
        assertTrue(ItemStack.matches(stored, clientCopy.getTheItem()));

        original.removeComponentsFromTag(saved);
        assertFalse(saved.contains("CustomName"));
        assertTrue(saved.contains("Item"));
        assertEquals(original.getCustomName(), original.collectComponents().get(DataComponents.CUSTOM_NAME));
    }

    @Test
    void emptyStoredStackRoundTrips()
    {
        TrackingTile original = new TrackingTile();
        TrackingTile restored = new TrackingTile();
        restored.loadWithComponents(original.saveWithoutMetadata(registryManager), registryManager);
        assertTrue(restored.getTheItem().isEmpty());
        assertNull(restored.getCustomName());
        assertNull(restored.collectComponents().get(DataComponents.CUSTOM_NAME));
    }

    @Test
    void allSingleStackRemovalRoutesUpdateTheTile()
    {
        TrackingTile tile = new TrackingTile();
        tile.setTheItem(Items.DIAMOND.getDefaultInstance());
        int updates = tile.updates;
        assertTrue(tile.splitTheItem(1).is(Items.DIAMOND));
        assertTrue(tile.isEmpty());
        assertEquals(updates + 1, tile.updates);

        tile.setTheItem(Items.DIAMOND.getDefaultInstance());
        updates = tile.updates;
        assertTrue(tile.removeTheItem().is(Items.DIAMOND));
        assertTrue(tile.isEmpty());
        assertEquals(updates + 1, tile.updates);

        tile.setTheItem(Items.DIAMOND.getDefaultInstance());
        updates = tile.updates;
        tile.clearContent();
        assertTrue(tile.isEmpty());
        assertEquals(updates + 1, tile.updates);
    }

    @Test
    void recipeInputKeepsCentralStackSeparateAndAppliesAllRemainders()
    {
        TrackingTile table = new TrackingTile();
        table.setTheItem(Items.BOOK.getDefaultInstance());
        NonNullList<AbstractInfusionTile> pedestals = NonNullList.create();
        for (int i = 0; i < 8; i++) pedestals.add(new TrackingTile());
        pedestals.get(0).setTheItem(Items.WATER_BUCKET.getDefaultInstance());
        InfusionInventory inventory = new InfusionInventory(table, pedestals);
        assertEquals(8, inventory.size());
        assertTrue(inventory.getItem(0).is(Items.WATER_BUCKET));
        assertTrue(inventory.getTableStack().is(Items.BOOK));

        NonNullList<ItemStack> remainder = NonNullList.withSize(8, ItemStack.EMPTY);
        remainder.set(0, Items.BUCKET.getDefaultInstance());
        inventory.setRemainder(remainder);
        assertTrue(pedestals.get(0).getTheItem().is(Items.BUCKET));
        assertTrue(table.getTheItem().is(Items.BOOK));
        assertTrue(inventory.removeStack(0).is(Items.BUCKET));
        assertTrue(pedestals.get(0).isEmpty());
    }

    // A vanilla registered type/state keeps these world-free tests focused on the shared tile contract.
    private static class TrackingTile extends AbstractInfusionTile
    {
        int updates;

        TrackingTile()
        {
            super(BlockEntityType.CHEST, BlockPos.ZERO, Blocks.CHEST.defaultBlockState());
        }

        @Override
        public void update()
        {
            updates++;
            super.update();
        }
    }
}
