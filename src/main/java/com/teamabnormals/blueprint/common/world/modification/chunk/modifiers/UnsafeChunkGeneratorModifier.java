package com.teamabnormals.blueprint.common.world.modification.chunk.modifiers;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * A sealed {@link IChunkGeneratorModifier} implementation permitted only for specific classes because it gives access to the Unsafe.
 *
 * @param <C> The type of config object for this modifier.
 * @author SmellyModder (Luke Tonon)
 * @see IChunkGeneratorModifier
 */
public sealed abstract class UnsafeChunkGeneratorModifier<C> implements IChunkGeneratorModifier<C> permits SurfaceRuleModifier {
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
