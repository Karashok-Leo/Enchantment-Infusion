package karashokleo.enchantment_infusion.content.recipe;

import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

public record SimpleInfusionRecipe(
    Ingredient input,
    DefaultedList<Ingredient> ingredients,
    ItemStack output,
    boolean copyNbt
) implements InfusionRecipe
{
    @Override
    public Ingredient getTableIngredient()
    {
        return input;
    }

    @Override
    public DefaultedList<Ingredient> getPedestalIngredient()
    {
        return ingredients;
    }

    @Override
    public ItemStack infuse(ItemStack tableStack)
    {
        ItemStack output = this.output.copy();
        if (copyNbt)
        {
            // Copy stack-specific data without replacing the output item's default components.
            NbtComponent outputData = output.get(DataComponentTypes.CUSTOM_DATA);
            NbtComponent inputData = tableStack.get(DataComponentTypes.CUSTOM_DATA);
            output.applyChanges(tableStack.getComponentChanges());
            if (outputData != null && inputData != null)
            {
                // Preserve the recursive merge used by copy_nbt for custom data.
                NbtCompound merged = outputData.copyNbt();
                merged.copyFrom(inputData.copyNbt());
                output.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(merged));
            }
        }
        return output;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registryManager)
    {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return EIRecipes.SI_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return EIRecipes.INFUSION_RECIPE_TYPE;
    }
}
