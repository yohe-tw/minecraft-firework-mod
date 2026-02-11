package net.yohe.firework;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.yohe.firework.entity.ModEntities;
import net.yohe.firework.item.ModItems;

public class FireworkModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModItems.SPARKLER_BLOCK, RenderLayer.getCutout());
        EntityRendererRegistry.register(ModEntities.CUSTOM_FIREWORK, FlyingItemEntityRenderer::new);
    }
}
