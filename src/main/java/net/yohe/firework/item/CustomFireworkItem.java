package net.yohe.firework.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.yohe.firework.FireworkMod;
import net.yohe.firework.entity.CustomFireworkEntity; // 你的自定義實體
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.List;

public class CustomFireworkItem extends FireworkRocketItem {

    public CustomFireworkItem(Item.Settings settings) {
        super(settings);
    }

    // 取得物品上的自定義 shapeId
    public int getShapeId(ItemStack stack) {
        // 獲取 NbtComponent 組件
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);

        if (nbtComponent != null) {
            // 透過 copyNbt() 取得數據內容
            NbtCompound nbt = nbtComponent.copyNbt();
            if (nbt.contains("shapeId")) {
                return nbt.getInt("shapeId");
            }
        }
        return 1; // 預設值
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        int id = getShapeId(stack);
        // 根據 ID 顯示不同的名字 (這只是範例)
        // String name = (id == 2) ? "十字形" : "一直線";

        tooltip.add(Text.literal("客製化形狀: " + " (ID: " + id + ")")
                .formatted(Formatting.YELLOW));
    }

    // 1. 複寫：對著方塊點擊右鍵（插在地上發射）
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            ItemStack itemStack = context.getStack();
            Vec3d vec3d = context.getHitPos();
            Direction direction = context.getSide();
            // 改用你的 CustomFireworkEntity
            CustomFireworkEntity customEntity = new CustomFireworkEntity(
                    world,
                    vec3d.x + direction.getOffsetX() * 0.15,
                    vec3d.y + direction.getOffsetY() * 0.15,
                    vec3d.z + direction.getOffsetZ() * 0.15,
                    itemStack,
                    getShapeId(itemStack)
            );

            world.spawnEntity(customEntity);
            itemStack.decrement(1);
        }
        return ActionResult.success(world.isClient);
    }

    // 2. 複寫：空中使用（鞘翅加速）
//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        if (user.isFallFlying()) {
//            ItemStack itemStack = user.getStackInHand(hand);
//            if (!world.isClient) {
//                // 改用你的 CustomFireworkEntity
//                CustomFireworkEntity customEntity = new CustomFireworkEntity(world, itemStack, user);
//                world.spawnEntity(customEntity);
//
//                itemStack.decrementUnlessCreative(1, user);
//                user.incrementStat(Stats.USED.getOrCreateStat(this));
//            }
//            return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
//        } else {
//            return TypedActionResult.pass(user.getStackInHand(hand));
//        }
//    }

    // 3. 複寫：ProjectileItem 介面方法（用於發射器 Dispenser）
    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        // 改用你的 CustomFireworkEntity
        // 注意：這裡使用的建構子要對應你在 Entity 類別中定義的參數
        return new CustomFireworkEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount(1), getShapeId(stack));
    }

}