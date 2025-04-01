package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.api.EggLayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {

	protected ZombieMixin(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/ServerLevelAccessor;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"), method = "finalizeSpawn")
	public void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
		List<Chicken> chickens = level.getEntitiesOfClass(Chicken.class, this.getBoundingBox().inflate(5.0, 3.0, 5.0), EntitySelector.ENTITY_NOT_BEING_RIDDEN);
		if (chickens.isEmpty()) {
			List<Animal> birds = level.getEntitiesOfClass(Animal.class, this.getBoundingBox().inflate(5.0, 3.0, 5.0), EntitySelector.ENTITY_NOT_BEING_RIDDEN.and(entity -> entity instanceof EggLayer));
			if (!birds.isEmpty()) {
				Animal bird = birds.getFirst();
				((EggLayer) bird).setBirdJockey(true);
				this.startRiding(bird);
			}
		}
	}
}