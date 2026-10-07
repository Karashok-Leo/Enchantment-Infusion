package karashokleo.enchantment_infusion.content.compat.jei;

import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

/** Native stacks retain enchantment components for JEI's normal subtype matching. */
public record JEIInfusionRecipe(Identifier id, List<ItemStack> table, List<List<ItemStack>> pedestals, ItemStack output)
{
    public JEIInfusionRecipe(RecipeEntry<? extends InfusionRecipe> recipe, RegistryWrapper.WrapperLookup registries)
    {
        this(recipe.id(), matchingStacks(recipe.value().getTableIngredient()),
            recipe.value().getPedestalIngredient().stream().map(JEIInfusionRecipe::matchingStacks).toList(),
            recipe.value().getResult(registries).copy());
    }

    static List<ItemStack> matchingStacks(Ingredient ingredient)
    {
        // Explicitly enumerate Fabric custom ingredients: their examples can carry components.
        var custom = ingredient.getCustomIngredient();
        var stacks = custom == null ? Arrays.asList(ingredient.getMatchingStacks()) : custom.getMatchingStacks();
        return stacks.stream().map(ItemStack::copy).toList();
    }
}
