package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.loot.modification.LootModificationManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.eventbus.api.EventPriority;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LootDataManager.class)
public final class LootDataManagerMixin {
	@SuppressWarnings("unchecked")
	@Inject(method = "apply", at = @At("HEAD"))
	private void modifyLootTables(Map<LootDataType<?>, Map<ResourceLocation, ?>> map, CallbackInfo info) {
		LootModificationManager lootModificationManager = LootModificationManager.getInstance();
		if (lootModificationManager != null) {
			Map<ResourceLocation, LootTable> lootTableMap = (Map<ResourceLocation, LootTable>) map.get(LootDataType.TABLE);
			lootTableMap.forEach((location, lootTable) -> {
				for (EventPriority priority : EventPriority.values()) lootModificationManager.applyModifiers(priority, location, lootTable);
			});
		}
	}
}
