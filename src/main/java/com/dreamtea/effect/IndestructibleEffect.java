package com.dreamtea.effect;

import com.dreamtea.calback.DamageModifiers;
import com.dreamtea.calback.EntityDamageCallback;
import com.dreamtea.calback.EntityFallDamageCallback;
import com.dreamtea.imixin.IDisableItemStacks;
import com.dreamtea.imixin.IInteractingTool;
import com.dreamtea.tag.TagUtils;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.Wearable;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.dreamtea.imixin.IDisableItemStacks.disable;
import static com.dreamtea.imixin.IDisableItemStacks.getBroken;
import static com.dreamtea.tag.IndestructibleTag.INDESTRUCTIBLE_ITEMS;
public class IndestructibleEffect {
  public static void registerCallback(){
    UseItemCallback.EVENT.register(IndestructibleEffect::itemInteract);
    AttackBlockCallback.EVENT.register(IndestructibleEffect::attackInteract);
    UseBlockCallback.EVENT.register(IndestructibleEffect::useInteract);
    AttackEntityCallback.EVENT.register(IndestructibleEffect::attackEntity);
    EntityFallDamageCallback.EVENT.register(IndestructibleEffect::fallDamage);
    EntityDamageCallback.EVENT.register(IndestructibleEffect::damage);
  }

  public static boolean isNotUsable(ItemStack stack) {
    return stack.getDamage() >= stack.getMaxDamage();
  }

  public static boolean isIndestructible(ItemStack item){
    return TagUtils.itemIsIn(item, INDESTRUCTIBLE_ITEMS);
  }

  public static int doNotBreak(ItemStack stack, int amount, LivingEntity entity, Consumer<LivingEntity> breakCallback) {
    if(!isIndestructible(stack)){
      return amount;
    }
    stack.setDamage(Math.min(stack.getMaxDamage(), stack.getDamage() + amount));
    if(isNotUsable(stack) && !getBroken(stack)){
      ((IDisableItemStacks)(Object) stack).breakItem();
      breakCallback.accept(entity);
    }
    return 0;
  }

  private static ActionResult interact(PlayerEntity player, Hand hand, Supplier<ActionResult> onFail) {
    ItemStack item = player.getStackInHand(hand);
    if(isIndestructible(item) &&
      (IDisableItemStacks.getBroken(item) || isNotUsable(item))) {
      return onFail.get();
    }
    return ActionResult.PASS;
  }

  private static TypedActionResult<ItemStack> itemInteract(PlayerEntity player, World world, Hand hand){
    return new TypedActionResult<>(interact(
      player,
      hand,
      () -> {
        if(player.getStackInHand(hand).getItem() instanceof Wearable){
          return ActionResult.FAIL;
        }
        return ActionResult.PASS;
      }),
      player.getStackInHand(hand)
    );
  }

  private static ActionResult attackInteract(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
    return interact(
      player,
      hand,
      () -> {
        BlockState target = world.getBlockState(pos);
        Item item = player.getStackInHand(hand).getItem();
        if(item instanceof MiningToolItem tool){
          return tool.getMiningSpeedMultiplier(player.getStackInHand(hand), target) == 1.0F ?
            ActionResult.PASS : ActionResult.FAIL;
        }
        if(item instanceof SwordItem sword){
          return sword.getMiningSpeedMultiplier(player.getStackInHand(hand), target) == 1.0F ?
            ActionResult.PASS : ActionResult.FAIL;
        }
        return ActionResult.PASS;
      }
    );
  }

  private static ActionResult useInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
   return interact(
      player,
      hand,
      ()->{
        ItemStack item = player.getStackInHand(hand);
        if(item.getItem() instanceof IInteractingTool tool){
          BlockState block = world.getBlockState(hitResult.getBlockPos());
          if(tool.getBlocks().contains(block.getBlock())){
            return ActionResult.FAIL;
          }
        }
        return ActionResult.PASS;
      }
    );
  }

  private static ActionResult attackEntity(PlayerEntity player, World world, Hand hand,
                                           Entity entity, @Nullable EntityHitResult hitResult){
    return interact(
      player,
      hand,
      () -> {
        disable(player.getStackInHand(hand));
        player.attack(entity);
        IDisableItemStacks.enable(player.getStackInHand(hand));
        return ActionResult.FAIL;
      }
    );
  }

  private static TypedActionResult<DamageModifiers> damage(LivingEntity entity, DamageSource source, float damage){
    Iterable<ItemStack> items = entity.getArmorItems();
    List<ItemStack> broken = new ArrayList<>();
    for(ItemStack i: items){
      if(IDisableItemStacks.getBroken(i)
        || (i.isDamageable() && isIndestructible(i) && isNotUsable(i))){
        broken.add(i);
      }
    }
    if(broken.isEmpty()){
      return new TypedActionResult<>(ActionResult.PASS, new DamageModifiers());
    }
    return new TypedActionResult<>(ActionResult.FAIL, new DamageModifiers(broken, 1));
  }

  private static TypedActionResult<DamageModifiers> fallDamage(LivingEntity entity, float fallDistance, float damageMultiplier, DamageSource damageSource){
    return damage(entity, damageSource, 0);
  }


}
