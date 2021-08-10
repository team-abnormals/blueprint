package core;

import com.minecraftabnormals.abnormals_core.client.screen.shaking.EntityShakeSource;
import com.minecraftabnormals.abnormals_core.client.screen.shaking.ScreenShakeHandler;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import com.minecraftabnormals.abnormals_core.core.util.TradeUtil;
import core.registry.TestItems;
import core.registry.TestTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = ACTest.MOD_ID)
public final class TestEvents {

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		Entity entity = event.getTarget();
		if (entity instanceof CowEntity || entity instanceof PlayerEntity) {
			if (!entity.level.isClientSide) {
				PlayerEntity player = event.getPlayer();
				if (player instanceof ServerPlayerEntity) {
					TestTriggers.EMPTY_TEST.trigger((ServerPlayerEntity) player);
				}
				TrackedDataManager.INSTANCE.setValue(entity, ACTest.TEST_TRACKED_DATA, true);
			} else {
				ScreenShakeHandler.INSTANCE.addShakeSource(new EntityShakeSource(entity, 100, 0.1F, 0.1F, 0.02F, 0.2F, 0.2F, 0.04F, 0.98F, 0.98F, 0.99F));
			}
		}
	}

	@SubscribeEvent
	public static void onLivingTick(LivingEvent.LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (entity.level.isClientSide && (entity instanceof CowEntity || entity instanceof PlayerEntity) && TrackedDataManager.INSTANCE.getValue(entity, ACTest.TEST_TRACKED_DATA)) {
			Random rand = entity.getRandom();
			for (int i = 0; i < 2; ++i) {
				entity.level.addParticle(ParticleTypes.PORTAL, entity.getRandomX(0.5D), entity.getRandomY() - 0.25D, entity.getRandomZ(0.5D), (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
			}
		}
	}

	@SubscribeEvent
	public static void onTrades(VillagerTradesEvent event) {
		TradeUtil.addVillagerTrades(event, 1,
				new TradeUtil.AbnormalsTrade(TestItems.ITEM.get(), 5, 2, 6, 15),
				new TradeUtil.AbnormalsTrade(TestItems.ITEM.get(), 5, 2, 6, 55),
				new TradeUtil.AbnormalsTrade(TestItems.ITEM.get(), 5, 9, 6, 75)
		);
		TradeUtil.addVillagerTrades(event, VillagerProfession.CARTOGRAPHER, 2, new TradeUtil.AbnormalsTrade(TestItems.BOAT.get(), 5, 10, 6, 100));
	}

	@SubscribeEvent
	public static void onWandererTrades(WandererTradesEvent event) {
		TradeUtil.addWandererTrades(event, new TradeUtil.AbnormalsTrade(Items.EMERALD_ORE, 5, 2, 6, 15));
		TradeUtil.addRareWandererTrades(event,
				new TradeUtil.AbnormalsTrade(TestItems.COW_SPAWN_EGG.get(), 5, 2, 6, 15),
				new TradeUtil.AbnormalsTrade(TestItems.COW_SPAWN_EGG.get(), 5, 2, 6, 55)
		);
	}

}
