package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipeSerializer;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipeSerializer;
import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class EIRecipes
{
    public static final ResourceLocation INFUSION_ID = EnchantmentInfusion.id("infusion");

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, EnchantmentInfusion.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, EnchantmentInfusion.MOD_ID);
    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, EnchantmentInfusion.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<InfusionRecipe>> INFUSION_RECIPE_TYPE = RECIPE_TYPES.register(INFUSION_ID.getPath(), () -> new RecipeType<InfusionRecipe>()
    {
        @Override
        public String toString()
        {
            return INFUSION_ID.getPath();
        }
    });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EnchantmentInfusionRecipe>> EI_SERIALIZER = RECIPE_SERIALIZERS.register(EnchantmentInfusion.MOD_ID, EnchantmentInfusionRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SimpleInfusionRecipe>> SI_SERIALIZER = RECIPE_SERIALIZERS.register("simple_infusion", SimpleInfusionRecipeSerializer::new);

    public static final DeferredHolder<IngredientType<?>, IngredientType<EnchantmentIngredient>> ENCHANTMENT_INGREDIENT_SERIALIZER = INGREDIENT_TYPES.register("enchantment",
        () -> new IngredientType<>(EnchantmentIngredient.CODEC, EnchantmentIngredient.PACKET_CODEC));

    public static void register(IEventBus bus)
    {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        INGREDIENT_TYPES.register(bus);
    }
}
