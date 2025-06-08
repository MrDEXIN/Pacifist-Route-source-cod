package name.modid.client;

import name.modid.client.renderer.MortarCompositeRenderer;
import name.modid.client.renderer.MortarHudRenderer;
import name.modid.block.ModBlocks;
import name.modid.block.entity.ModBlockEntities;
import name.modid.event.MouseScrollHandler;
import name.modid.particle.ModParticles;
import name.modid.particle.YellowSmokeParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderLayer.getTranslucent(),
                ModBlocks.NITRE_CRYSTAL_BLOCK,
                ModBlocks.BUDDING_NITRE,
                ModBlocks.TINY_NITRE_BUD,
                ModBlocks.SMALL_NITRE_BUD,
                ModBlocks.MEDIUM_NITRE_BUD,
                ModBlocks.LARGE_NITRE_BUD,
                ModBlocks.NITRE_CLUSTER,
                ModBlocks.SULFUR_IN_DEEP,
                ModBlocks.SULFUR_IN_NETHERRACK,
                ModBlocks.SULFUR_IN_STONE
        );
        ParticleFactoryRegistry.getInstance().register(ModParticles.YELLOW_SMOKE, YellowSmokeParticle.Factory::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MORTAR_BLOCK, RenderLayer.getSolid());

        BlockEntityRendererRegistry.register(
                ModBlockEntities.MORTAR_BLOCK_ENTITY,
                MortarCompositeRenderer::new
        );

        MouseScrollHandler.register();
        MortarHudRenderer.register();
    }
}