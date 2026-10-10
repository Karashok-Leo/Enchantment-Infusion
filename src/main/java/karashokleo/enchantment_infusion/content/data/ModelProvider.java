package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.forge.EnchantmentInfusion;
import karashokleo.enchantment_infusion.init.EIBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.client.ModelIds;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModelProvider extends BlockStateProvider
{
    public ModelProvider(DataOutput output, ExistingFileHelper existingFileHelper)
    {
        super(output, EnchantmentInfusion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        registerStateWithBooleanProperty(EIBlocks.INFUSION_TABLE.get());
        registerStateWithBooleanProperty(EIBlocks.INFUSION_PEDESTAL.get());
    }

    private void registerStateWithBooleanProperty(Block block)
    {
        ModelFile trueModel = models().getExistingFile(ModelIds.getBlockSubModelId(block, "_infusing"));
        ModelFile falseModel = models().getExistingFile(ModelIds.getBlockModelId(block));

        getVariantBuilder(block)
            .partialState().with(EIBlocks.INFUSING, true)
                .modelForState().modelFile(trueModel).addModel()
            .partialState().with(EIBlocks.INFUSING, false)
                .modelForState().modelFile(falseModel).addModel();
        simpleBlockItem(block, falseModel);
    }
}
