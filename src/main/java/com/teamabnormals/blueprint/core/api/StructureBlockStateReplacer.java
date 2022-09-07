package com.teamabnormals.blueprint.core.api;

import com.teamabnormals.blueprint.core.util.DataUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class StructureBlockStateReplacer {
	private static final Set<StructureFunction> FUNCTIONS = new HashSet<>();
	private static final ThreadLocal<StructureHolder> STRUCTURE_HOLDER = new ThreadLocal<>();

	public static void addReplacement(StructureFunction function) {
		FUNCTIONS.add(function);
	}

	public static void addBasicReplacement(Block input, Block output, ResourceKey<?>... structures) {
		addBasicReplacement(input, output, true, structures);
	}

	public static void addBasicReplacement(Block input, Block output, boolean condition, ResourceKey<?>... structures) {
		addReplacement((level, state, holder) -> {
			Optional<ResourceLocation> optional = level.registryAccess().registry(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).map((it) -> it.getKey(holder.currentStructure));
			if (optional.isPresent() && state.is(input) && condition && DataUtil.matchesKeys(optional.get(), structures)) {
				return output.withPropertiesOf(state);
			}
			return null;
		});
	}

	public static BlockState getReplacementState(ServerLevelAccessor level, BlockState originalState) {
		StructureHolder holder = getCurrentStructureHolder();
		if (holder != null && holder.currentStructure != null)
			for (StructureFunction function : FUNCTIONS) {
				BlockState state = function.replaceBlockState(level, originalState, holder);
				if (state != null)
					return state;
			}

		return originalState;
	}

	private static StructureHolder getCurrentStructureHolder() {
		return STRUCTURE_HOLDER.get();
	}

	public static void setActiveStructure(ConfiguredStructureFeature<?, ?> structure, PiecesContainer components) {
		StructureHolder holder = getCurrentStructureHolder();
		if (holder == null) {
			holder = new StructureHolder();
			STRUCTURE_HOLDER.set(holder);
		}

		holder.currentStructure = structure;
		holder.currentComponents = components == null ? null : components.pieces();
	}

	@FunctionalInterface
	public interface StructureFunction {
		BlockState replaceBlockState(ServerLevelAccessor level, BlockState state, StructureHolder holder);
	}

	public static class StructureHolder {
		public ConfiguredStructureFeature<?, ?> currentStructure;
		public List<StructurePiece> currentComponents;
	}
}