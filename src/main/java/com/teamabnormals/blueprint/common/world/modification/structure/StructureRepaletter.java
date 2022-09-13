package com.teamabnormals.blueprint.common.world.modification.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * The interface used by {@link StructureRepalleterManager} to alter the blocks that make up structures.
 * <p>Use {@link #CODEC} for serializing and deserializing instances of this interface.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepalleterManager
 */
public interface StructureRepaletter {
	Codec<StructureRepaletter> CODEC = StructureRepalleterManager.REPALLETER_SERIALIZERS.dispatchStable(StructureRepaletter::codec, Function.identity());

	/**
	 * Returns a {@link BlockState} instance to replace the given {@link BlockState} instance.
	 * <p>Returning null will allow the next {@link StructureRepaletter} instance to apply its version of this method.</p>
	 *
	 * @param level  A {@link ServerLevelAccessor} instance for the level that the repaletter is applying in.
	 * @param state  A {@link BlockState} instance to potentially replace.
	 * @param random A {@link RandomSource} instance to generate random values.
	 * @return A {@link BlockState} instance to replace the given {@link BlockState} instance.
	 */
	@Nullable
	BlockState getReplacement(ServerLevelAccessor level, BlockState state, RandomSource random);

	/**
	 * Gets the {@link Codec} instance for serializing instances of this interface.
	 *
	 * @return The {@link Codec} instance for serializing instances of this interface.
	 */
	Codec<? extends StructureRepaletter> codec();
}