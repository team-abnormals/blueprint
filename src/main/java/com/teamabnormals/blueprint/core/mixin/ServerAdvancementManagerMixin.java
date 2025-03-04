package com.teamabnormals.blueprint.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModificationManager;
import com.teamabnormals.blueprint.common.advancement.modification.BlueprintAdvancementBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.neoforged.bus.api.EventPriority;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerAdvancementManager.class)
public final class ServerAdvancementManagerMixin {
	@WrapOperation(method = "*(Lnet/minecraft/resources/RegistryOps;Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/resources/ResourceLocation;Lcom/google/gson/JsonElement;)V", at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/advancements/AdvancementHolder;"))
	private static AdvancementHolder modifyAdvancement(ResourceLocation id, Advancement advancement, Operation<AdvancementHolder> operation) {
		var holder = operation.call(id, advancement);
		if (AdvancementModificationManager.INSTANCE != null) {
			BlueprintAdvancementBuilder mutableAdvancement = new BlueprintAdvancementBuilder();
			advancement.parent().ifPresent(mutableAdvancement::parent);
			advancement.display().ifPresent(mutableAdvancement::display);
			mutableAdvancement.rewards(advancement.rewards());
			advancement.criteria().forEach(mutableAdvancement::addCriterion);
			mutableAdvancement.requirements(advancement.requirements());
			if (advancement.sendsTelemetryEvent()) mutableAdvancement.sendsTelemetryEvent();
			for (EventPriority priority : EventPriority.values()) {
				AdvancementModificationManager.INSTANCE.applyModifiers(priority, id, mutableAdvancement);
			}
			return mutableAdvancement.build(id);
		}
		return new AdvancementHolder(holder.id(), holder.value());
	}
}
