package karashokleo.enchantment_infusion.content.compat.jei;

import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipe;
import net.minecraft.recipe.RecipeEntry;

import com.mojang.serialization.Lifecycle;
import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

class JEIInfusionRecipeTest
{
    private static RegistryEntry<Enchantment> target;
    private static DynamicRegistryManager registryManager;

    @BeforeAll
    static void bootstrap()
    {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
        SimpleRegistry<Enchantment> enchantments = new SimpleRegistry<>(RegistryKeys.ENCHANTMENT, Lifecycle.stable());
        target = Registry.registerReference(enchantments, Identifier.of("test", "target"), enchantment(RegistryEntryList.of()));
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

    @Test
    void preservesCustomEnchantmentLevelsAndOutputComponents()
    {
        var ingredient = new EnchantmentIngredient(target, 3);
        var recipe = new EnchantmentInfusionRecipe(ingredient,
            DefaultedList.copyOf(Ingredient.EMPTY, Ingredient.ofItems(Items.DIAMOND)), target, 5, false);
        var id = Identifier.of("test", "upgrade");
        var display = new JEIInfusionRecipe(new RecipeEntry<>(id, recipe), registryManager);
        assertEquals(id, display.id());
        assertEquals(List.of(3, 4, 5), display.table().stream()
            .map(stack -> EnchantmentHelper.getEnchantments(stack).getLevel(target)).toList());
        assertEquals(5, EnchantmentHelper.getEnchantments(display.output()).getLevel(target));
        assertTrue(display.output().contains(DataComponentTypes.STORED_ENCHANTMENTS));
        assertEquals(1, display.pedestals().size());
    }

    @Test
    void preservesOrdinaryCenterAndRepeatedPedestalsForOneThroughEightSlots()
    {
        for (int count = 1; count <= 8; count++)
        {
            var pedestals = DefaultedList.<Ingredient>of();
            for (int i = 0; i < count; i++) pedestals.add(Ingredient.ofItems(Items.DIAMOND));
            var output = new ItemStack(Items.GOLD_INGOT, 3);
            output.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Component output"));
            var recipe = new SimpleInfusionRecipe(Ingredient.ofItems(Items.BOOK), pedestals, output, true);
            var display = new JEIInfusionRecipe(new RecipeEntry<>(Identifier.of("test", "simple"), recipe), registryManager);
            assertEquals(count, display.pedestals().size());
            assertEquals(1, display.table().size());
            assertTrue(display.table().getFirst().isOf(Items.BOOK));
            assertEquals(3, display.output().getCount());
            assertEquals(Text.literal("Component output"), display.output().get(DataComponentTypes.CUSTOM_NAME));
            assertNotSame(output, display.output());
            display.output().setCount(1);
            assertEquals(3, output.getCount());
        }
    }
    @Test
    void enumeratesCustomPedestalsWithoutFlatteningComponents()
    {
        var pedestals = DefaultedList.copyOf(Ingredient.EMPTY, new EnchantmentIngredient(target, 4).toVanilla());
        var recipe = new EnchantmentInfusionRecipe(null, pedestals, target, 1, false);
        var display = new JEIInfusionRecipe(new RecipeEntry<>(Identifier.of("test", "base"), recipe), registryManager);
        assertTrue(display.table().getFirst().isOf(Items.BOOK));
        assertEquals(List.of(4, 5), display.pedestals().getFirst().stream()
            .map(stack -> EnchantmentHelper.getEnchantments(stack).getLevel(target)).toList());
        assertEquals(1, EnchantmentHelper.getEnchantments(display.output()).getLevel(target));
    }
}
