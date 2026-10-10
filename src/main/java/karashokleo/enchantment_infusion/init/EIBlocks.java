package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.content.block.EnchantmentInfusionPedestalBlock;
import karashokleo.enchantment_infusion.content.block.EnchantmentInfusionTableBlock;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionPedestalTile;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionTableTile;
import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.property.BooleanProperty;

public class EIBlocks
{
    public static final BooleanProperty INFUSING = BooleanProperty.of("infusing");
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(RegistryKeys.BLOCK, EnchantmentInfusion.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(RegistryKeys.BLOCK_ENTITY_TYPE, EnchantmentInfusion.MOD_ID);

    public static final RegistryObject<EnchantmentInfusionTableBlock> INFUSION_TABLE = BLOCKS.register("enchantment_infusion_table", EnchantmentInfusionTableBlock::new);
    public static final RegistryObject<EnchantmentInfusionPedestalBlock> INFUSION_PEDESTAL = BLOCKS.register("enchantment_infusion_pedestal", EnchantmentInfusionPedestalBlock::new);
    public static final RegistryObject<BlockEntityType<EnchantmentInfusionTableTile>> INFUSION_TABLE_TILE = BLOCK_ENTITIES.register("enchantment_infusion_table",
        () -> BlockEntityType.Builder.create(EnchantmentInfusionTableTile::new, INFUSION_TABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<EnchantmentInfusionPedestalTile>> INFUSION_PEDESTAL_TILE = BLOCK_ENTITIES.register("enchantment_infusion_pedestal",
        () -> BlockEntityType.Builder.create(EnchantmentInfusionPedestalTile::new, INFUSION_PEDESTAL.get()).build(null));

    public static void register(IEventBus bus)
    {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }
}
