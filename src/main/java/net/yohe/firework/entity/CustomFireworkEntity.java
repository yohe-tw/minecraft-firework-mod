package net.yohe.firework.entity;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.yohe.firework.FireworkMod;
import net.yohe.firework.mixin.FireworkRocketEntityAccessor;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CustomFireworkEntity extends FireworkRocketEntity {

    // 1. 註冊同步變數 ID
    private static final TrackedData<Integer> CUSTOM_SHAPE_ID = DataTracker.registerData(CustomFireworkEntity.class, TrackedDataHandlerRegistry.INTEGER);
    // 2. 初始化 DataTracker
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CUSTOM_SHAPE_ID, 0); // 預設值為 0
    }

    public CustomFireworkEntity(EntityType<? extends FireworkRocketEntity> entityType, World world) {
        super(entityType, world);
    }

    // 這是讓你從 Item 召喚實體時使用的建構子
    public CustomFireworkEntity(World world, double x, double y, double z, ItemStack stack, int customShapeId) {
        // 關鍵修正：傳入你註冊的實體類型，而不是原版的
        super(ModEntities.CUSTOM_FIREWORK, world);
        // 3. 設定同步變數 (這會自動發送封包給客戶端)
        this.dataTracker.set(CUSTOM_SHAPE_ID, customShapeId);
        // 手動執行原版建構子在做的事情
        ((FireworkRocketEntityAccessor)this).setLife(0);
        this.setPosition(x, y, z);

        // 設定物品數據 (這對於渲染外觀與讀取 NBT 至關重要)
        // 注意：ITEM 是父類別的私有或靜態變數，如果抓不到，請參考下方的解決方式
        this.dataTracker.set(((FireworkRocketEntityAccessor)this).getITEM(), stack.copy());
        int i = 1;
        FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
        if (fireworksComponent != null) {
            i += fireworksComponent.flightDuration();
        }

        this.setVelocity(this.random.nextTriangular(0.0, 0.002297), 0.05, this.random.nextTriangular(0.0, 0.002297));
        ((FireworkRocketEntityAccessor)this).setLifeTime(10 * i + this.random.nextInt(6) + this.random.nextInt(7));

        FireworkMod.LOGGER.info("成功在伺服器端生成自定義煙火實體！");
    }

    private List<FireworkExplosionComponent> getCustomExplosions() {
        ItemStack itemStack = this.dataTracker.get(((FireworkRocketEntityAccessor)this).getITEM());
        FireworksComponent fireworksComponent = itemStack.get(DataComponentTypes.FIREWORKS);
        return fireworksComponent != null ? fireworksComponent.explosions() : List.of();
    }

    @Override
    public void handleStatus(byte status) {
        // 攔截爆炸狀態碼
        if (status == EntityStatuses.EXPLODE_FIREWORK_CLIENT && this.getWorld().isClient) {
            spawnLineParticles();
        } else {
            super.handleStatus(status);
        }
    }

    private void spawnLineParticles() {
        // 設定直線的長度與粒子密度
        int shapeId = this.dataTracker.get(CUSTOM_SHAPE_ID);
        int particleCount = 20 + shapeId * 2;
        double spacing = 0.3;

        // 取得當前的座標
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        // 在 X 軸上生成一條橫線
        for (int i = -particleCount / 2; i <= particleCount / 2; i++) {
            double offsetX = i * spacing;

            // 生成粒子。速度設為 0 以保持形狀，或稍微給一點隨機偏移增加質感
            this.getWorld().addParticle(
                    ParticleTypes.FIREWORK,
                    x + offsetX, y, z,
                    0, 0, 0
            );
        }

        // 也可以同時播放原版的爆炸音效
        this.getWorld().playSound(x, y, z,
                net.minecraft.sound.SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST,
                net.minecraft.sound.SoundCategory.AMBIENT, 2.0F, 1.0F, false);
    }
}