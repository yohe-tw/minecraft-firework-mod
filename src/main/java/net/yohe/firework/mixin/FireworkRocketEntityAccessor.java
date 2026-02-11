package net.yohe.firework.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FireworkRocketEntity.class)
public interface FireworkRocketEntityAccessor {
    @Accessor("life")
    void setLife(int life);

    @Accessor("lifeTime")
    void setLifeTime(int lifeTime);

    @Accessor("life")
    int getLife();

    @Accessor("ITEM")
    TrackedData<ItemStack> getITEM();
}
