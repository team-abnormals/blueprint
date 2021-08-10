package com.minecraftabnormals.abnormals_core.client.screen.shaking;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.config.ACConfig;
import com.minecraftabnormals.abnormals_core.core.mixin.client.ActiveRenderInfoInvokerMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Handles the updating of {@link ShakeSource}s used to shake the screen.
 * <p>Individual {@link ShakeSource}s are updated, and their intensities get added together to get values used for shaking the screen.</p>
 * <p>This class is an enum to make it unable to be extended and to only have one instance ({@link #INSTANCE}).</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see ShakeSource
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, value = Dist.CLIENT)
public enum ScreenShakeHandler {
	INSTANCE;

	private static final Random RANDOM = new Random();
	private final List<ShakeSource> sources = new LinkedList<>();
	private double prevIntensityX, prevIntensityY, prevIntensityZ;
	private double intensityX, intensityY, intensityZ;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			INSTANCE.tick();
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
		INSTANCE.shakeCamera(event);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onPlayerLoggedOut(LoggedOutEvent event) {
		INSTANCE.clear();
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		INSTANCE.clear();
	}

	private static double randomizeIntensity(double intensity) {
		double randomDouble = RANDOM.nextDouble();
		return (1.0D - randomDouble * randomDouble) * (RANDOM.nextInt(2) - 0.5D) * intensity * 2.0D;
	}

	/**
	 * Tries to add a {@link ShakeSource} to the {@link #sources} list.
	 * <p>Use this to add a {@link ShakeSource} to affect the screen shaking.</p>
	 *
	 * @param source A {@link ShakeSource} to add.
	 * @return If the amount of sources doesn't exceed the maximum amount of shakers ({@link ACConfig.ValuesHolder#getMaxScreenShakers()}).
	 */
	public boolean addShakeSource(ShakeSource source) {
		List<ShakeSource> sources = this.sources;
		if (sources.size() >= ACConfig.ValuesHolder.getMaxScreenShakers()) return false;
		sources.add(source);
		return true;
	}

	private void tick() {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.isPaused()) {
			this.prevIntensityX = this.intensityX;
			this.prevIntensityY = this.intensityY;
			this.prevIntensityZ = this.intensityZ;
			List<ShakeSource> sources = this.sources;
			if (sources.isEmpty()) {
				this.intensityX = this.intensityY = this.intensityZ = 0.0F;
			} else {
				Iterator<ShakeSource> sourceIterator = sources.iterator();
				Entity entity = minecraft.cameraEntity;
				Vector3d pos = entity != null ? entity.position() : Vector3d.ZERO;
				double intensityX = 0.0F, intensityY = 0.0F, intensityZ = 0.0F;
				while (sourceIterator.hasNext()) {
					ShakeSource shakingSource = sourceIterator.next();
					shakingSource.tick();
					if (shakingSource.isStopped()) {
						sourceIterator.remove();
					} else {
						Vector3d intensity = shakingSource.getIntensity(pos);
						double newIntensityX = intensityX + intensity.x;
						double maxX = shakingSource.getMaxBuildupX();
						if (newIntensityX <= maxX) {
							intensityX = newIntensityX;
						} else if (maxX > intensityX) {
							intensityX = maxX;
						}
						double newIntensityY = intensityY + intensity.y;
						double maxY = shakingSource.getMaxBuildupY();
						if (newIntensityY <= maxY) {
							intensityY = newIntensityY;
						} else if (maxY > intensityY) {
							intensityY = maxY;
						}
						double newIntensityZ = intensityZ + intensity.z;
						double maxZ = shakingSource.getMaxBuildupZ();
						if (newIntensityZ <= maxZ) {
							intensityZ = newIntensityZ;
						} else if (maxZ > intensityZ) {
							intensityZ = maxZ;
						}
					}
				}
				this.intensityX = intensityX != 0.0F ? randomizeIntensity(intensityX) : 0.0F;
				this.intensityY = intensityY != 0.0F ? randomizeIntensity(intensityY) : 0.0F;
				this.intensityZ = intensityZ != 0.0F ? randomizeIntensity(intensityZ) : 0.0F;
			}
		}
	}

	private void shakeCamera(EntityViewRenderEvent.CameraSetup event) {
		double screenEffectScale = ACConfig.ValuesHolder.getScreenShakeScale();
		if (screenEffectScale > 0.0D) {
			double partialTicks = event.getRenderPartialTicks();
			double x = MathHelper.lerp(partialTicks, this.prevIntensityX, this.intensityX), y = MathHelper.lerp(partialTicks, this.prevIntensityY, this.intensityY), z = MathHelper.lerp(partialTicks, this.prevIntensityZ, this.intensityZ);
			if (x != 0.0F || y != 0.0F || z != 0.0F) {
				((ActiveRenderInfoInvokerMixin) event.getInfo()).callMove(z * screenEffectScale, y * screenEffectScale, x * screenEffectScale);
			}
		}
	}

	private void clear() {
		this.sources.clear();
		this.prevIntensityX = this.prevIntensityY = this.prevIntensityZ = this.intensityX = this.intensityY = this.intensityZ = 0.0F;
	}
}
