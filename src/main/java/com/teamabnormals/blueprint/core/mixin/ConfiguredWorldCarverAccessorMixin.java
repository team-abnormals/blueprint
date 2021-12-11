package com.teamabnormals.blueprint.core.mixin;

import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConfiguredWorldCarver.class)
public interface ConfiguredWorldCarverAccessorMixin<WC extends CarverConfiguration> {
	@Accessor
	WorldCarver<WC> getWorldCarver();
}
