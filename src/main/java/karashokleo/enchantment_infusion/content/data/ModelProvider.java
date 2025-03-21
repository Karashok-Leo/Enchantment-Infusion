package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.util.Identifier;

public class ModelProvider extends FabricModelProvider
{
    public ModelProvider(FabricDataOutput output)
    {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator)
    {
        registerStateWithBooleanProperty(blockStateModelGenerator, EIBlocks.INFUSION_TABLE);
        registerStateWithBooleanProperty(blockStateModelGenerator, EIBlocks.INFUSION_PEDESTAL);
//        blockStateModelGenerator.registerSimpleState(EIBlocks.INFUSION_TABLE);
//        blockStateModelGenerator.registerSimpleState(EIBlocks.INFUSION_PEDESTAL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator)
    {
    }

    private static void registerStateWithBooleanProperty(BlockStateModelGenerator generator, Block block)
    {
        Identifier trueModel = ModelIds.getBlockSubModelId(block, "_infusing");
        Identifier falseModel = ModelIds.getBlockModelId(block);

        generator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(block)
                        .coordinate(
                                BlockStateModelGenerator.createBooleanModelMap(EIBlocks.INFUSING, trueModel, falseModel)
                        )
        );
    }
}
