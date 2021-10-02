package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

//TODO: Enable once Forge fixes the mixin bug with ModifyArgs
@Mixin(MultiNoiseBiomeSource.Preset.class)
public final class MultiNoiseBiomeSourcePresetMixin {

	//Not sure why @ModifyArg wouldn't work?
	@ModifyArgs(method = "*(Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource$Preset;Lnet/minecraft/core/Registry;Ljava/lang/Long;)Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;<init>(JLjava/util/List;Ljava/util/Optional;)V"))
	private static void addModdedNetherBiomes(Args args, MultiNoiseBiomeSource.Preset preset, Registry<Biome> lookupRegistry, Long seed) {
		args.set(1, BiomeUtil.getModifiedNetherBiomes(args.get(1), lookupRegistry));
	}

}
