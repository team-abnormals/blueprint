package com.teamabnormals.blueprint.core.registry;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.api.conditions.BlueprintAndCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Codec registry class for Blueprint's {@link ICondition} implementations.
 */
public final class BlueprintConditionCodecs {
	public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, Blueprint.MOD_ID);

	public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<BlueprintAndCondition>> AND_CONDITION = CONDITION_CODECS.register("and", () -> BlueprintAndCondition.CODEC);
}
