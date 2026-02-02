package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.remolder.data.Molding;
import org.objectweb.asm.Opcodes;

/**
 * A {@link Remolder} implementation for breaking in a loop.
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum BreakRemolder implements Remolder {
	INSTANCE;

	public static final MapCodec<BreakRemolder> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public void remold(Molding molding) {
		if (molding.getBreakLabel() == null) return;
		molding.getBreakLabel().jump(molding, Opcodes.GOTO);
	}

	@Override
	public MapCodec<? extends Remolder> codec() {
		return CODEC;
	}

	@Override
	public String toString() {
		return "Break";
	}
}
