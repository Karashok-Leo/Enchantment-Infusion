package karashokleo.enchantment_infusion.forge;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EIItems;
import karashokleo.enchantment_infusion.init.EIRecipes;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EnchantmentInfusion.MOD_ID)
public class EnchantmentInfusion
{
    public static final String MOD_ID = "enchantment_infusion";

    public EnchantmentInfusion()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EIBlocks.register(bus);
        EIItems.register(bus);
        bus.addListener(EIItems::addCreativeEntries);
        EIRecipes.register(bus);
    }

    public static Identifier id(String path)
    {
        return new Identifier(MOD_ID, path);
    }
}
