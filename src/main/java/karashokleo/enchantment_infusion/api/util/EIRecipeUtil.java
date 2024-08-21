package karashokleo.enchantment_infusion.api.util;

import karashokleo.enchantment_infusion.content.data.EnchantmentInfusionRecipeBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class EIRecipeUtil
{
    public static void add(Consumer<EnchantmentInfusionRecipeBuilder> consumer, Enchantment enchantment, int level, Consumer<RecipeJsonProvider> exporter, Identifier recipeId)
    {
        EnchantmentInfusionRecipeBuilder builder = new EnchantmentInfusionRecipeBuilder();
        consumer.accept(builder);
        builder.withTableIngredient(enchantment, level - 1)
                .offerTo(
                        exporter,
                        recipeId,
                        enchantment,
                        level
                );
    }

    public static void set(Consumer<EnchantmentInfusionRecipeBuilder> consumer, Enchantment enchantment, int level, Consumer<RecipeJsonProvider> exporter, Identifier recipeId)
    {
        EnchantmentInfusionRecipeBuilder builder = new EnchantmentInfusionRecipeBuilder();
        consumer.accept(builder);
        builder.offerTo(
                exporter,
                recipeId,
                enchantment,
                level
        );
    }
}
