package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.remolder.data.*;

/**
 * A {@link Remolder} implementation for adding new abstract data.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record AddRemolder(DynamicReference.Expression target, DynamicReference value) implements Remolder {
	public static final MapCodec<AddRemolder> CODEC = RecordCodecBuilder.mapCodec(instance -> {
		return instance.group(
				DynamicReference.EXPRESSION_CODEC.fieldOf("target").forGetter(AddRemolder::target),
				DynamicReference.MAP_CODEC.forGetter(AddRemolder::value)
		).apply(instance, AddRemolder::new);
	});

	@Override
	public void remold(Molding molding) throws Exception {
		var target = this.target;
		var targetVisitor = target.visitor();
		if (targetVisitor instanceof EncapsulatedDataVisitor encapsulated) {
			molding.add(encapsulated.parent(), encapsulated.identifier(), this.value.visitor());
		} else {
			molding.add(targetVisitor, null, this.value.visitor());
		}
	}

	@Override
	public MapCodec<? extends Remolder> codec() {
		return CODEC;
	}
}
