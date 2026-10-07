package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.content.block.EnchantmentInfusionPedestalBlock;
import karashokleo.enchantment_infusion.content.block.EnchantmentInfusionTableBlock;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionPedestalTile;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionTableTile;
import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class EIBlocks
{
    public static final BooleanProperty INFUSING = BooleanProperty.create("infusing");
    public static EnchantmentInfusionTableBlock INFUSION_TABLE;
    public static EnchantmentInfusionPedestalBlock INFUSION_PEDESTAL;
    public static BlockEntityType<EnchantmentInfusionTableTile> INFUSION_TABLE_TILE;
    public static BlockEntityType<EnchantmentInfusionPedestalTile> INFUSION_PEDESTAL_TILE;

    public static void register(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(Registries.BLOCK))
        {
            INFUSION_TABLE = Registry.register(
                BuiltInRegistries.BLOCK,
                EnchantmentInfusion.id("enchantment_infusion_table"),
                new EnchantmentInfusionTableBlock()
            );
            INFUSION_PEDESTAL = Registry.register(
                BuiltInRegistries.BLOCK,
                EnchantmentInfusion.id("enchantment_infusion_pedestal"),
                new EnchantmentInfusionPedestalBlock()
            );
        }
        if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE))
        {
            INFUSION_TABLE_TILE = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                EnchantmentInfusion.id("enchantment_infusion_table"),
                BlockEntityType.Builder.of(EnchantmentInfusionTableTile::new, INFUSION_TABLE).build(null)
            );
            INFUSION_PEDESTAL_TILE = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                EnchantmentInfusion.id("enchantment_infusion_pedestal"),
                BlockEntityType.Builder.of(EnchantmentInfusionPedestalTile::new, INFUSION_PEDESTAL).build(null)
            );
        }
    }
}
