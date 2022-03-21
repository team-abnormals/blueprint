package com.teamabnormals.blueprint.common.world.modification.chunk.modifiers;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * A sealed {@link ChunkGeneratorModifier} implementation permitted only for specific classes because it gives access to the Unsafe.
 *
 * @author SmellyModder (Luke Tonon)
 * @see ChunkGeneratorModifier
 */
public sealed abstract class UnsafeChunkGeneratorModifier<M extends ChunkGeneratorModifier<M>> implements ChunkGeneratorModifier<M> permits SurfaceRuleModifier {
	protected static final Unsafe UNSAFE;

	static {
		try {
			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			UNSAFE = (Unsafe) theUnsafeField.get(null);
		} catch (Exception exception) {
			throw new RuntimeException("Failed to get the instance of Unsafe!");
		}
	}
}
