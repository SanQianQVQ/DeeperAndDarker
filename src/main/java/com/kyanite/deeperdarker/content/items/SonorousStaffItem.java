package com.kyanite.deeperdarker.content.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@SuppressWarnings("NullableProblems")
public class SonorousStaffItem extends Item {
    public double dropOffFactor = 1/3.0;

    public SonorousStaffItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if(!(pLivingEntity instanceof Player player)) return;

        int timeUsed = getUseDuration(pStack) - pTimeCharged;
        int damage = (int) Math.round(50 / (1 + 16 / Math.exp(0.06 * timeUsed)));
        int range = (int) Math.min(40, Math.round(3 * Math.log(timeUsed + 1)));

        Vec3 eyePos = player.getEyePosition();
        Vec3 facing = player.getForward();
        for(int i = 0; i < range; i++) {
            Vec3 scanVec = eyePos.add(facing.scale(i));
            BlockPos targetPos = new BlockPos((int) scanVec.x, (int) scanVec.y, (int) scanVec.z);
            BlockState targetState = pLevel.getBlockState(targetPos);

            if(!targetState.isAir() && targetState.canOcclude()) break;
            if(pLevel.isClientSide() && i % 2 == 0) pLevel.addParticle(ParticleTypes.SONIC_BOOM, scanVec.x, scanVec.y, scanVec.z, 1, 0, 0);

            AABB aabb = new AABB(targetPos).inflate(0.4);
            List<LivingEntity> targets = pLevel.getEntitiesOfClass(LivingEntity.class, aabb);
            for(LivingEntity entity : targets) {
                if(entity.is(player)) continue;

                int finalDamage = (int) (damage * (1 - dropOffFactor * Math.pow((double) i / range, 2)));
                entity.hurt(pLevel.damageSources().sonicBoom(player), finalDamage);
                double horizontalResistance = 1 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                double verticalResistance = 1 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                entity.push(facing.x * horizontalResistance, facing.y * verticalResistance, facing.z * horizontalResistance);
            }
        }

        player.playSound(SoundEvents.WARDEN_SONIC_BOOM);
        pStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, 20);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if(pEntity instanceof Player player) {
            pStack.getOrCreateTag().putBoolean("charged", player.getUseItem() == pStack && pStack.getUseDuration() - player.getUseItemRemainingTicks() >= 123);
        }
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return super.isFoil(pStack) || (pStack.hasTag() && pStack.getTag().getBoolean("charged"));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }
}
