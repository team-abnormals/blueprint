package com.minecraftabnormals.abnormals_core.core.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.minecraftabnormals.abnormals_core.core.events.AdvancementBuildingEvent;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(Advancement.Builder.class)
public final class AdvancementBuilderMixin {

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Inject(at = @At(value = "NEW", target = "net/minecraft/advancements/Advancement$Builder", shift = At.Shift.BEFORE), method = "deserialize", locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private static void modifyBuilder(JsonObject json, ConditionArrayParser conditionParser, CallbackInfoReturnable<Advancement.Builder> info, ResourceLocation resourcelocation, DisplayInfo displayinfo, AdvancementRewards advancementrewards, Map map, JsonArray jsonarray, String[][] astring) {
		Advancement.Builder builder = new Advancement.Builder(resourcelocation, displayinfo, advancementrewards, map, astring);
		AdvancementBuildingEvent.onBuildingAdvancement(builder, conditionParser.func_234049_a_());
		info.setReturnValue(builder);
	}

}
