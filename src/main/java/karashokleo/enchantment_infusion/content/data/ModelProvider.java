package karashokleo.enchantment_infusion.content.data;

import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.neoforge.EnchantmentInfusion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModelProvider extends BlockStateProvider
{
    public ModelProvider(PackOutput output, ExistingFileHelper existingFileHelper)
    {
        super(output, EnchantmentInfusion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        registerStateWithBooleanProperty(EIBlocks.INFUSION_TABLE);
        registerStateWithBooleanProperty(EIBlocks.INFUSION_PEDESTAL);
    }

    private void registerStateWithBooleanProperty(Block block)
    {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        ModelFile falseModel = models().getExistingFile(blockId.withPrefix("block/"));
        ModelFile trueModel = models().getExistingFile(blockId.withPrefix("block/").withSuffix("_infusing"));

        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
            .modelFile(state.getValue(EIBlocks.INFUSING) ? trueModel : falseModel)
            .build());
        simpleBlockItem(block, falseModel);
    }
}
