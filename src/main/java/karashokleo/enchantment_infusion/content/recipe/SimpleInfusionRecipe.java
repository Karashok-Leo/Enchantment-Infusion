package karashokleo.enchantment_infusion.content.recipe;

import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public record SimpleInfusionRecipe(
    Ingredient input,
    NonNullList<Ingredient> ingredients,
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
    public NonNullList<Ingredient> getPedestalIngredient()
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
            CustomData outputData = output.get(DataComponents.CUSTOM_DATA);
            CustomData inputData = tableStack.get(DataComponents.CUSTOM_DATA);
            output.applyComponentsAndValidate(tableStack.getComponentsPatch());
            if (outputData != null && inputData != null)
            {
                // Preserve the recursive merge used by copy_nbt for custom data.
                CompoundTag merged = outputData.copyTag();
                merged.merge(inputData.copyTag());
                output.set(DataComponents.CUSTOM_DATA, CustomData.of(merged));
            }
        }
        return output;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryManager)
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
