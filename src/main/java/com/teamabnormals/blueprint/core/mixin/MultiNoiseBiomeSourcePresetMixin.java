package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.util.BiomeUtil;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MultiNoiseBiomeSource.Preset.class)
public final class MultiNoiseBiomeSourcePresetMixin {

	//Not sure why @ModifyArg wouldn't work?
	@ModifyArgs(method = "*(Lnet/minecraft/core/Registry;)Lnet/minecraft/world/level/biome/Climate$ParameterList;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Climate$ParameterList;<init>(Ljava/util/List;)V"))
	private static void addModdedNetherBiomes(Args args, Registry<Biome> lookupRegistry) {
		args.set(0, BiomeUtil.getModifiedNetherBiomes(args.get(0), lookupRegistry));
	}

}
