package net.yohe.firework;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.yohe.firework.item.ModItems;

public class FireworkModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModItems.SPARKLER_BLOCK, RenderLayer.getCutout());
    }
}
