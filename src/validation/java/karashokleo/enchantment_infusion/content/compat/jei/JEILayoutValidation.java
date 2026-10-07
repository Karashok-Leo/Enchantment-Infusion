package karashokleo.enchantment_infusion.content.compat.jei;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/** Exercises the actual JEI layout code after Forge initializes Minecraft. Never shipped. */
public final class JEILayoutValidation
{
    private record Slot(RecipeIngredientRole role, int x, int y, List<ItemStack> stacks) { }

    public static void validate()
    {
        checkRecipe(null, 1);
        checkRecipe(new EnchantmentIngredient(Enchantments.EFFICIENCY, 4), 5);
        System.out.println("Enchantment Infusion: JEI level-one and upgrade slot-data checks passed.");
    }

    private static void checkRecipe(EnchantmentIngredient input, int level)
    {
        var ingredients = DefaultedList.copyOf(Ingredient.EMPTY,
            Ingredient.ofItems(Items.DIAMOND), Ingredient.ofItems(Items.LAPIS_LAZULI));
        var recipe = new EnchantmentInfusionRecipe(new Identifier("test", "jei"), input,
            ingredients, Enchantments.EFFICIENCY, level, false);
        ItemStack output = recipe.getOutput(null);
        List<Slot> slots = new ArrayList<>();
        IRecipeLayoutBuilder builder = (IRecipeLayoutBuilder) Proxy.newProxyInstance(
            JEILayoutValidation.class.getClassLoader(), new Class<?>[]{IRecipeLayoutBuilder.class}, (proxy, method, args) -> {
                if (!method.getName().equals("addSlot")) throw new AssertionError(method.getName());
                Slot slot = new Slot((RecipeIngredientRole) args[0], (int) args[1], (int) args[2], new ArrayList<>());
                slots.add(slot);
                return Proxy.newProxyInstance(JEILayoutValidation.class.getClassLoader(), new Class<?>[]{IRecipeSlotBuilder.class},
                    (slotProxy, slotMethod, slotArgs) -> {
                        switch (slotMethod.getName())
                        {
                            case "addItemStacks" -> {
                                for (Object stack : (List<?>) slotArgs[0]) slot.stacks.add((ItemStack) stack);
                            }
                            case "addItemStack" -> slot.stacks.add((ItemStack) slotArgs[0]);
                            case "setStandardSlotBackground", "setOutputSlotBackground" -> { }
                            default -> throw new AssertionError(slotMethod.getName());
                        }
                        return slotProxy;
                    });
            });
        JEIInfusionCategory.populateLayout(builder, recipe, output);
        require(slots.size() == 4, "pedestal, central, and output slots");
        require(slots.get(0).stacks.get(0).isOf(Items.DIAMOND), "first pedestal ingredient");
        require(slots.get(1).stacks.get(0).isOf(Items.LAPIS_LAZULI), "second pedestal ingredient");
        require(slots.get(0).x == 32 && slots.get(0).y == 5, "ring position");
        Slot central = slots.get(2);
        require(central.role == RecipeIngredientRole.INPUT && central.x == 32 && central.y == 34, "central input position");
        if (input == null)
            require(central.stacks.get(0).isOf(Items.BOOK), "plain book prerequisite");
        else
        {
            require(central.stacks.size() == 2, "all native level-four/five representatives");
            require(EnchantmentHelper.get(central.stacks.get(0)).get(Enchantments.EFFICIENCY) == 4, "native prerequisite enchantment");
        }
        Slot result = slots.get(3);
        require(result.role == RecipeIngredientRole.OUTPUT && result.x == 112 && result.y == 34, "output position");
        require(result.stacks.get(0) == output, "unmodified native result stack");
        require(output.isOf(Items.ENCHANTED_BOOK) && EnchantmentHelper.get(output).get(Enchantments.EFFICIENCY) == level,
            "native output enchantment and level");
    }

    private static void require(boolean condition, String message)
    {
        if (!condition) throw new AssertionError(message);
    }
}
