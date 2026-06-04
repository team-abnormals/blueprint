package com.teamabnormals.blueprint.core.mixin;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(BiomeSource.class)
public final class BiomeSourceMixin {
	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void modifyCodec(CallbackInfo info) {
		// When serializing ModdedBiomeSources, redirect to serialize the original biome source
		// Without redirecting, the game may refuse to reload worlds if Blueprint is uninstalled
		// This is caused by a remnant of the Infinite Dimensions April Fools update :)
		// When a deserialized biome source type is missing, Mojang has no safety mechanism
		// The game is simply coded to give up and not even try to refresh worldgen settings
		// In general, datapack worldgen has many issues like this
		// Worlds can still be easily recovered but the manual repair is slightly annoying
		// This mixin gets rid of that worry for ModdedBiomeSource
		setCODEC(BiomeSource.CODEC.xmap(Function.identity(), source -> {
			return source instanceof ModdedBiomeSource modded ? modded.getOriginalSource() : source;
		}));
	}

	@Accessor
	@Mutable
	private static void setCODEC(Codec<BiomeSource> codec) {}
}
