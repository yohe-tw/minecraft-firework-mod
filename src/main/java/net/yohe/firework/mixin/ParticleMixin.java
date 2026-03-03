package net.yohe.firework.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.yohe.firework.FireworkMod;

@Mixin(Particle.class)
public abstract class ParticleMixin {
    @Shadow
    protected int maxAge;

    @Shadow
    public abstract ParticleTextureSheet getType();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ClientWorld world, double x, double y, double z, CallbackInfo ci) {
        // 這裡可以根據邏輯強制把壽命改長
        // 例如：如果是特定的粒子，壽命延長到 100 ticks
        if (this.getType() == ParticleTypes.DUST) {
            this.maxAge = 100;
            FireworkMod.LOGGER.info("extend DUST life");
        }
    }
}
