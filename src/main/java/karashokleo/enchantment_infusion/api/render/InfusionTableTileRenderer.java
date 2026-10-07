package karashokleo.enchantment_infusion.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import karashokleo.enchantment_infusion.api.block.entity.AbstractInfusionTile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;

public class InfusionTableTileRenderer<T extends AbstractInfusionTile> implements BlockEntityRenderer<T>
{
    private final float yOffset;
    private final ItemRenderer itemRenderer;

    public InfusionTableTileRenderer(float yOffset, BlockEntityRendererProvider.Context ctx)
    {
        this.yOffset = yOffset;
        this.itemRenderer = ctx.getItemRenderer();
    }

    @SuppressWarnings("all")
    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay)
    {
        if (entity.getTheItem() == null || entity.getTheItem().isEmpty())
        {
            return;
        }

        matrices.pushPose();
        matrices.translate(0.5f, yOffset, 0.5f);
        matrices.scale(0.5f, 0.5f, 0.5f);
        matrices.mulPose(Axis.YP.rotationDegrees((tickDelta + entity.getLevel().getGameTime()) * 3f));
        itemRenderer.renderStatic(
            entity.getTheItem(),
            ItemDisplayContext.FIXED,
            light,
            overlay,
            matrices,
            vertexConsumers,
            entity.getLevel(),
            (int) entity.getBlockPos().asLong()
        );
        matrices.popPose();
    }
}
