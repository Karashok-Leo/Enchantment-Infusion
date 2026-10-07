package karashokleo.enchantment_infusion.content.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import io.netty.buffer.Unpooled;
import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InfusionRecipeTest
{
    private static Holder<Enchantment> target;
    private static Holder<Enchantment> prerequisite;
    private static Holder<Enchantment> conflicting;
    private static RegistryAccess registryManager;

    @BeforeAll
    static void bootstrap()
    {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        MappedRegistry<Enchantment> enchantments = new MappedRegistry<>(Registries.ENCHANTMENT, Lifecycle.stable());
        target = Registry.registerForHolder(enchantments, ResourceLocation.fromNamespaceAndPath("test", "target"), enchantment(HolderSet.direct()));
        prerequisite = Registry.registerForHolder(enchantments, ResourceLocation.fromNamespaceAndPath("test", "prerequisite"), enchantment(HolderSet.direct()));
        conflicting = Registry.registerForHolder(enchantments, ResourceLocation.fromNamespaceAndPath("test", "conflicting"), enchantment(HolderSet.direct(target)));
        enchantments.freeze();
        List<Registry<?>> registries = new ArrayList<>();
        BuiltInRegistries.REGISTRY.forEach(registries::add);
        registries.add(enchantments);
        registryManager = new RegistryAccess.ImmutableRegistryAccess(registries);
    }

    private static Enchantment enchantment(HolderSet<Enchantment> exclusiveSet)
    {
        return new Enchantment(Component.literal("Test enchantment"), Enchantment.definition(
            HolderSet.direct(Items.DIAMOND_SWORD.builtInRegistryHolder(), Items.DIAMOND_PICKAXE.builtInRegistryHolder()),
            1, 5, Enchantment.constantCost(1), Enchantment.constantCost(10), 1,
            new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
        ), exclusiveSet, DataComponentMap.EMPTY);
    }

    @Test
    void viewerPreviewsPreservePrerequisiteBookLevels()
    {
        ItemStack[] previews = new EnchantmentIngredient(prerequisite, 3).toVanilla().getItems();
        assertEquals(3, previews.length);
        for (int i = 0; i < previews.length; i++)
        {
            assertTrue(previews[i].is(Items.ENCHANTED_BOOK));
            assertEquals(i + 3, EnchantmentHelper.getEnchantmentsForCrafting(previews[i]).getLevel(prerequisite));
        }
        ItemStack[] plain = new EnchantmentIngredient(prerequisite, 0).toVanilla().getItems();
        assertEquals(1, plain.length);
        assertTrue(plain[0].is(Items.BOOK));
    }

    @Test
    void viewerOutputIsNativeEnchantedBookWithExactLevel()
    {
        ItemStack output = enchantmentRecipe(null, 4, false).getResultItem(registryManager);
        assertTrue(output.is(Items.ENCHANTED_BOOK));
        assertEquals(4, EnchantmentHelper.getEnchantmentsForCrafting(output).getLevel(target));
    }

    private static NonNullList<Ingredient> ingredients()
    {
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.DIAMOND), Ingredient.of(Items.LAPIS_LAZULI));
    }

    private static EnchantmentInfusionRecipe enchantmentRecipe(EnchantmentIngredient input, int level, boolean force)
    {
        return new EnchantmentInfusionRecipe(input, ingredients(), target, level, force);
    }

    private static void enchant(ItemStack stack, Holder<Enchantment> enchantment, int level)
    {
        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(stack));
        builder.set(enchantment, level);
        EnchantmentHelper.setEnchantments(stack, builder.toImmutable());
    }

    @Test
    void booksAndApplicableEquipmentMatch()
    {
        var recipe = enchantmentRecipe(null, 1, false);
        assertTrue(recipe.matchTableStack(Items.BOOK.getDefaultInstance()));
        assertTrue(recipe.matchTableStack(Items.ENCHANTED_BOOK.getDefaultInstance()));
        assertTrue(recipe.matchTableStack(Items.DIAMOND_SWORD.getDefaultInstance()));
        assertFalse(recipe.matchTableStack(Items.STONE.getDefaultInstance()));
        assertTrue(recipe.infuse(Items.BOOK.getDefaultInstance()).is(Items.ENCHANTED_BOOK));
        assertEquals(1, EnchantmentHelper.getEnchantmentsForCrafting(recipe.infuse(Items.BOOK.getDefaultInstance())).getLevel(target));
    }

    @Test
    void forceOnlyBypassesApplicabilityAndConflicts()
    {
        var normal = enchantmentRecipe(null, 2, false);
        var forced = enchantmentRecipe(null, 2, true);
        ItemStack sword = Items.DIAMOND_SWORD.getDefaultInstance();
        enchant(sword, conflicting, 1);
        assertFalse(normal.matchTableStack(sword));
        assertTrue(forced.matchTableStack(sword));
        assertTrue(forced.matchTableStack(Items.STONE.getDefaultInstance()));

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
        ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
        enchant(book, prerequisite, 2);
        enchant(book, conflicting, 1);
        var recipe = enchantmentRecipe(new EnchantmentIngredient(prerequisite, 2), 3, true);
        assertTrue(recipe.matchTableStack(book));
        ItemStack result = recipe.infuse(book.copy());
        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(result);
        assertEquals(0, enchantments.getLevel(prerequisite));
        assertEquals(3, enchantments.getLevel(target));
        assertEquals(1, enchantments.getLevel(conflicting));
        assertEquals(2, EnchantmentHelper.getEnchantmentsForCrafting(book).getLevel(prerequisite));
    }

    @Test
    void sameEnchantmentUpgradeChecksTheOriginalLevel()
    {
        ItemStack sword = Items.DIAMOND_SWORD.getDefaultInstance();
        var recipe = enchantmentRecipe(new EnchantmentIngredient(target, 1), 2, false);
        assertFalse(recipe.matchTableStack(sword));
        enchant(sword, target, 1);
        assertTrue(recipe.matchTableStack(sword));
        assertEquals(2, EnchantmentHelper.getEnchantmentsForCrafting(recipe.infuse(sword.copy())).getLevel(target));
        enchant(sword, target, 2);
        assertFalse(recipe.matchTableStack(sword));
    }

    @Test
    void copyNbtMergesStackChangesAndNestedCustomData()
    {
        ItemStack input = Items.DIAMOND_SWORD.getDefaultInstance();
        input.setDamageValue(17);
        input.set(DataComponents.CUSTOM_NAME, Component.literal("Input name"));
        enchant(input, target, 2);
        CompoundTag inputNested = new CompoundTag();
        inputNested.putInt("input", 1);
        inputNested.putInt("shared", 2);
        CompoundTag inputData = new CompoundTag();
        inputData.put("nested", inputNested);
        input.set(DataComponents.CUSTOM_DATA, CustomData.of(inputData));

        ItemStack output = Items.NETHERITE_SWORD.getDefaultInstance();
        output.set(DataComponents.CUSTOM_NAME, Component.literal("Output name"));
        CompoundTag outputNested = new CompoundTag();
        outputNested.putInt("output", 3);
        outputNested.putInt("shared", 4);
        CompoundTag outputData = new CompoundTag();
        outputData.put("nested", outputNested);
        output.set(DataComponents.CUSTOM_DATA, CustomData.of(outputData));

        var recipe = new SimpleInfusionRecipe(Ingredient.of(Items.DIAMOND_SWORD), ingredients(), output, true);
        ItemStack result = recipe.infuse(input);
        assertTrue(result.is(Items.NETHERITE_SWORD));
        assertEquals(output.getMaxDamage(), result.getMaxDamage());
        assertEquals(17, result.getDamageValue());
        assertEquals("Input name", result.getHoverName().getString());
        assertEquals(2, EnchantmentHelper.getEnchantmentsForCrafting(result).getLevel(target));
        CompoundTag merged = result.get(DataComponents.CUSTOM_DATA).copyTag().getCompound("nested");
        assertEquals(1, merged.getInt("input"));
        assertEquals(3, merged.getInt("output"));
        assertEquals(2, merged.getInt("shared"));
        assertEquals("Output name", output.getHoverName().getString());
        assertEquals(4, output.get(DataComponents.CUSTOM_DATA).copyTag().getCompound("nested").getInt("shared"));
    }

    @Test
    void copyNbtFalseKeepsOutputAndDoesNotShareTheTemplate()
    {
        ItemStack input = Items.DIAMOND_SWORD.getDefaultInstance();
        input.set(DataComponents.CUSTOM_NAME, Component.literal("Input name"));
        input.setDamageValue(17);
        ItemStack output = new ItemStack(Items.DIAMOND, 2);
        output.set(DataComponents.CUSTOM_NAME, Component.literal("Output name"));
        var recipe = new SimpleInfusionRecipe(Ingredient.of(Items.DIAMOND_SWORD), ingredients(), output, false);
        ItemStack result = recipe.infuse(input);
        assertTrue(ItemStack.matches(output, result));
        assertNotSame(output, result);
        result.setCount(1);
        assertEquals(2, output.getCount());
    }

    @Test
    void pedestalIngredientsRemainShapelessAndCounted()
    {
        var recipe = enchantmentRecipe(null, 1, false);
        assertTrue(recipe.matchPedestalStacks(List.of(Items.LAPIS_LAZULI.getDefaultInstance(), ItemStack.EMPTY, Items.DIAMOND.getDefaultInstance())));
        assertFalse(recipe.matchPedestalStacks(List.of(Items.DIAMOND.getDefaultInstance())));
        assertFalse(recipe.matchPedestalStacks(List.of(Items.DIAMOND.getDefaultInstance(), Items.DIAMOND.getDefaultInstance())));
        assertFalse(recipe.matchPedestalStacks(List.of(Items.DIAMOND.getDefaultInstance(), Items.LAPIS_LAZULI.getDefaultInstance(), Items.STONE.getDefaultInstance())));
    }

    @Test
    void enchantmentJsonAndPacketRoundTrip()
    {
        var recipe = enchantmentRecipe(new EnchantmentIngredient(target, 1), 2, true);
        var ops = registryManager.createSerializationContext(JsonOps.INSTANCE);
        JsonObject json = EnchantmentInfusionRecipeSerializer.CODEC.codec().encodeStart(ops, recipe).getOrThrow().getAsJsonObject();
        var decoded = EnchantmentInfusionRecipeSerializer.CODEC.codec().parse(ops, json).getOrThrow();
        assertEquals(target, decoded.enchantment());
        assertEquals(recipe.input(), decoded.input());
        assertEquals(2, decoded.level());
        assertTrue(decoded.force());
        assertEquals(2, decoded.ingredients().size());

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryManager);
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
        output.set(DataComponents.CUSTOM_NAME, Component.literal("Output name"));
        var recipe = new SimpleInfusionRecipe(Ingredient.of(Items.STONE), ingredients(), output, false);
        var ops = registryManager.createSerializationContext(JsonOps.INSTANCE);
        JsonObject json = SimpleInfusionRecipeSerializer.CODEC.codec().encodeStart(ops, recipe).getOrThrow().getAsJsonObject();
        var decoded = SimpleInfusionRecipeSerializer.CODEC.codec().parse(ops, json).getOrThrow();
        assertTrue(ItemStack.matches(output, decoded.output()));
        assertFalse(decoded.copyNbt());
        assertEquals(2, decoded.ingredients().size());
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryManager);
        try
        {
            SimpleInfusionRecipeSerializer.PACKET_CODEC.encode(buf, recipe);
            var synced = SimpleInfusionRecipeSerializer.PACKET_CODEC.decode(buf);
            assertTrue(ItemStack.matches(output, synced.output()));
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
        var ops = registryManager.createSerializationContext(JsonOps.INSTANCE);
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
