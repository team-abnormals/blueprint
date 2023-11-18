package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.common.remolder.data.DynamicReference;
import org.objectweb.asm.Opcodes;

/**
 * A {@link Remolder} implementation for removing abstract data.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record RemoveRemolder(DynamicReference.Expression target) implements Remolder {
	public static final Codec<RemoveRemolder> CODEC = DynamicReference.EXPRESSION_CODEC.fieldOf("target").xmap(RemoveRemolder::new, RemoveRemolder::target).codec();

	@Override
	public Remold remold() throws Exception {
		var targetAccessor = this.target.access();
		Remold.Fields fields = new Remold.Fields();
		targetAccessor.accept(fields);
		return new Remold(this.getClass().getSimpleName(), (molding, owner, method) -> {
			targetAccessor.remove(molding, owner, method);
			method.visitInsn(Opcodes.POP);
		}, fields);
	}

	@Override
	public Codec<? extends Remolder> codec() {
		return CODEC;
	}
}
