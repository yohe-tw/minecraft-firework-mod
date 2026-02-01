package net.yohe.firework.item;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.TorchBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.yohe.firework.FireworkMod;

public class ModItems {
    // 1. 建立方塊物件 (設定發光等級為 15)
    public static final Block SPARKLER_BLOCK = Registry.register(Registries.BLOCK, Identifier.of(FireworkMod.MOD_ID, "sparkler"), new TorchBlock(
            ParticleTypes.FLAME, // 放置後方塊上的粒子
            AbstractBlock.Settings.create()
                    .noCollision()
                    .breakInstantly()
                    .luminance(state -> 15) // 設定亮度為滿分 15
                    .sounds(BlockSoundGroup.WOOD)
    ));
    public static final Item FAKE_DIAMOND = Registry.register(Registries.ITEM, Identifier.of(FireworkMod.MOD_ID, "fake_diamond"), new Item(new Item.Settings()));
    public static final Item SPARKLER = Registry.register(Registries.ITEM, Identifier.of(FireworkMod.MOD_ID, "sparkler"), new SparklerItem(SPARKLER_BLOCK, new Item.Settings().maxCount(1)));

    public static void RegistryItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(FAKE_DIAMOND);
            entries.add(SPARKLER);
        });
    }
}
