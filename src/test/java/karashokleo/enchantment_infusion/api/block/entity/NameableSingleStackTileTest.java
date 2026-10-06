package karashokleo.enchantment_infusion.api.block.entity;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameableSingleStackTileTest
{
    private static DynamicRegistryManager registryManager;

    @BeforeAll
    static void bootstrap()
    {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
        registryManager = DynamicRegistryManager.of(Registries.REGISTRIES);
    }

    @Test
    void customNameSurvivesComponentsToBlockToDropRoundTrip()
    {
        TrackingTile placed = new TrackingTile();
        ItemStack namedBlock = Items.CHEST.getDefaultStack();
        Text customName = Text.literal("Named infusion block");
        namedBlock.set(DataComponentTypes.CUSTOM_NAME, customName);
        placed.readComponents(namedBlock);
        assertEquals(customName, placed.getCustomName());
        assertEquals(customName, placed.getName());

        ComponentMap dropComponents = placed.createComponentMap();
        assertEquals(customName, dropComponents.get(DataComponentTypes.CUSTOM_NAME));
        ItemStack droppedBlock = Items.CHEST.getDefaultStack();
        droppedBlock.applyComponentsFrom(dropComponents);
        TrackingTile replaced = new TrackingTile();
        replaced.readComponents(droppedBlock);
        assertEquals(customName, replaced.getCustomName());

        placed.readComponents(Items.CHEST.getDefaultStack());
        assertNull(placed.getCustomName());
        assertNull(placed.createComponentMap().get(DataComponentTypes.CUSTOM_NAME));
    }

    @Test
    void customNameAndStoredItemSurviveSaveAndChunkSync()
    {
        TrackingTile original = new TrackingTile();
        original.setCustomName(Text.literal("Saved name"));
        ItemStack stored = Items.DIAMOND_SWORD.getDefaultStack();
        stored.setDamage(12);
        stored.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Stored item name"));
        original.setStack(stored);

        NbtCompound saved = original.createNbt(registryManager);
        TrackingTile restored = new TrackingTile();
        restored.read(saved, registryManager);
        assertEquals(original.getCustomName(), restored.getCustomName());
        assertTrue(ItemStack.areEqual(stored, restored.getStack()));

        TrackingTile clientCopy = new TrackingTile();
        clientCopy.read(original.toInitialChunkDataNbt(registryManager), registryManager);
        assertEquals(original.getCustomName(), clientCopy.getCustomName());
        assertTrue(ItemStack.areEqual(stored, clientCopy.getStack()));

        original.removeFromCopiedStackNbt(saved);
        assertFalse(saved.contains("CustomName"));
        assertTrue(saved.contains("Item"));
        assertEquals(original.getCustomName(), original.createComponentMap().get(DataComponentTypes.CUSTOM_NAME));
    }

    @Test
    void emptyStoredStackRoundTrips()
    {
        TrackingTile original = new TrackingTile();
        TrackingTile restored = new TrackingTile();
        restored.read(original.createNbt(registryManager), registryManager);
        assertTrue(restored.getStack().isEmpty());
        assertNull(restored.getCustomName());
        assertNull(restored.createComponentMap().get(DataComponentTypes.CUSTOM_NAME));
    }

    @Test
    void allSingleStackRemovalRoutesUpdateTheTile()
    {
        TrackingTile tile = new TrackingTile();
        tile.setStack(Items.DIAMOND.getDefaultStack());
        int updates = tile.updates;
        assertTrue(tile.decreaseStack(1).isOf(Items.DIAMOND));
        assertTrue(tile.isEmpty());
        assertEquals(updates + 1, tile.updates);

        tile.setStack(Items.DIAMOND.getDefaultStack());
        updates = tile.updates;
        assertTrue(tile.emptyStack().isOf(Items.DIAMOND));
        assertTrue(tile.isEmpty());
        assertEquals(updates + 1, tile.updates);

        tile.setStack(Items.DIAMOND.getDefaultStack());
        updates = tile.updates;
        tile.clear();
        assertTrue(tile.isEmpty());
        assertEquals(updates + 1, tile.updates);
    }

    @Test
    void recipeInputKeepsCentralStackSeparateAndAppliesAllRemainders()
    {
        TrackingTile table = new TrackingTile();
        table.setStack(Items.BOOK.getDefaultStack());
        DefaultedList<AbstractInfusionTile> pedestals = DefaultedList.of();
        for (int i = 0; i < 8; i++) pedestals.add(new TrackingTile());
        pedestals.get(0).setStack(Items.WATER_BUCKET.getDefaultStack());
        InfusionInventory inventory = new InfusionInventory(table, pedestals);
        assertEquals(8, inventory.getSize());
        assertTrue(inventory.getStackInSlot(0).isOf(Items.WATER_BUCKET));
        assertTrue(inventory.getTableStack().isOf(Items.BOOK));

        DefaultedList<ItemStack> remainder = DefaultedList.ofSize(8, ItemStack.EMPTY);
        remainder.set(0, Items.BUCKET.getDefaultStack());
        inventory.setRemainder(remainder);
        assertTrue(pedestals.get(0).getStack().isOf(Items.BUCKET));
        assertTrue(table.getStack().isOf(Items.BOOK));
        assertTrue(inventory.removeStack(0).isOf(Items.BUCKET));
        assertTrue(pedestals.get(0).isEmpty());
    }

    // A vanilla registered type/state keeps these world-free tests focused on the shared tile contract.
    private static class TrackingTile extends AbstractInfusionTile
    {
        int updates;

        TrackingTile()
        {
            super(BlockEntityType.CHEST, BlockPos.ORIGIN, Blocks.CHEST.getDefaultState());
        }

        @Override
        public void update()
        {
            updates++;
            super.update();
        }
    }
}
