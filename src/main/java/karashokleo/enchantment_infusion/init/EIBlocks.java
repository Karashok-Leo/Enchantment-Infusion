package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.content.block.EnchantmentInfusionPedestalBlock;
import karashokleo.enchantment_infusion.content.block.EnchantmentInfusionTableBlock;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionPedestalTile;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionTableTile;
import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.property.BooleanProperty;

public class EIBlocks
{
    public static final BooleanProperty INFUSING = BooleanProperty.of("infusing");
    public static EnchantmentInfusionTableBlock INFUSION_TABLE;
    public static EnchantmentInfusionPedestalBlock INFUSION_PEDESTAL;
    public static BlockEntityType<EnchantmentInfusionTableTile> INFUSION_TABLE_TILE;
    public static BlockEntityType<EnchantmentInfusionPedestalTile> INFUSION_PEDESTAL_TILE;

    public static void register(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(RegistryKeys.BLOCK))
        {
            INFUSION_TABLE = new EnchantmentInfusionTableBlock();
            INFUSION_PEDESTAL = new EnchantmentInfusionPedestalBlock();
            event.register(RegistryKeys.BLOCK, helper -> {
                helper.register(EnchantmentInfusion.id("enchantment_infusion_table"), INFUSION_TABLE);
                helper.register(EnchantmentInfusion.id("enchantment_infusion_pedestal"), INFUSION_PEDESTAL);
            });
        }
        if (event.getRegistryKey().equals(RegistryKeys.BLOCK_ENTITY_TYPE))
        {
            INFUSION_TABLE_TILE = BlockEntityType.Builder.create(EnchantmentInfusionTableTile::new, INFUSION_TABLE).build(null);
            INFUSION_PEDESTAL_TILE = BlockEntityType.Builder.create(EnchantmentInfusionPedestalTile::new, INFUSION_PEDESTAL).build(null);
            event.register(RegistryKeys.BLOCK_ENTITY_TYPE, helper -> {
                helper.register(EnchantmentInfusion.id("enchantment_infusion_table"), INFUSION_TABLE_TILE);
                helper.register(EnchantmentInfusion.id("enchantment_infusion_pedestal"), INFUSION_PEDESTAL_TILE);
            });
        }
    }
}
