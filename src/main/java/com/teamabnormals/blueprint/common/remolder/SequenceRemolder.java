package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.remolder.data.Molding;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

/**
 * A {@link Remolder} implementation that runs multiple remolders in sequential order.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record SequenceRemolder(List<Remolder> remolders) implements Remolder {
	public static final MapCodec<SequenceRemolder> CODEC = ExtraCodecs.nonEmptyList(Remolder.CODEC.listOf()).fieldOf("remolders").xmap(SequenceRemolder::new, SequenceRemolder::remolders);

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
}
