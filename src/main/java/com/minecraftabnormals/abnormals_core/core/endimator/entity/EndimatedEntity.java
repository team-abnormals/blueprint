package com.minecraftabnormals.abnormals_core.core.endimator.entity;

import com.minecraftabnormals.abnormals_core.core.endimator.Endimation;
import com.minecraftabnormals.abnormals_core.core.util.NetworkUtil;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Implementation class of {@link IEndimatedEntity} for Endimated entities.
 * @author - SmellyModder(Luke Tonon)
 */
public abstract class EndimatedEntity extends CreatureEntity implements IEndimatedEntity {
	private Endimation endimation = BLANK_ANIMATION;
	private int animationTick;
	
	public EndimatedEntity(EntityType<? extends CreatureEntity> type, World world) {
		super(type, world);
	}
	
	@Override
	public void tick() {
		super.tick();
		this.endimateTick();
		
		if (this.getHealth() <= 0.0F) {
			Endimation deathEndimation = this.getDeathAnimation();
			if (deathEndimation != null) {
				if (this.isEffectiveAi() && !this.isEndimationPlaying(deathEndimation)) {
					NetworkUtil.setPlayingAnimationMessage(this, deathEndimation);
				}
				this.onEndimatedDeathUpdate(deathEndimation.getAnimationTickDuration());
			} else {
				this.onEndimatedDeathUpdate(20);
			}
		}
	}
	
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!this.isWorldRemote() && this.getHurtAnimation() != null && this.isNoEndimationPlaying()) {
			NetworkUtil.setPlayingAnimationMessage(this, this.getHurtAnimation());
		}
		return super.hurt(source, amount);
	}
	
	@Override
	protected void tickDeath() {}
	
	private void onEndimatedDeathUpdate(int deathAnimationDuration) {
		if (this.deathTime++ >= deathAnimationDuration - 1) {
			this.remove();
			this.addDeathEffects();
		}
	}
	
	/**
	 * Adds the death effects for this entity
	 * Gets called in {@link #onEndimatedDeathUpdate(int)}
	 * Should be overridden if you wish to change the death effects of this entity, default is Vanilla poof particles.
	 */
	protected void addDeathEffects() {
		for (int i = 0; i < 20; ++i) {
			this.level.addParticle(ParticleTypes.POOF, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
		}
	}
	
	@Override
	public void setPlayingEndimation(Endimation endimationToPlay) {
		this.onEndimationEnd(this.endimation);
		this.endimation = endimationToPlay;
		this.setAnimationTick(0);
	}

	@Override
	public Endimation getPlayingEndimation() {
		return this.endimation;
	}
	
	/**
	 * @param endimation - The endimation to check
	 * @return - Is the endimation playing
	 */
	public boolean isEndimationPlaying(Endimation endimation) {
		return this.getPlayingEndimation() == endimation;
	}
	
	/**
	 * @return - Is the world remote; if true: client
	 */
	public boolean isWorldRemote() {
		return this.getCommandSenderWorld().isClientSide;
	}
	
	/**
	 * @return - The progress; measured in ticks, of the current playing animation
	 */
	@Override
	public int getAnimationTick() {
		return this.animationTick;
	}
	
	/**
	 * Sets the progress of the current playing animation
	 * @param animationTick - Progress; measured in ticks
	 */
	@Override
	public void setAnimationTick(int animationTick) {
		this.animationTick = animationTick;
	}
	
	/**
	 * Resets the current animation to a blank one
	 */
	public void resetEndimation() {
		this.setPlayingEndimation(BLANK_ANIMATION);
	}
	
	@Nullable
	public Endimation getHurtAnimation() {
		return null;
	}
	
	@Nullable
	public Endimation getDeathAnimation() {
		return null;
	}
	
	/**
	 * Used in movement controllers to get the distance between the entity's desired path location and its current position
	 * @param pathX - x location of the path
	 * @param pathY - y location of the path
	 * @param pathZ - z location of the path
	 * @return - A vector containing the mid-position of the entity's path end location and its current location
	 */
	public Vector3d getMoveControllerPathDistance(double pathX, double pathY, double pathZ) {
		return new Vector3d(pathX - this.getX(), pathY - this.getY(), pathY - this.getY());
	}
	
	/**
	 * Used for rotationYaw in movement controllers
	 * @param Vector3d - The distance vector
	 * @return - A vector that gets the target angle for a path's distance
	 */
	public float getTargetAngleForPathDistance(Vector3d Vector3d) {
		return (float) (MathHelper.atan2(Vector3d.z, Vector3d.x) * (double) (180F / (float) Math.PI)) - 90F;
	}
}