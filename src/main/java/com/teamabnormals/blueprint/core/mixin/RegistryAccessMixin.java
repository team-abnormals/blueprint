package com.teamabnormals.blueprint.core.mixin;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.common.world.biome.modification.BiomeModificationManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryReadOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegistryAccess.class)
public final class RegistryAccessMixin {

	//TODO: Try to find a cleaner solution to this
	@SuppressWarnings("unchecked")
	@Inject(at = @At("TAIL"), method = "load")
	private static void trackJsonOps(RegistryAccess registryAccess, RegistryReadOps<?> readOps, CallbackInfo info) {
		if (((DelegatingOpsAccessorMixin<?>) readOps).getDelegate() == JsonOps.INSTANCE) {
			BiomeModificationManager.trackReadOps(registryAccess, (RegistryReadOps<JsonElement>) readOps);
		}
	}

}
