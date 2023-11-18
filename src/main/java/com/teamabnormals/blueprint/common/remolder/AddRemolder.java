package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.remolder.data.DataAccessor;
import com.teamabnormals.blueprint.common.remolder.data.DynamicReference;

/**
 * A {@link Remolder} implementation for adding new abstract data.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record AddRemolder(DynamicReference.Expression target, DynamicReference value) implements Remolder {
	public static final Codec<AddRemolder> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				DynamicReference.EXPRESSION_CODEC.fieldOf("target").forGetter(AddRemolder::target),
				DynamicReference.MAP_CODEC.forGetter(AddRemolder::value)
		).apply(instance, AddRemolder::new);
	});

	@Override
	public Remold remold() throws Exception {
		DataAccessor targetAccessor = this.target.access();
		DataAccessor valueAccessor = this.value.access();
		Remold.Fields fields = new Remold.Fields();
		targetAccessor.accept(fields);
		valueAccessor.accept(fields);
		return new Remold(this.getClass().getSimpleName(), (molding, owner, method) -> {
			targetAccessor.add(molding, owner, method, valueAccessor);
		}, fields);
	}

	@Override
	public Codec<? extends Remolder> codec() {
		return CODEC;
	}
}
