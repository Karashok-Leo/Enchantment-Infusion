package karashokleo.enchantment_infusion.neoforge;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EIItems;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(EnchantmentInfusion.MOD_ID)
public class EnchantmentInfusion
{
    public static final String MOD_ID = "enchantment_infusion";

    public EnchantmentInfusion(IEventBus modBus)
    {
        modBus.addListener(EIBlocks::register);
        modBus.addListener(EIItems::register);
        modBus.addListener(EIItems::addCreativeItems);
        modBus.addListener(EIRecipes::register);
        modBus.addListener(EnchantmentInfusionDataGenerator::gatherData);
    }

    public static ResourceLocation id(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
