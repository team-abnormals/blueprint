package com.teamabnormals.blueprint.common.remolder;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.remolder.data.Molding;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A {@link Remolder} implementation that runs multiple remolders in sequential order.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record SequenceRemolder(List<Remolder> remolders) implements Remolder {
	// Remolder.CODEC uses this CODEC, so we have to delay the reference
	public static final MapCodec<SequenceRemolder> CODEC = new Codec<List<Remolder>>() {
		@Override
		public <T> DataResult<T> encode(List<Remolder> input, DynamicOps<T> ops, T prefix) {
			return ExtraCodecs.nonEmptyList(Remolder.CODEC.listOf()).encode(input, ops, prefix);
		}

		@Override
		public <T> DataResult<Pair<List<Remolder>, T>> decode(DynamicOps<T> ops, T input) {
			return ExtraCodecs.nonEmptyList(Remolder.CODEC.listOf()).decode(ops, input);
		}
	}.fieldOf("remolders").xmap(SequenceRemolder::new, SequenceRemolder::remolders);

	@Override
	public void remold(Molding molding) throws Exception {
		for (Remolder remolder : this.remolders) {
			remolder.remold(molding);
		}
	}

	@Override
	public MapCodec<? extends Remolder> codec() {
		return CODEC;
	}

	/**
	 * A builder class for {@link SequenceRemolder}.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class Builder {
		private final ArrayList<Remolder> remolders;

		public Builder() {
			this.remolders = new ArrayList<>();
		}

		public void then(Remolder remolder) {
			if (remolder instanceof SequenceRemolder(List<Remolder> list)) {
				this.remolders.addAll(list);
			} else {
				this.remolders.add(remolder);
			}
		}

		public void then(Remolder[] remolders) {
			for (Remolder remolder : remolders) {
				this.remolders.add(remolder);
			}
		}

		public void then(Collection<Remolder> remolders) {
			this.remolders.addAll(remolders);
		}

		public SequenceRemolder complete() {
			return new SequenceRemolder(this.remolders);
		}
	}
}
