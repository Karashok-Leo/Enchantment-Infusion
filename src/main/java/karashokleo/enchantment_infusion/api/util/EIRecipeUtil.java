package karashokleo.enchantment_infusion.api.util;

import karashokleo.enchantment_infusion.content.data.EnchantmentInfusionRecipeBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class EIRecipeUtil
{
    public static void add(Consumer<EnchantmentInfusionRecipeBuilder> consumer, Holder<Enchantment> enchantment, int level, RecipeOutput exporter, ResourceLocation recipeId)
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

    public static void set(Consumer<EnchantmentInfusionRecipeBuilder> consumer, Holder<Enchantment> enchantment, int level, RecipeOutput exporter, ResourceLocation recipeId)
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
