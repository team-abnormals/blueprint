package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.remolder.data.DataAccessor;
import com.teamabnormals.blueprint.common.remolder.data.DynamicReference;

/**
 * A {@link Remolder} implementation for replacing abstract data.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record ReplaceRemolder(DynamicReference.Expression target, DynamicReference value) implements Remolder {
	public static final Codec<ReplaceRemolder> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				DynamicReference.EXPRESSION_CODEC.fieldOf("target").forGetter(ReplaceRemolder::target),
				DynamicReference.MAP_CODEC.forGetter(ReplaceRemolder::value)
		).apply(instance, ReplaceRemolder::new);
	});

	@Override
	public Remold remold() throws Exception {
		DataAccessor targetAccessor = this.target.access();
		DataAccessor valueAccessor = this.value.access();
		Remold.Fields fields = new Remold.Fields();
		targetAccessor.accept(fields);
		valueAccessor.accept(fields);
		return new Remold(this.getClass().getSimpleName(), (molding, owner, method) -> {
			targetAccessor.set(molding, owner, method, valueAccessor);
		}, fields);
	}

	@Override
	public Codec<? extends Remolder> codec() {
		return CODEC;
	}
}
