package karashokleo.enchantment_infusion.init;

import karashokleo.enchantment_infusion.content.block.EnchantmentInfusionPedestalBlock;
import karashokleo.enchantment_infusion.content.block.EnchantmentInfusionTableBlock;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionPedestalTile;
import karashokleo.enchantment_infusion.content.block.entity.EnchantmentInfusionTableTile;
import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class EIBlocks
{
    public static final BooleanProperty INFUSING = BooleanProperty.create("infusing");
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EnchantmentInfusion.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EnchantmentInfusion.MOD_ID);

    public static final DeferredBlock<EnchantmentInfusionTableBlock> INFUSION_TABLE = BLOCKS.register("enchantment_infusion_table", () -> new EnchantmentInfusionTableBlock());
    public static final DeferredBlock<EnchantmentInfusionPedestalBlock> INFUSION_PEDESTAL = BLOCKS.register("enchantment_infusion_pedestal", () -> new EnchantmentInfusionPedestalBlock());
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchantmentInfusionTableTile>> INFUSION_TABLE_TILE = BLOCK_ENTITIES.register("enchantment_infusion_table",
        () -> BlockEntityType.Builder.of(EnchantmentInfusionTableTile::new, INFUSION_TABLE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchantmentInfusionPedestalTile>> INFUSION_PEDESTAL_TILE = BLOCK_ENTITIES.register("enchantment_infusion_pedestal",
        () -> BlockEntityType.Builder.of(EnchantmentInfusionPedestalTile::new, INFUSION_PEDESTAL.get()).build(null));

    public static void register(IEventBus bus)
    {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }
}
