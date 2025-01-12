package com.teamabnormals.blueprint.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModificationManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.neoforged.bus.api.EventPriority;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerAdvancementManager.class)
public final class ServerAdvancementManagerMixin {
	@WrapOperation(method = "*(Lnet/minecraft/resources/RegistryOps;Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/resources/ResourceLocation;Lcom/google/gson/JsonElement;)V", at = @At(value = "INVOKE", target = "net/minecraft/advancements/AdvancementHolder;<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/advancements/Advancement;)V"))
	private static AdvancementHolder modifyAdvancement(ResourceLocation id, Advancement advancement, Operation<AdvancementHolder> operation) {
		var holder = operation.call(id, advancement);
		if (AdvancementModificationManager.INSTANCE != null) {
			Advancement.Builder builder = new Advancement.Builder();
			advancement.parent().ifPresent(builder::parent);
			advancement.display().ifPresent(builder::display);
			builder.rewards(advancement.rewards());
			advancement.criteria().forEach(builder::addCriterion);
			builder.requirements(advancement.requirements());
			if (advancement.sendsTelemetryEvent()) builder.sendsTelemetryEvent();
			for (EventPriority priority : EventPriority.values()) {
				AdvancementModificationManager.INSTANCE.applyModifiers(priority, id, builder);
			}
			return builder.build(id);
		}
		return new AdvancementHolder(holder.id(), holder.value());
	}
}
