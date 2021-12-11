package com.teamabnormals.blueprint.core.mixin;

import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(BiomeSpecialEffects.class)
public interface BiomeSpecialEffectsInvokerMixin {
	@Invoker(value = "<init>")
	static BiomeSpecialEffects init(int p_47941_, int p_47942_, int p_47943_, int p_47944_, Optional<Integer> p_47945_, Optional<Integer> p_47946_, BiomeSpecialEffects.GrassColorModifier p_47947_, Optional<AmbientParticleSettings> p_47948_, Optional<SoundEvent> p_47949_, Optional<AmbientMoodSettings> p_47950_, Optional<AmbientAdditionsSettings> p_47951_, Optional<Music> p_47952_) {
		return null;
	}
}
