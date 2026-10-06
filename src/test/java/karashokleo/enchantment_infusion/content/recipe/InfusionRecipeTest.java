package karashokleo.enchantment_infusion.content.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import io.netty.buffer.Unpooled;
import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InfusionRecipeTest
{
    private static RegistryEntry<Enchantment> target;
    private static RegistryEntry<Enchantment> prerequisite;
    private static RegistryEntry<Enchantment> conflicting;
    private static DynamicRegistryManager registryManager;

    @BeforeAll
    static void bootstrap()
    {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
        SimpleRegistry<Enchantment> enchantments = new SimpleRegistry<>(RegistryKeys.ENCHANTMENT, Lifecycle.stable());
        target = Registry.registerReference(enchantments, Identifier.of("test", "target"), enchantment(RegistryEntryList.of()));
        prerequisite = Registry.registerReference(enchantments, Identifier.of("test", "prerequisite"), enchantment(RegistryEntryList.of()));
        conflicting = Registry.registerReference(enchantments, Identifier.of("test", "conflicting"), enchantment(RegistryEntryList.of(target)));
        enchantments.freeze();
        List<Registry<?>> registries = new ArrayList<>();
        Registries.REGISTRIES.forEach(registries::add);
        registries.add(enchantments);
        registryManager = new DynamicRegistryManager.ImmutableImpl(registries);
    }

    private static Enchantment enchantment(RegistryEntryList<Enchantment> exclusiveSet)
    {
        return new Enchantment(Text.literal("Test enchantment"), Enchantment.definition(
            RegistryEntryList.of(Items.DIAMOND_SWORD.getRegistryEntry(), Items.DIAMOND_PICKAXE.getRegistryEntry()),
            1, 5, Enchantment.constantCost(1), Enchantment.constantCost(10), 1,
            new AttributeModifierSlot[]{AttributeModifierSlot.MAINHAND}
        ), exclusiveSet, ComponentMap.EMPTY);
    }

    private static DefaultedList<Ingredient> ingredients()
    {
        return DefaultedList.copyOf(Ingredient.EMPTY, Ingredient.ofItems(Items.DIAMOND), Ingredient.ofItems(Items.LAPIS_LAZULI));
    }

    private static EnchantmentInfusionRecipe enchantmentRecipe(EnchantmentIngredient input, int level, boolean force)
    {
        return new EnchantmentInfusionRecipe(input, ingredients(), target, level, force);
    }

    private static void enchant(ItemStack stack, RegistryEntry<Enchantment> enchantment, int level)
    {
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(stack));
        builder.set(enchantment, level);
        EnchantmentHelper.set(stack, builder.build());
    }

    @Test
    void booksAndApplicableEquipmentMatch()
    {
        var recipe = enchantmentRecipe(null, 1, false);
        assertTrue(recipe.matchTableStack(Items.BOOK.getDefaultStack()));
        assertTrue(recipe.matchTableStack(Items.ENCHANTED_BOOK.getDefaultStack()));
        assertTrue(recipe.matchTableStack(Items.DIAMOND_SWORD.getDefaultStack()));
        assertFalse(recipe.matchTableStack(Items.STONE.getDefaultStack()));
        assertTrue(recipe.infuse(Items.BOOK.getDefaultStack()).isOf(Items.ENCHANTED_BOOK));
        assertEquals(1, EnchantmentHelper.getEnchantments(recipe.infuse(Items.BOOK.getDefaultStack())).getLevel(target));
    }

    @Test
    void forceOnlyBypassesApplicabilityAndConflicts()
    {
        var normal = enchantmentRecipe(null, 2, false);
        var forced = enchantmentRecipe(null, 2, true);
        ItemStack sword = Items.DIAMOND_SWORD.getDefaultStack();
        enchant(sword, conflicting, 1);
        assertFalse(normal.matchTableStack(sword));
        assertTrue(forced.matchTableStack(sword));
        assertTrue(forced.matchTableStack(Items.STONE.getDefaultStack()));

        var requiresInput = enchantmentRecipe(new EnchantmentIngredient(prerequisite, 2), 2, true);
        assertFalse(requiresInput.matchTableStack(sword));
        enchant(sword, prerequisite, 1);
        assertFalse(requiresInput.matchTableStack(sword));
        enchant(sword, prerequisite, 2);
        assertTrue(requiresInput.matchTableStack(sword));
        enchant(sword, target, 2);
        assertFalse(requiresInput.matchTableStack(sword));
        enchant(sword, target, 3);
        assertFalse(requiresInput.matchTableStack(sword));
    }

    @Test
    void upgradeConsumesPrerequisiteAndPreservesOtherEnchantments()
    {
        ItemStack book = Items.ENCHANTED_BOOK.getDefaultStack();
        enchant(book, prerequisite, 2);
        enchant(book, conflicting, 1);
        var recipe = enchantmentRecipe(new EnchantmentIngredient(prerequisite, 2), 3, true);
        assertTrue(recipe.matchTableStack(book));
        ItemStack result = recipe.infuse(book.copy());
        var enchantments = EnchantmentHelper.getEnchantments(result);
        assertEquals(0, enchantments.getLevel(prerequisite));
        assertEquals(3, enchantments.getLevel(target));
        assertEquals(1, enchantments.getLevel(conflicting));
        assertEquals(2, EnchantmentHelper.getEnchantments(book).getLevel(prerequisite));
    }

    @Test
    void sameEnchantmentUpgradeChecksTheOriginalLevel()
    {
        ItemStack sword = Items.DIAMOND_SWORD.getDefaultStack();
        var recipe = enchantmentRecipe(new EnchantmentIngredient(target, 1), 2, false);
        assertFalse(recipe.matchTableStack(sword));
        enchant(sword, target, 1);
        assertTrue(recipe.matchTableStack(sword));
        assertEquals(2, EnchantmentHelper.getEnchantments(recipe.infuse(sword.copy())).getLevel(target));
        enchant(sword, target, 2);
        assertFalse(recipe.matchTableStack(sword));
    }

    @Test
    void copyNbtMergesStackChangesAndNestedCustomData()
    {
        ItemStack input = Items.DIAMOND_SWORD.getDefaultStack();
        input.setDamage(17);
        input.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Input name"));
        enchant(input, target, 2);
        NbtCompound inputNested = new NbtCompound();
        inputNested.putInt("input", 1);
        inputNested.putInt("shared", 2);
        NbtCompound inputData = new NbtCompound();
        inputData.put("nested", inputNested);
        input.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(inputData));

        ItemStack output = Items.NETHERITE_SWORD.getDefaultStack();
        output.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Output name"));
        NbtCompound outputNested = new NbtCompound();
        outputNested.putInt("output", 3);
        outputNested.putInt("shared", 4);
        NbtCompound outputData = new NbtCompound();
        outputData.put("nested", outputNested);
        output.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(outputData));

        var recipe = new SimpleInfusionRecipe(Ingredient.ofItems(Items.DIAMOND_SWORD), ingredients(), output, true);
        ItemStack result = recipe.infuse(input);
        assertTrue(result.isOf(Items.NETHERITE_SWORD));
        assertEquals(output.getMaxDamage(), result.getMaxDamage());
        assertEquals(17, result.getDamage());
        assertEquals("Input name", result.getName().getString());
        assertEquals(2, EnchantmentHelper.getEnchantments(result).getLevel(target));
        NbtCompound merged = result.get(DataComponentTypes.CUSTOM_DATA).copyNbt().getCompound("nested");
        assertEquals(1, merged.getInt("input"));
        assertEquals(3, merged.getInt("output"));
        assertEquals(2, merged.getInt("shared"));
        assertEquals("Output name", output.getName().getString());
        assertEquals(4, output.get(DataComponentTypes.CUSTOM_DATA).copyNbt().getCompound("nested").getInt("shared"));
    }

    @Test
    void copyNbtFalseKeepsOutputAndDoesNotShareTheTemplate()
    {
        ItemStack input = Items.DIAMOND_SWORD.getDefaultStack();
        input.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Input name"));
        input.setDamage(17);
        ItemStack output = new ItemStack(Items.DIAMOND, 2);
        output.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Output name"));
        var recipe = new SimpleInfusionRecipe(Ingredient.ofItems(Items.DIAMOND_SWORD), ingredients(), output, false);
        ItemStack result = recipe.infuse(input);
        assertTrue(ItemStack.areEqual(output, result));
        assertNotSame(output, result);
        result.setCount(1);
        assertEquals(2, output.getCount());
    }

    @Test
    void pedestalIngredientsRemainShapelessAndCounted()
    {
        var recipe = enchantmentRecipe(null, 1, false);
        assertTrue(recipe.matchPedestalStacks(List.of(Items.LAPIS_LAZULI.getDefaultStack(), ItemStack.EMPTY, Items.DIAMOND.getDefaultStack())));
        assertFalse(recipe.matchPedestalStacks(List.of(Items.DIAMOND.getDefaultStack())));
        assertFalse(recipe.matchPedestalStacks(List.of(Items.DIAMOND.getDefaultStack(), Items.DIAMOND.getDefaultStack())));
        assertFalse(recipe.matchPedestalStacks(List.of(Items.DIAMOND.getDefaultStack(), Items.LAPIS_LAZULI.getDefaultStack(), Items.STONE.getDefaultStack())));
    }

    @Test
    void enchantmentJsonAndPacketRoundTrip()
    {
        var recipe = enchantmentRecipe(new EnchantmentIngredient(target, 1), 2, true);
        var ops = registryManager.getOps(JsonOps.INSTANCE);
        JsonObject json = EnchantmentInfusionRecipeSerializer.CODEC.codec().encodeStart(ops, recipe).getOrThrow().getAsJsonObject();
        var decoded = EnchantmentInfusionRecipeSerializer.CODEC.codec().parse(ops, json).getOrThrow();
        assertEquals(target, decoded.enchantment());
        assertEquals(recipe.input(), decoded.input());
        assertEquals(2, decoded.level());
        assertTrue(decoded.force());
        assertEquals(2, decoded.ingredients().size());

        RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), registryManager);
        try
        {
            EnchantmentInfusionRecipeSerializer.PACKET_CODEC.encode(buf, recipe);
            var synced = EnchantmentInfusionRecipeSerializer.PACKET_CODEC.decode(buf);
            assertEquals(recipe.input(), synced.input());
            assertEquals(target, synced.enchantment());
            assertEquals(2, synced.level());
            assertTrue(synced.force());
            assertEquals(2, synced.ingredients().size());
            assertEquals(0, buf.readableBytes());
        } finally
        {
            buf.release();
        }
        json.remove("force");
        json.remove("input");
        var defaults = EnchantmentInfusionRecipeSerializer.CODEC.codec().parse(ops, json).getOrThrow();
        assertFalse(defaults.force());
        assertNull(defaults.input());
    }

    @Test
    void simpleJsonAndPacketRoundTripPreserveComponentsAndDefaults()
    {
        ItemStack output = new ItemStack(Items.DIAMOND, 2);
        output.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Output name"));
        var recipe = new SimpleInfusionRecipe(Ingredient.ofItems(Items.STONE), ingredients(), output, false);
        var ops = registryManager.getOps(JsonOps.INSTANCE);
        JsonObject json = SimpleInfusionRecipeSerializer.CODEC.codec().encodeStart(ops, recipe).getOrThrow().getAsJsonObject();
        var decoded = SimpleInfusionRecipeSerializer.CODEC.codec().parse(ops, json).getOrThrow();
        assertTrue(ItemStack.areEqual(output, decoded.output()));
        assertFalse(decoded.copyNbt());
        assertEquals(2, decoded.ingredients().size());
        RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), registryManager);
        try
        {
            SimpleInfusionRecipeSerializer.PACKET_CODEC.encode(buf, recipe);
            var synced = SimpleInfusionRecipeSerializer.PACKET_CODEC.decode(buf);
            assertTrue(ItemStack.areEqual(output, synced.output()));
            assertFalse(synced.copyNbt());
            assertEquals(0, buf.readableBytes());
        } finally
        {
            buf.release();
        }
        json.remove("copy_nbt");
        assertTrue(SimpleInfusionRecipeSerializer.CODEC.codec().parse(ops, json).getOrThrow().copyNbt());
    }

    @Test
    void jsonRejectsMissingAndExcessIngredientsAndInvalidLevels()
    {
        var ops = registryManager.getOps(JsonOps.INSTANCE);
        JsonObject json = EnchantmentInfusionRecipeSerializer.CODEC.codec()
            .encodeStart(ops, enchantmentRecipe(null, 1, false)).getOrThrow().getAsJsonObject();
        JsonArray original = json.getAsJsonArray("ingredients");
        json.add("ingredients", new JsonArray());
        assertTrue(EnchantmentInfusionRecipeSerializer.CODEC.codec().parse(ops, json).error().isPresent());
        JsonArray excessive = new JsonArray();
        for (int i = 0; i < 9; i++) excessive.add(original.get(0));
        json.add("ingredients", excessive);
        assertTrue(EnchantmentInfusionRecipeSerializer.CODEC.codec().parse(ops, json).error().isPresent());
        json.add("ingredients", original);
        json.addProperty("level", 256);
        assertTrue(EnchantmentInfusionRecipeSerializer.CODEC.codec().parse(ops, json).error().isPresent());
        json.addProperty("level", 0);
        assertTrue(EnchantmentInfusionRecipeSerializer.CODEC.codec().parse(ops, json).error().isPresent());
    }
}
