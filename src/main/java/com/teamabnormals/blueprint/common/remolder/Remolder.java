package com.teamabnormals.blueprint.common.remolder;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.remolder.data.Molding;

import java.util.List;
import java.util.function.Function;

/**
 * Responsible for adding instructions to {@link Molding} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface Remolder {
	Codec<Remolder> VERBOSE_CODEC = RemolderTypes.REGISTRY.dispatchStable(Remolder::codec, Function.identity());
	Codec<Remolder> CODEC = new Codec<>() {
		private final Codec<Remolder> sequenceCodec = this.listOf().xmap(SequenceRemolder::new, remolder -> {
			return remolder instanceof SequenceRemolder(List<Remolder> remolders) ? remolders : List.of(remolder);
		});

		@Override
		public <T> DataResult<Pair<Remolder, T>> decode(DynamicOps<T> ops, T input) {
			return ops.getMapValues(input).result().isPresent() ? VERBOSE_CODEC.decode(ops, input) : this.sequenceCodec.decode(ops, input);
		}

		@Override
		public <T> DataResult<T> encode(Remolder input, DynamicOps<T> ops, T prefix) {
			return input instanceof SequenceRemolder ? this.sequenceCodec.encode(input, ops, prefix) : VERBOSE_CODEC.encode(input, ops, prefix);
		}
	};

	void remold(Molding molding) throws Exception;

	MapCodec<? extends Remolder> codec();
}
