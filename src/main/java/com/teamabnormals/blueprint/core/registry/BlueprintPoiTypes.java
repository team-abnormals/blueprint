package com.teamabnormals.blueprint.core.registry;

import com.teamabnormals.blueprint.common.block.BlueprintBeehiveBlock;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.BlockEntitySubRegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.stream.Stream;

public final class BlueprintPoiTypes {
	public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Blueprint.MOD_ID);

	public static final RegistryObject<PoiType> BEEHIVE = POI_TYPES.register("beehive", () -> new PoiType(Stream.of(BlockEntitySubRegistryHelper.collectBlocks(BlueprintBeehiveBlock.class)).map(block -> block.getStateDefinition().getPossibleStates()).collect(HashSet::new, HashSet::addAll, HashSet::addAll), 0, 1));
}
