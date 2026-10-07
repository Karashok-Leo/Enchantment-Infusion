package karashokleo.enchantment_infusion.validation;

import karashokleo.enchantment_infusion.content.recipe.*;
import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.util.SerialUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Map;


/** Regression assertions run after Forge has applied its normal launch transformations. */
@Mod.EventBusSubscriber(modid = EnchantmentInfusion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class InfusionRecipeValidation
{
    @SubscribeEvent
    public static void validate(GatherDataEvent event) throws java.io.IOException
    {
        InfusionRecipeValidation tests = new InfusionRecipeValidation();
        tests.booksAndApplicableEquipmentMatch();
        tests.upgradesRequirePrerequisiteAndHigherLevel();
        tests.forceDoesNotBypassPrerequisiteOrUpgrade();
        tests.pedestalIngredientsAreUnorderedAndCounted();
        tests.customIngredientRecordAndForgeAdapterAgree();
        tests.enchantmentRecipeJsonAndNetworkRoundTrip();
        tests.invalidIngredientsFailRatherThanDisappear();
        tests.simpleInfusionCopiesNbtWithoutMutatingTemplate();
        tests.simpleInfusionCanDisableNbtCopy();
        tests.customIngredientJsonAndNetworkRoundTrip();
        java.nio.file.Path report = java.nio.file.Path.of(System.getProperty(
            "enchantment_infusion.validation.report", "../build/reports/forge-validation.json"));
        java.nio.file.Files.createDirectories(report.getParent());
        java.nio.file.Files.writeString(report, "{\"passed\":10,\"failed\":0,\"loader\":\"Forge\",\"minecraft\":\"1.20.1\"}\n");
        System.out.println("Enchantment Infusion: 10 Forge runtime regression checks passed.");
    }

    private static void assertTrue(boolean value)
    {
        if (!value) throw new AssertionError("Expected true");
    }

    private static void assertFalse(boolean value) { assertTrue(!value); }

    private static void assertEquals(Object expected, Object actual)
    {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError("Expected " + expected + ", got " + actual);
    }

    private static void assertThrows(Class<? extends Throwable> type, Runnable action)
    {
        try { action.run(); }
        catch (Throwable failure)
        {
            if (type.isInstance(failure)) return;
            throw new AssertionError("Unexpected exception", failure);
        }
        throw new AssertionError("Expected exception: " + type.getName());
    }

    private static DefaultedList<Ingredient> ingredients()
    {
        return DefaultedList.copyOf(Ingredient.EMPTY, Ingredient.ofItems(Items.DIAMOND), Ingredient.ofItems(Items.LAPIS_LAZULI));
    }

    private static EnchantmentInfusionRecipe recipe(EnchantmentIngredient input, int level, boolean force)
    {
        return new EnchantmentInfusionRecipe(new Identifier("test", "recipe"), input, ingredients(), Enchantments.EFFICIENCY, level, force);
    }

    void booksAndApplicableEquipmentMatch()
    {
        var recipe = recipe(null, 1, false);
        assertTrue(recipe.matchTableStack(Items.BOOK.getDefaultStack()));
        assertTrue(recipe.matchTableStack(Items.ENCHANTED_BOOK.getDefaultStack()));
        assertTrue(recipe.matchTableStack(Items.DIAMOND_PICKAXE.getDefaultStack()));
        assertFalse(recipe.matchTableStack(Items.STONE.getDefaultStack()));
        ItemStack result = recipe.infuse(Items.BOOK.getDefaultStack());
        assertTrue(result.isOf(Items.ENCHANTED_BOOK));
        assertEquals(1, EnchantmentHelper.get(result).get(Enchantments.EFFICIENCY));
    }

    void upgradesRequirePrerequisiteAndHigherLevel()
    {
        ItemStack stack = Items.DIAMOND_PICKAXE.getDefaultStack();
        var recipe = recipe(new EnchantmentIngredient(Enchantments.EFFICIENCY, 1), 2, false);
        assertFalse(recipe.matchTableStack(stack));
        EnchantmentHelper.set(Map.of(Enchantments.EFFICIENCY, 1), stack);
        assertTrue(recipe.matchTableStack(stack));
        ItemStack result = recipe.infuse(stack);
        assertEquals(2, EnchantmentHelper.get(result).get(Enchantments.EFFICIENCY));
        assertFalse(recipe.matchTableStack(result));
    }

    void forceDoesNotBypassPrerequisiteOrUpgrade()
    {
        assertTrue(recipe(null, 1, true).matchTableStack(Items.STONE.getDefaultStack()));
        assertFalse(recipe(new EnchantmentIngredient(Enchantments.EFFICIENCY, 1), 2, true).matchTableStack(Items.STONE.getDefaultStack()));
        ItemStack stack = Items.BOOK.getDefaultStack();
        EnchantmentHelper.set(Map.of(Enchantments.EFFICIENCY, 2), stack);
        assertFalse(recipe(null, 2, true).matchTableStack(stack));
    }

    void pedestalIngredientsAreUnorderedAndCounted()
    {
        var recipe = recipe(null, 1, false);
        assertTrue(recipe.matchPedestalStacks(List.of(Items.LAPIS_LAZULI.getDefaultStack(), Items.DIAMOND.getDefaultStack(), ItemStack.EMPTY)));
        assertFalse(recipe.matchPedestalStacks(List.of(Items.DIAMOND.getDefaultStack())));
        assertFalse(recipe.matchPedestalStacks(List.of(Items.DIAMOND.getDefaultStack(), Items.DIAMOND.getDefaultStack())));
    }

    void customIngredientRecordAndForgeAdapterAgree()
    {
        var ingredient = new EnchantmentIngredient(Enchantments.EFFICIENCY, 1);
        ItemStack stack = Items.DIAMOND_PICKAXE.getDefaultStack();
        assertFalse(ingredient.toVanilla().test(stack));
        EnchantmentHelper.set(Map.of(Enchantments.EFFICIENCY, 1), stack);
        assertTrue(ingredient.toVanilla().test(stack));
        assertFalse(ingredient.toVanilla().isSimple());
        assertFalse(ingredient.toVanilla().isEmpty());
        assertEquals(5, ingredient.toVanilla().getMatchingStacks().length);
        assertEquals(ingredient, new EnchantmentIngredient(Enchantments.EFFICIENCY, 1));
    }

    void enchantmentRecipeJsonAndNetworkRoundTrip()
    {
        var serializer = new EnchantmentInfusionRecipeSerializer();
        var original = serializer.read(new Identifier("test", "recipe"), JsonParser.parseString("{\"enchantment\":\"minecraft:efficiency\",\"level\":2,\"ingredients\":[{\"item\":\"minecraft:diamond\"}],\"input\":{\"enchantment\":\"minecraft:efficiency\",\"min_level\":1}}").getAsJsonObject());
        assertFalse(original.force());
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        serializer.write(buf, original);
        var result = serializer.read(original.getId(), buf);
        assertEquals(original.enchantment(), result.enchantment());
        assertEquals(original.input(), result.input());
        assertEquals(original.level(), result.level());
        assertTrue(result.ingredients().get(0).test(Items.DIAMOND.getDefaultStack()));
        buf.release();
    }

    void invalidIngredientsFailRatherThanDisappear()
    {
        assertThrows(RuntimeException.class, () -> SerialUtil.ingredientsFromJsonArray(JsonParser.parseString("[]").getAsJsonArray()));
        String nine = "[" + String.join(",", java.util.Collections.nCopies(9, "{\"item\":\"minecraft:diamond\"}")) + "]";
        assertThrows(RuntimeException.class, () -> SerialUtil.ingredientsFromJsonArray(JsonParser.parseString(nine).getAsJsonArray()));
    }
    void simpleInfusionCopiesNbtWithoutMutatingTemplate()
    {
        ItemStack input = Items.DIAMOND_PICKAXE.getDefaultStack();
        input.getOrCreateNbt().putString("marker", "retained");
        ItemStack output = Items.NETHERITE_PICKAXE.getDefaultStack();
        output.getOrCreateNbt().putString("template", "kept");
        var recipe = new SimpleInfusionRecipe(new Identifier("test", "simple"), Ingredient.ofItems(Items.DIAMOND_PICKAXE), ingredients(), output, true);
        ItemStack result = recipe.infuse(input);
        assertTrue(result.isOf(Items.NETHERITE_PICKAXE));
        assertEquals("retained", result.getNbt().getString("marker"));
        assertEquals("kept", result.getNbt().getString("template"));
        assertFalse(output.getNbt().contains("marker"));
    }

    void simpleInfusionCanDisableNbtCopy()
    {
        ItemStack input = Items.DIAMOND_PICKAXE.getDefaultStack();
        input.getOrCreateNbt().putString("marker", "not copied");
        var recipe = new SimpleInfusionRecipe(new Identifier("test", "simple"), Ingredient.ofItems(Items.DIAMOND_PICKAXE), ingredients(), Items.NETHERITE_PICKAXE.getDefaultStack(), false);
        assertFalse(recipe.infuse(input).getOrCreateNbt().contains("marker"));
    }

    void customIngredientJsonAndNetworkRoundTrip()
    {
        var serializer = new EnchantmentIngredient.Serializer();
        var ingredient = new EnchantmentIngredient(Enchantments.EFFICIENCY, 2);
        assertEquals(ingredient, serializer.read(ingredient.toVanilla().toJson()));
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        serializer.write(buf, ingredient);
        assertEquals(ingredient, serializer.read(buf));
        buf.release();
    }

}
