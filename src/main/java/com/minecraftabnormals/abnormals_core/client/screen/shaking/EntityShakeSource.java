package com.minecraftabnormals.abnormals_core.client.screen.shaking;

import net.minecraft.entity.Entity;

/**
 * An extension of {@link PositionedShakeSource} where the source belongs to an entity.
 * <p>The position of the source is updated to the entity's position in {@link #tick()}</p>
 * <p>If the entity is not alive, the source will decay, eventually stopping after 20 ticks.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see PositionedShakeSource
 * @see ScreenShakeHandler
 */
public class EntityShakeSource extends PositionedShakeSource {
	private final Entity entity;
	private int deadTicks;

	public EntityShakeSource(Entity entity, int duration, double intensityX, double intensityY, double intensityZ, double maxBuildupX, double maxBuildupY, double maxBuildupZ, double decayX, double decayY, double decayZ) {
		super(duration, intensityX, intensityY, intensityZ, maxBuildupX, maxBuildupY, maxBuildupZ, decayX, decayY, decayZ, entity.getX(), entity.getY(), entity.getZ());
		this.entity = entity;
	}

	public EntityShakeSource(Entity entity, int duration, double intensity, double maxBuildup, double decay) {
		super(duration, intensity, maxBuildup, decay, entity.getX(), entity.getY(), entity.getZ());
		this.entity = entity;
	}

	@Override
	public void tick() {
		Entity entity = this.entity;
		this.x = entity.getX();
		this.y = entity.getY();
		this.z = entity.getZ();
		if (!entity.isAlive()) {
			this.deadTicks++;
			this.decayX *= 0.925F;
			this.decayY *= 0.925F;
			this.decayZ *= 0.925F;
		}
		super.tick();
	}

	@Override
	public boolean isStopped() {
		return super.isStopped() || this.deadTicks >= 20;
	}
}
