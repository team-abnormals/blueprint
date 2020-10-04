package core;

import com.teamabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = ACTest.MOD_ID)
public final class TestEvents {

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		Entity entity = event.getTarget();
		if (!entity.world.isRemote && (entity instanceof CowEntity || entity instanceof PlayerEntity)) {
			TrackedDataManager.INSTANCE.setValue(entity, ACTest.TEST_TRACKED_DATA, true);
		}
	}

	@SubscribeEvent
	public static void onLivingTick(LivingEvent.LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (entity.world.isRemote && (entity instanceof CowEntity || entity instanceof PlayerEntity) && TrackedDataManager.INSTANCE.getValue(entity, ACTest.TEST_TRACKED_DATA)) {
			Random rand = entity.getRNG();
			for (int i = 0; i < 2; ++i) {
				entity.world.addParticle(ParticleTypes.PORTAL, entity.getPosXRandom(0.5D), entity.getPosYRandom() - 0.25D, entity.getPosZRandom(0.5D), (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
			}
		}
	}

}
