package net.yohe.firework.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.yohe.firework.FireworkMod;

public class ModEntities {
    // 1. 建立方塊物件 (設定發光等級為 15)
    public static final EntityType<CustomFireworkEntity> CUSTOM_FIREWORK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(FireworkMod.MOD_ID, "custom_firework"),
            EntityType.Builder.<CustomFireworkEntity>create(CustomFireworkEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    // 修正處：對應原始碼中的 maxTrackingRange 變數
                    .maxTrackingRange(4)
                    // 修正處：對應原始碼中的 trackingTickInterval 變數
                    .trackingTickInterval(10)
                    .build("custom_firework") // 注意：1.21 的 build 方法通常需要傳入 ID 字串
    );

    public static void registerModEntities() {
        // 在主類別呼叫此方法以加載靜態欄位
    }
}
