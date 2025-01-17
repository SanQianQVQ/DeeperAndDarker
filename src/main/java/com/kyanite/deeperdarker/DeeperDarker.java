package com.kyanite.deeperdarker;

import com.kyanite.deeperdarker.content.*;
import com.kyanite.deeperdarker.content.blocks.OthersidePortalFrameTester;
import com.kyanite.deeperdarker.network.Messages;
import com.kyanite.deeperdarker.util.AncientPaintings;
import com.kyanite.deeperdarker.util.DDConfig;
import com.kyanite.deeperdarker.util.DDCreativeTab;
import com.kyanite.deeperdarker.util.DDLootItemFunctions;
import com.kyanite.deeperdarker.world.DDFeatures;
import com.kyanite.deeperdarker.world.otherside.OthersideDimension;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.CustomPortalBlock;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.kyrptonaught.customportalapi.event.CPASoundEventData;
import net.kyrptonaught.customportalapi.portal.PortalIgnitionSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DeeperDarker implements ModInitializer {
	public static final String MOD_ID = "deeperdarker";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ResourceLocation OTHERSIDE_FRAME_TESTER = new ResourceLocation(MOD_ID, "otherside");

	public static final boolean SHOW_ME_YOUR_SKIN = FabricLoader.getInstance().isModLoaded("showmeyourskin");

	public static final DDConfig CONFIG = DDConfig.createAndLoad();

	@Override
	public void onInitialize() {
		DDCreativeTab.init();
		DDItems.init();
		DDBlocks.init();
		DDFeatures.init();
		DDSounds.init();
		DDPotions.init();
		DDEnchantments.init();
		DDEntities.init();
		DDBlockEntities.init();
		DDEffects.init();
		AncientPaintings.init();
		DDLootItemFunctions.init();

		CustomPortalBuilder.beginPortal()
				.customFrameTester(OTHERSIDE_FRAME_TESTER)
				.frameBlock(Blocks.REINFORCED_DEEPSLATE)
				.customIgnitionSource(PortalIgnitionSource.ItemUseSource(DDItems.HEART_OF_THE_DEEP))
				.destDimID(new ResourceLocation(DeeperDarker.MOD_ID, "otherside"))
				.tintColor(5, 98, 93)
				.customPortalBlock((CustomPortalBlock) DDBlocks.OTHERSIDE_PORTAL)
				.forcedSize(8, 4)
				.registerInPortalAmbienceSound((player) -> new CPASoundEventData(DDSounds.PORTAL_GROAN, 1.0f, 1.0f))
				.registerPortal();

		CustomPortalApiRegistry.registerPortalFrameTester(OTHERSIDE_FRAME_TESTER, OthersidePortalFrameTester::new);

		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (source == LootTableSource.DATA_PACK) return;

			if (EntityType.WARDEN.getDefaultLootTable().equals(id)) {
				LootPool.Builder carapacePoolBuilder = LootPool.lootPool()
						.add(LootItem.lootTableItem(DDItems.WARDEN_CARAPACE).apply(SetItemCountFunction.setCount(
								UniformGenerator.between(1.0f, 3.0f))));
				LootPool.Builder heartPoolBuilder = LootPool.lootPool()
						.add(LootItem.lootTableItem(DDItems.HEART_OF_THE_DEEP));

				tableBuilder.withPool(carapacePoolBuilder);
				tableBuilder.withPool(heartPoolBuilder);
			}
			if (BuiltInLootTables.ANCIENT_CITY.equals(id)) {
				LootPool.Builder carapacePoolBuilder = LootPool.lootPool()
						.add(LootItem.lootTableItem(DDItems.WARDEN_CARAPACE)
								.when(LootItemRandomChanceCondition.randomChance(0.2f)));

				tableBuilder.withPool(carapacePoolBuilder);
				tableBuilder.withPool(wardenUpgradePoolBuilder);
			}
		});

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity.getType() == EntityType.PLAYER && entity.getUUID().equals(UUID.fromString("7bb71eb9-b55e-4071-9175-8ec2f42ddd79")) && entity.level().dimension().equals(OthersideDimension.OTHERSIDE_LEVEL)) {
                double xm = Mth.randomBetween(entity.level().getRandom(), -0.2f, 0.2f);
                double ym = Mth.randomBetween(entity.level().getRandom(), 0.3f, 0.7f);
                double zm = Mth.randomBetween(entity.level().getRandom(), -0.2f, 0.2f);
                entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(DDItems.SHATTERED_HEAD), xm, ym, zm));
            }
        });

		Messages.registerMessages();
	}
}
