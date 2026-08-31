package com.teamabnormals.blueprint.common.codec;

import com.mojang.serialization.*;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Similar to the {@link net.minecraft.util.ExtraCodecs} class, but for even more extra codecs!
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintExtraCodecs {
	/**
	 * Creates a lazy-loaded {@link MapCodec} instance.
	 *
	 * @param codec The supplier to get the {@link MapCodec} instance from.
	 * @return A lazy {@link MapCodec} instance.
	 */
	public static <A> MapCodec<A> lazyMapCodec(Supplier<MapCodec<A>> codec) {
		Lazy<MapCodec<A>> lazyMapCodec = Lazy.of(codec);
		return new MapCodec<>() {
			@Override
			public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				return lazyMapCodec.get().encode(input, ops, prefix);
			}

			@Override
			public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
				return lazyMapCodec.get().decode(ops, input);
			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return lazyMapCodec.get().keys(ops);
			}
		};
	}
}
