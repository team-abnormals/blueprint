package com.teamabnormals.blueprint.core.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.core.events.SimpleJsonResourceListenerPreparedEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SimpleJsonResourceReloadListener.class)
public final class SimpleJsonResourceReloadListenerMixin {
	@Shadow
	@Final
	private Gson gson;
	@Shadow
	@Final
	private String directory;

	@Inject(at = @At("RETURN"), method = "prepare")
	private void onPrepared(ResourceManager manager, ProfilerFiller profiler, CallbackInfoReturnable<Map<ResourceLocation, JsonElement>> info) {
		MinecraftForge.EVENT_BUS.post(new SimpleJsonResourceListenerPreparedEvent(this.gson, this.directory, info.getReturnValue()));
	}
}
