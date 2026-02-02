package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.remolder.data.Molding;
import org.objectweb.asm.Opcodes;

/**
 * A {@link Remolder} implementation for continuing in a loop.
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum ContinueRemolder implements Remolder {
	INSTANCE;

	public static final MapCodec<ContinueRemolder> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public void remold(Molding molding) {
		if (molding.getContinueLabel() == null) return;
		molding.getContinueLabel().jump(molding, Opcodes.GOTO);
	}

	@Override
	public MapCodec<? extends Remolder> codec() {
		return CODEC;
	}

	@Override
	public String toString() {
		return "Continue";
	}
}
