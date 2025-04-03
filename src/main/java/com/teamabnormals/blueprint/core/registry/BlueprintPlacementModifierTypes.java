package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.common.levelgen.placement.BetterNoiseBasedCountPlacement;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BlueprintPlacementModifierTypes {
	public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, Blueprint.MOD_ID);

	public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<BetterNoiseBasedCountPlacement>> BETTER_NOISE_BASED_COUNT = PLACEMENT_MODIFIER_TYPES.register("better_noise_based_count", () -> () -> BetterNoiseBasedCountPlacement.CODEC);
}