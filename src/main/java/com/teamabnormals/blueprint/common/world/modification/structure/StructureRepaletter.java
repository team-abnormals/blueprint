package com.teamabnormals.blueprint.common.world.modification.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * The interface used by {@link StructureRepaletterManager} to alter the blocks that make up structures.
 * <p>Use {@link #CODEC} for serializing and deserializing instances of this interface.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletterManager
 */
public interface StructureRepaletter {
	Codec<StructureRepaletter> CODEC = StructureRepaletterManager.REPALLETER_SERIALIZERS.dispatchStable(StructureRepaletter::codec, Function.identity());

	/**
	 * Creates a {@link Replacer} instance based on initial structure context.
	 *
	 * @param context A {@link StructureModificationContext} instance for individual structure context.
	 * @return A {@link Replacer} instance based on initial structure context.
	 */
	@Nullable
	Replacer createReplacer(StructureModificationContext context);

	/**
	 * Gets the {@link MapCodec} instance for serializing instances of this interface.
	 *
	 * @return The {@link MapCodec} instance for serializing instances of this interface.
	 */
	MapCodec<? extends StructureRepaletter> codec();

	/**
	 * Interface used to replace blocks in structures.
	 *
	 * @author SmellyModder (Luke Tonon)
	 * @see StructureRepaletterManager
	 */
	interface Replacer {
		Codec<Replacer> CODEC = StructureRepaletterManager.REPLACER_SERIALIZERS.dispatchStable(Replacer::savedTagCodec, Function.identity());

		/**
		 * Returns a {@link BlockState} instance to replace the given {@link BlockState} instance.
		 * <p>Returning null will allow the next replacer to apply its version of this method.</p>
		 *
		 * @param level  A {@link ServerLevelAccessor} instance for the level that the repaletter is applying in.
		 * @param state  A {@link BlockState} instance to potentially replace.
		 * @param random A {@link RandomSource} instance to generate random values.
		 * @return A {@link BlockState} instance to replace the given {@link BlockState} instance.
		 */
		@Nullable
		BlockState getReplacement(ServerLevelAccessor level, BlockState state, RandomSource random);

		/**
		 * Gets the {@link MapCodec} instance for serializing instances of this interface.
		 *
		 * @return The {@link MapCodec} instance for serializing instances of this interface.
		 */
		MapCodec<? extends Replacer> savedTagCodec();
	}

	/**
	 * Interface used to test initial structure contextual data.
	 *
	 * @author SmellyModder (Luke Tonon)
	 * @see StructureRepaletterManager
	 */
	interface Condition {
		Codec<Condition> CODEC = StructureRepaletterManager.CONDITION_SERIALIZERS.dispatchStable(Condition::codec, Function.identity());

		/**
		 * Tests to replace based on initial structure context.
		 *
		 * @param context A {@link StructureModificationContext} instance for individual structure context.
		 * @return Whether to replace based on initial structure context.
		 */
		boolean test(StructureModificationContext context);

		/**
		 * Gets the {@link MapCodec} instance for serializing instances of this interface.
		 *
		 * @return The {@link MapCodec} instance for serializing instances of this interface.
		 */
		MapCodec<? extends Condition> codec();
	}
}