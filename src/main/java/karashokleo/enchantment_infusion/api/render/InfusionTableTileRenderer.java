package karashokleo.enchantment_infusion.api.render;

import karashokleo.enchantment_infusion.api.block.entity.AbstractInfusionTile;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class InfusionTableTileRenderer<T extends AbstractInfusionTile> implements BlockEntityRenderer<T>
{
    private final float yOffset;
    private final ItemRenderer itemRenderer;

    public InfusionTableTileRenderer(float yOffset, BlockEntityRendererFactory.Context ctx)
    {
        this.yOffset = yOffset;
        this.itemRenderer = ctx.getItemRenderer();
    }

    @SuppressWarnings("all")
    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        if (entity.getStack() == null || entity.getStack().isEmpty()) return;

        matrices.push();
        matrices.translate(0.5f, yOffset, 0.5f);
        matrices.scale(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((tickDelta + entity.getWorld().getTime()) * 3f));
        itemRenderer.renderItem(
                entity.getStack(),
                ModelTransformationMode.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                entity.getWorld(),
                (int) entity.getPos().asLong()
        );
        matrices.pop();
    }
}
