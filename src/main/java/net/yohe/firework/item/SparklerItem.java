package net.yohe.firework.item; // 請確認這是你的實際包名

import net.fabricmc.fabric.impl.recipe.ingredient.builtin.CustomDataIngredient;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes; // 引入粒子類型
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.component.type.NbtComponent; // 在 1.21 中，通常使用 NbtComponent
import net.minecraft.nbt.NbtCompound;
import org.joml.Vector3f;

public class SparklerItem extends VerticallyAttachableBlockItem {


    public SparklerItem(Block block, Settings settings) {
        // 第一個參數是地上的方塊，第二個是牆上的方塊（這裡我們先用同一個）
        super(block, block, settings, Direction.DOWN);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // 如果玩家「沒按住潛行鍵」，強制攔截放置行為
        if (context.getPlayer() != null && !context.getPlayer().isSneaking()) {
            // 這裡回傳 PASS，代表「我不放方塊」，
            // 這樣系統才會接著去執行你的 use() 方法（開關邏輯）
            return ActionResult.PASS;
        }

        // 如果按著潛行鍵，呼叫父類別邏輯（執行放置）
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && !user.isSneaking()) {
            // 1. 取得 NbtComponent，如果沒有則建立空的
            NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);

            // 2. 透過 copyNbt() 拿到可修改的 NbtCompound
            NbtCompound nbt = nbtComponent.copyNbt();

            // 3. 切換狀態
            boolean isActive = nbt.getBoolean("active");
            nbt.putBoolean("active", !isActive);

            // 4. 使用 NbtComponent.of() 寫回
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }

    // 這個方法在物品位於玩家物品欄中時，每一遊戲刻都會被呼叫。
    // 這很適合用來處理手持物品的持續效果。
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // 確保只有在伺服器端才處理邏輯，然後在客戶端生成粒子
        // World.isClient() 會在客戶端返回 true，伺服器端返回 false
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        boolean active = nbtComponent.copyNbt().getBoolean("active");

        // entity instanceof PlayerEntity player: if entity is playerEntity then return true and also assign entity to var player
        if (active && world.isClient() && entity instanceof PlayerEntity player && selected) {
            // 檢查玩家是否正在手持這個物品 (selected 為 true)
            // 或檢查是否在主手或副手
            boolean isHolding = player.getMainHandStack() == stack || player.getOffHandStack() == stack;
            // get player yaw
            float yaw = player.getYaw() * 0.017453292F;
            double offsetX = -Math.cos(yaw) * 0.4 - Math.sin(yaw) * 0.8;
            double offsetZ = -Math.sin(yaw) * 0.4 + Math.cos(yaw) * 0.8;
            Vector3f color = new Vector3f(1.0f, 0.5f, 0.0f); // 橙色火花
            float scale = 1.0f; // 粒子大小

            if (isHolding) {
                // 生成粒子效果
                // 這裡使用了 FLAME 和 SMOKE 粒子，你可以嘗試其他 ParticleTypes
                world.addParticle(new DustParticleEffect(color, scale),
                        player.getX() + (world.random.nextDouble() - 0.5) * 0.4 + offsetX, // 隨機 X 偏移
                        player.getY() + player.getEyeHeight(player.getPose()) - 0.5, // 靠近手部位置
                        player.getZ() + (world.random.nextDouble() - 0.5) * 0.4 + offsetZ, // 隨機 Z 偏移
                        0.0, 0.05, 0.0); // 粒子速度 (x, y, z)

                world.addParticle(ParticleTypes.SMOKE,
                        player.getX() + (world.random.nextDouble() - 0.5) * 0.4 + offsetX,
                        player.getY() + player.getEyeHeight(player.getPose()) - 0.5,
                        player.getZ() + (world.random.nextDouble() - 0.5) * 0.4 + offsetZ,
                        0.0, 0.02, 0.0);

                // 每隔一段時間播放音效，避免過於頻繁
                if (world.random.nextInt(5) == 0) { // 大約每 5 刻播放一次
                    world.playSound(player, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, // 煙火爆炸聲
                            SoundCategory.PLAYERS,
                            0.3F, // 音量
                            0.8F + world.random.nextFloat() * 0.4F); // 音高隨機變化
                }
            }
        }
    }
}
