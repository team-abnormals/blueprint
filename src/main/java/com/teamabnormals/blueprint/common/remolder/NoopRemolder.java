package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.Codec;

/**
 * A {@link Remolder} implementation that creates noop {@link Remold} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum NoopRemolder implements Remolder {
	INSTANCE;

	public static final Codec<NoopRemolder> CODEC = Codec.unit(INSTANCE);

	@Override
	public Remold remold() throws Exception {
		return new Remold(this.getClass().getSimpleName(), (molding, owner, method) -> {}, new Remold.Fields());
	}

	@Override
	public Codec<? extends Remolder> codec() {
		return CODEC;
	}

	@Override
	public String toString() {
		return "Noop";
	}
}
