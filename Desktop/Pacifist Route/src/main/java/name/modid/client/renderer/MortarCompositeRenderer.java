package name.modid.client.renderer;

import name.modid.block.entity.MortarBlockEntity;
import name.modid.client.model.MortarBlockEntityModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MortarCompositeRenderer implements BlockEntityRenderer<MortarBlockEntity> {
    private final GeoBlockRenderer<MortarBlockEntity> geoRenderer;

    public MortarCompositeRenderer(BlockEntityRendererFactory.Context ctx) {
        this.geoRenderer = new GeoBlockRenderer<>(new MortarBlockEntityModel());
    }

    @Override
    public void render(MortarBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        geoRenderer.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);
        renderItems(entity, matrices, vertexConsumers, light, overlay);
    }

    private void renderItems(MortarBlockEntity entity, MatrixStack matrices,
                             VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 0.15, 0.5);
        int renderedItems = 0;
        for (int i = 0; i < entity.size() && renderedItems < 5; i++) {
            ItemStack stack = entity.getStack(i);

            if (stack != null && !stack.isEmpty() && stack.getCount() > 0) {
                matrices.push();
                matrices.translate(0, renderedItems * 0.04f, 0);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
                matrices.scale(0.3f, 0.3f, 0.3f);
                MinecraftClient.getInstance().getItemRenderer().renderItem(
                        stack, ModelTransformationMode.FIXED, light,
                        OverlayTexture.DEFAULT_UV, matrices, vertexConsumers,
                        entity.getWorld(), (int) entity.getPos().asLong() + renderedItems);
                matrices.pop();
                renderedItems++;
            }
        }
        matrices.pop();
    }
}