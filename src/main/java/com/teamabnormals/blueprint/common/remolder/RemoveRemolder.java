package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.remolder.data.DynamicReference;
import com.teamabnormals.blueprint.common.remolder.data.EncapsulatedDataVisitor;
import com.teamabnormals.blueprint.common.remolder.data.Molding;

/**
 * A {@link Remolder} implementation for removing abstract data.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record RemoveRemolder(DynamicReference.Expression target) implements Remolder {
	public static final MapCodec<RemoveRemolder> CODEC = DynamicReference.EXPRESSION_CODEC.fieldOf("target").xmap(RemoveRemolder::new, RemoveRemolder::target);

	@Override
	public void remold(Molding molding) throws Exception {
		var target = this.target;
		var targetVisitor = target.visitor();
		if (targetVisitor instanceof EncapsulatedDataVisitor encapsulated) {
			molding.remove(encapsulated.parent(), encapsulated.identifier());
		} else throw new UnsupportedOperationException("Don't know how to remove target: " + target.getRawExpression());
	}

	@Override
	public MapCodec<? extends Remolder> codec() {
		return CODEC;
	}
}
