package com.teamabnormals.abnormals_core.common.entity.ai;

import java.util.EnumSet;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * @author SmellyModder(Luke Tonon)
 */
public class PredicateAttackGoal<T extends LivingEntity> extends TargetGoal {
	private final Predicate<MobEntity> canOwnerTarget;
	private final Class<T> targetClass;
	private final int targetChance;
	private LivingEntity nearestTarget;
	private EntityPredicate targetEntitySelector;

	public PredicateAttackGoal(MobEntity goalOwnerIn, Class<T> targetClassIn, boolean checkSight, Predicate<MobEntity> canOwnerTarget) {
		this(goalOwnerIn, targetClassIn, checkSight, false, canOwnerTarget);
	}

	public PredicateAttackGoal(MobEntity goalOwnerIn, Class<T> targetClassIn, boolean checkSight, boolean nearbyOnlyIn, Predicate<MobEntity> canOwnerTarget) {
		this(goalOwnerIn, targetClassIn, 10, checkSight, nearbyOnlyIn, null, canOwnerTarget);
	}

	public PredicateAttackGoal(MobEntity goalOwnerIn, Class<T> targetClassIn, int targetChanceIn, boolean checkSight, boolean nearbyOnlyIn, @Nullable Predicate<LivingEntity> targetPredicate, Predicate<MobEntity> canOwnerTarget) {
		super(goalOwnerIn, checkSight, nearbyOnlyIn);
		this.canOwnerTarget = canOwnerTarget;
		this.targetClass = targetClassIn;
		this.targetChance = targetChanceIn;
		this.targetEntitySelector = (new EntityPredicate()).setDistance(this.getTargetDistance()).setCustomPredicate(targetPredicate);
		this.setMutexFlags(EnumSet.of(Flag.TARGET));
	}

	public boolean shouldExecute() {
		if (!this.canOwnerTarget.test(this.goalOwner) || (this.targetChance > 0 && this.goalOwner.getRNG().nextInt(this.targetChance) != 0)) {
			return false;
		} else {
			this.findNearestTarget();
			return this.nearestTarget != null;
		}
	}

	protected AxisAlignedBB getTargetableArea(double targetDistance) {
		return this.goalOwner.getBoundingBox().grow(targetDistance, 4.0D, targetDistance);
	}

	protected void findNearestTarget() {
		if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
			this.nearestTarget = this.goalOwner.world.<T>func_225318_b(this.targetClass, this.targetEntitySelector, this.goalOwner, this.goalOwner.getPosX(), this.goalOwner.getPosYEye(), this.goalOwner.getPosZ(), this.getTargetableArea(this.getTargetDistance()));
		} else {
			this.nearestTarget = this.goalOwner.world.getClosestPlayer(this.targetEntitySelector, this.goalOwner, this.goalOwner.getPosX(), this.goalOwner.getPosYEye(), this.goalOwner.getPosZ());
		}
	}

	public void startExecuting() {
		this.goalOwner.setAttackTarget(this.nearestTarget);
		super.startExecuting();
	}
}