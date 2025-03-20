package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.remolder.data.DynamicReference;
import com.teamabnormals.blueprint.common.remolder.data.EncapsulatedDataVisitor;
import com.teamabnormals.blueprint.common.remolder.data.Molding;
import com.teamabnormals.blueprint.common.remolder.data.VariableDataVisitor;

/**
 * A {@link Remolder} implementation for replacing abstract data.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record ReplaceRemolder(DynamicReference.Expression target, DynamicReference value) implements Remolder {
	public static final MapCodec<ReplaceRemolder> CODEC = RecordCodecBuilder.mapCodec(instance -> {
		return instance.group(
				DynamicReference.EXPRESSION_CODEC.fieldOf("target").forGetter(ReplaceRemolder::target),
				DynamicReference.MAP_CODEC.forGetter(ReplaceRemolder::value)
		).apply(instance, ReplaceRemolder::new);
	});

	@Override
	public void remold(Molding molding) throws Exception {
		var target = this.target;
		var targetVisitor = target.visitor();
		if (targetVisitor instanceof VariableDataVisitor variable) {
			this.value.visitor().visit(molding);
			variable.set(molding);
		} else if (targetVisitor instanceof EncapsulatedDataVisitor encapsulated) {
			molding.set(encapsulated.parent(), encapsulated.identifier(), this.value.visitor());
		} else throw new UnsupportedOperationException("Don't know how to replace target: " + target.getRawExpression());
	}

	@Override
	public MapCodec<? extends Remolder> codec() {
		return CODEC;
	}
}
