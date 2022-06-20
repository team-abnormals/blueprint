package core;

import com.teamabnormals.blueprint.client.screen.shaking.EmanatingShakeSource;
import com.teamabnormals.blueprint.client.screen.shaking.ScreenShakeHandler;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.util.TradeUtil;
import com.teamabnormals.blueprint.core.util.TradeUtil.BlueprintTrade;
import core.registry.TestItems;
import core.registry.TestTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID)
public final class TestEvents {

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		Entity entity = event.getTarget();
		if (entity instanceof Cow || entity instanceof Player) {
			if (!entity.level.isClientSide) {
				Player player = event.getPlayer();
				if (player instanceof ServerPlayer) {
					TestTriggers.EMPTY_TEST.trigger((ServerPlayer) player);
				}
				TrackedDataManager.INSTANCE.setValue(entity, BlueprintTest.TEST_TRACKED_DATA, true);
			} else {
				ScreenShakeHandler.INSTANCE.addShakeSource(new EmanatingShakeSource(entity, 100, 0.1F, 0.1F, 0.02F, 0.2F, 0.2F, 0.04F, 0.98F, 0.98F, 0.99F));
			}
		}
	}

	@SubscribeEvent
	public static void onLivingTick(LivingEvent.LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (entity.level.isClientSide && (entity instanceof Cow || entity instanceof Player) && TrackedDataManager.INSTANCE.getValue(entity, BlueprintTest.TEST_TRACKED_DATA)) {
			RandomSource rand = entity.getRandom();
			for (int i = 0; i < 2; ++i) {
				entity.level.addParticle(ParticleTypes.PORTAL, entity.getRandomX(0.5D), entity.getRandomY() - 0.25D, entity.getRandomZ(0.5D), (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
			}
		}
	}

	@SubscribeEvent
	public static void onTrades(VillagerTradesEvent event) {
		TradeUtil.addVillagerTrades(event, 1,
				new BlueprintTrade(TestItems.ITEM.get(), 5, 2, 6, 15),
				new BlueprintTrade(TestItems.ITEM.get(), 5, 2, 6, 55),
				new BlueprintTrade(TestItems.ITEM.get(), 5, 9, 6, 75)
		);
		TradeUtil.addVillagerTrades(event, VillagerProfession.CARTOGRAPHER, 2, new BlueprintTrade(TestItems.BOAT.getFirst().get(), 5, 10, 6, 100));
	}

	@SubscribeEvent
	public static void onWandererTrades(WandererTradesEvent event) {
		TradeUtil.addWandererTrades(event, new BlueprintTrade(Items.EMERALD_ORE, 5, 2, 6, 15));
		TradeUtil.addRareWandererTrades(event,
				new BlueprintTrade(TestItems.COW_SPAWN_EGG.get(), 5, 2, 6, 15),
				new BlueprintTrade(TestItems.COW_SPAWN_EGG.get(), 5, 2, 6, 55)
		);
	}

}
