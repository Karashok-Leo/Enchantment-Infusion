package karashokleo.enchantment_infusion.content.recipe;

import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public record SimpleInfusionRecipe(
        Identifier id,
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
            output.getOrCreateNbt().copyFrom(tableStack.getOrCreateNbt());
        return output;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager)
    {
        return output;
    }

    @Override
    public Identifier getId()
    {
        return id;
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
