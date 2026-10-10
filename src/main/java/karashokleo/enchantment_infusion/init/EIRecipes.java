package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.api.recipe.EnchantmentIngredient;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipeSerializer;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipe;
import karashokleo.enchantment_infusion.content.recipe.SimpleInfusionRecipeSerializer;
import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class EIRecipes
{
    public static final Identifier INFUSION_ID = EnchantmentInfusion.id("infusion");

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(RegistryKeys.RECIPE_TYPE, EnchantmentInfusion.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(RegistryKeys.RECIPE_SERIALIZER, EnchantmentInfusion.MOD_ID);

    public static final RegistryObject<RecipeType<InfusionRecipe>> INFUSION_RECIPE_TYPE = RECIPE_TYPES.register(INFUSION_ID.getPath(), () -> new RecipeType<InfusionRecipe>()
    {
        @Override
        public String toString()
        {
            return INFUSION_ID.getPath();
        }
    });

    public static final RegistryObject<RecipeSerializer<EnchantmentInfusionRecipe>> EI_SERIALIZER = RECIPE_SERIALIZERS.register(EnchantmentInfusion.MOD_ID, EnchantmentInfusionRecipeSerializer::new);
    public static final RegistryObject<RecipeSerializer<SimpleInfusionRecipe>> SI_SERIALIZER = RECIPE_SERIALIZERS.register("simple_infusion", SimpleInfusionRecipeSerializer::new);

    public static final EnchantmentIngredient.Serializer ENCHANTMENT_INGREDIENT_SERIALIZER = new EnchantmentIngredient.Serializer();

    public static void register(IEventBus bus)
    {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        bus.addListener(EIRecipes::registerIngredientSerializer);
    }

    private static void registerIngredientSerializer(RegisterEvent event)
    {
        // Forge 1.20.1 ingredient serializers use CraftingHelper, not a registry.
        if (event.getRegistryKey().equals(RegistryKeys.RECIPE_SERIALIZER))
            CraftingHelper.register(EnchantmentInfusion.id("enchantment"), ENCHANTMENT_INGREDIENT_SERIALIZER);
    }
}
