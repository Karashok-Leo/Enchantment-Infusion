package karashokleo.enchantment_infusion.fabric;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EIItems;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

public class EnchantmentInfusion implements ModInitializer
{
    public static final String MOD_ID = "enchantment_infusion";

    @Override
    public void onInitialize()
    {
        EIBlocks.register();
        EIItems.register();
        EIRecipes.register();
    }

    public static Identifier id(String path)
    {
        return new Identifier(MOD_ID, path);
    }
}