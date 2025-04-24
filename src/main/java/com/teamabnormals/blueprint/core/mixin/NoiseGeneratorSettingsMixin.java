package com.teamabnormals.blueprint.core.mixin;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NoiseGeneratorSettings.class)
public interface NoiseGeneratorSettingsMixin {
	@Accessor
	@Mutable
		// Curse technique
	void setSurfaceRule(SurfaceRules.RuleSource surfaceRule);
}
