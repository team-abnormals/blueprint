package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.api.BlueprintRabbitVariants;
import com.teamabnormals.blueprint.core.api.BlueprintRabbitVariants.BlueprintRabbitGroupData;
import com.teamabnormals.blueprint.core.api.BlueprintRabbitVariants.BlueprintRabbitVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Rabbit.class)
public abstract class RabbitMixin extends Animal implements VariantHolder<Rabbit.Variant> {

	protected RabbitMixin(EntityType<? extends Animal> animal, Level level) {
		super(animal, level);
	}

	@Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
	private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		tag.putInt("RabbitType", this.entityData.get(Rabbit.DATA_TYPE_ID));
	}

	@Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
	private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		this.getEntityData().set(Rabbit.DATA_TYPE_ID, tag.getInt("RabbitType"));
	}

	@Inject(at = @At("RETURN"), method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Rabbit;", cancellable = true)
	private void getBreedOffspring(ServerLevel level, AgeableMob ageableMob, CallbackInfoReturnable<Rabbit> cir) {
		Rabbit rabbit = cir.getReturnValue();

		int id = 0;
		if (this.random.nextInt(20) != 0) {
			if (this.random.nextBoolean() && ageableMob instanceof Rabbit partner) {
				id = partner.getEntityData().get(Rabbit.DATA_TYPE_ID);
			} else {
				id = this.getEntityData().get(Rabbit.DATA_TYPE_ID);
			}
		} else {
			boolean foundVariant = false;
			for (BlueprintRabbitVariant newVariant : BlueprintRabbitVariants.values()) {
				if (newVariant.test(level, this.blockPosition())) {
					id = newVariant.id();
					foundVariant = true;
					break;
				}
			}

			if (!foundVariant) {
				id = Rabbit.getRandomRabbitVariant(level, this.blockPosition()).id();
			}
		}

		rabbit.getEntityData().set(Rabbit.DATA_TYPE_ID, id);
		cir.setReturnValue(rabbit);
	}


	@Inject(at = @At("RETURN"), method = "finalizeSpawn", cancellable = true)
	private void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnData, CompoundTag tag, CallbackInfoReturnable<SpawnGroupData> cir) {
		int id = this.getVariant().id();

		for (BlueprintRabbitVariant newVariant : BlueprintRabbitVariants.values()) {
			if (newVariant.test(level, this.blockPosition())) {
				id = newVariant.id();
				break;
			}
		}

		if (spawnData instanceof BlueprintRabbitGroupData groupData) {
			id = groupData.variant;
		} else {
			spawnData = new BlueprintRabbitGroupData(id);
		}

		this.getEntityData().set(Rabbit.DATA_TYPE_ID, id);
		cir.setReturnValue(super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag));
	}
}
