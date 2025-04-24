package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.remolder.data.Molding;

/**
 * A {@link Remolder} implementation that does nothing.
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum NoopRemolder implements Remolder {
	INSTANCE;

	public static final MapCodec<NoopRemolder> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public void remold(Molding molding) {
	}

	@Override
	public MapCodec<? extends Remolder> codec() {
		return CODEC;
	}

	@Override
	public String toString() {
		return "Noop";
	}
}
