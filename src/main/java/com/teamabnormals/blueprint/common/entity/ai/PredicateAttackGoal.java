package com.teamabnormals.blueprint.common.entity.ai;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

/**
 * A {@link TargetGoal} extension that allows for conditioning the targeting of the owner.
 *
 * @author SmellyModder(Luke Tonon)
 */
public class PredicateAttackGoal<T extends LivingEntity> extends TargetGoal {
	private final Predicate<Mob> canOwnerTarget;
	private final Class<T> targetClass;
	private final int targetChance;
	private LivingEntity nearestTarget;
	private final TargetingConditions targetEntitySelector;

	public PredicateAttackGoal(Mob goalOwnerIn, Class<T> targetClassIn, boolean checkSight, Predicate<Mob> canOwnerTarget) {
		this(goalOwnerIn, targetClassIn, checkSight, false, canOwnerTarget);
	}

	public PredicateAttackGoal(Mob goalOwnerIn, Class<T> targetClassIn, boolean checkSight, boolean nearbyOnlyIn, Predicate<Mob> canOwnerTarget) {
		this(goalOwnerIn, targetClassIn, 10, checkSight, nearbyOnlyIn, null, canOwnerTarget);
	}

	public PredicateAttackGoal(Mob goalOwnerIn, Class<T> targetClassIn, int targetChanceIn, boolean checkSight, boolean nearbyOnlyIn, @Nullable Predicate<LivingEntity> targetPredicate, Predicate<Mob> canOwnerTarget) {
		super(goalOwnerIn, checkSight, nearbyOnlyIn);
		this.canOwnerTarget = canOwnerTarget;
		this.targetClass = targetClassIn;
		this.targetChance = targetChanceIn;
		this.targetEntitySelector = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(targetPredicate);
		this.setFlags(EnumSet.of(Flag.TARGET));
	}

	public boolean canUse() {
		if (!this.canOwnerTarget.test(this.mob) || (this.targetChance > 0 && this.mob.getRandom().nextInt(this.targetChance) != 0)) {
			return false;
		} else {
			this.findNearestTarget();
			return this.nearestTarget != null;
		}
	}

	protected AABB getTargetableArea(double targetDistance) {
		return this.mob.getBoundingBox().inflate(targetDistance, 4.0D, targetDistance);
	}

	protected void findNearestTarget() {
		if (this.targetClass != Player.class && this.targetClass != ServerPlayer.class) {
			this.nearestTarget = this.mob.level.getNearestEntity(this.targetClass, this.targetEntitySelector, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.getTargetableArea(this.getFollowDistance()));
		} else {
			this.nearestTarget = this.mob.level.getNearestPlayer(this.targetEntitySelector, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
		}
	}

	public void start() {
		this.mob.setTarget(this.nearestTarget);
		super.start();
	}
}