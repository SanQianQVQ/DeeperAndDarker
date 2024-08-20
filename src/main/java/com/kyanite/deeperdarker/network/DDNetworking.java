package com.kyanite.deeperdarker.network;

import com.kyanite.deeperdarker.DeeperDarker;
import com.kyanite.deeperdarker.content.DDItems;
import com.kyanite.deeperdarker.content.items.SculkTransmitterItem;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class DDNetworking {
    public static void registerPayloadTypes() {
        PayloadTypeRegistry.playC2S().register(SoulElytraBoostPayload.TYPE, SoulElytraBoostPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(UseTransmitterPayload.TYPE, UseTransmitterPayload.CODEC);
    }

    public static void registerMessages() {
        ServerPlayNetworking.registerGlobalReceiver(SoulElytraBoostPayload.TYPE, (payload, ctx) -> {
            ServerPlayer player = ctx.player();
            Level level = player.level();
            if (DeeperDarker.CONFIG.server.soulElytraCooldown() == -1) {
                player.displayClientMessage(Component.translatable(DDItems.SOUL_ELYTRA.getDescriptionId() + ".boost_disabled").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                return;
            }
            if(player.isFallFlying() && player.getInventory().armor.get(2).is(DDItems.SOUL_ELYTRA) && !player.getCooldowns().isOnCooldown(DDItems.SOUL_ELYTRA) && DeeperDarker.CONFIG.server.soulElytraCooldown() != -1) {
                FireworkRocketEntity rocket = new FireworkRocketEntity(level, new ItemStack(Items.FIREWORK_ROCKET), player);
                level.addFreshEntity(rocket);
                player.getCooldowns().addCooldown(DDItems.SOUL_ELYTRA, DeeperDarker.CONFIG.server.soulElytraCooldown());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(UseTransmitterPayload.TYPE, (payload, ctx) -> {
            ServerPlayer player = ctx.player();
            for(ItemStack stack : player.getInventory().items) {
                if(stack.is(DDItems.SCULK_TRANSMITTER) && SculkTransmitterItem.isLinked(stack)) {
                    SculkTransmitterItem.transmit(player.level(), player, stack, null);
                    break;
                }
            }
        });
    }
}