package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.content.block.EnchantmentInfusionPedestalBlock;
import karashokleo.enchantment_infusion.content.block.EnchantmentInfusionTableBlock;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionPedestalTile;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionTableTile;
import karashokleo.enchantment_infusion.fabric.EnchantmentInfusion;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.BooleanProperty;

public class EIBlocks
{
    public static final BooleanProperty INFUSING = BooleanProperty.of("infusing");
    public static EnchantmentInfusionTableBlock INFUSION_TABLE;
    public static EnchantmentInfusionPedestalBlock INFUSION_PEDESTAL;
    public static BlockEntityType<EnchantmentInfusionTableTile> INFUSION_TABLE_TILE;
    public static BlockEntityType<EnchantmentInfusionPedestalTile> INFUSION_PEDESTAL_TILE;

    public static void register()
    {
        INFUSION_TABLE = Registry.register(
                Registries.BLOCK,
                EnchantmentInfusion.id("enchantment_infusion_table"),
                new EnchantmentInfusionTableBlock()
        );
        INFUSION_PEDESTAL = Registry.register(
                Registries.BLOCK,
                EnchantmentInfusion.id("enchantment_infusion_pedestal"),
                new EnchantmentInfusionPedestalBlock()
        );
        INFUSION_TABLE_TILE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                EnchantmentInfusion.id("enchantment_infusion_table"),
                FabricBlockEntityTypeBuilder.create(EnchantmentInfusionTableTile::new, INFUSION_TABLE).build()
        );
        INFUSION_PEDESTAL_TILE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                EnchantmentInfusion.id("enchantment_infusion_pedestal"),
                FabricBlockEntityTypeBuilder.create(EnchantmentInfusionPedestalTile::new, INFUSION_PEDESTAL).build()
        );
    }
}
