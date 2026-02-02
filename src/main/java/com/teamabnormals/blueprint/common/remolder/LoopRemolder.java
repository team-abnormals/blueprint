package com.teamabnormals.blueprint.common.remolder;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.remolder.data.DataVisitors;
import com.teamabnormals.blueprint.common.remolder.data.DynamicReference;
import com.teamabnormals.blueprint.common.remolder.data.Molding;
import com.teamabnormals.blueprint.common.remolder.util.DiscretionaryLabel;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LabelNode;

/**
 * A {@link Remolder} implementation for while loops.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record LoopRemolder(DynamicReference.Expression condition, Either<Remolder, Remolder> remolder) implements Remolder {
	public static final MapCodec<LoopRemolder> CODEC = RecordCodecBuilder.mapCodec(instance -> {
		return instance.group(
				DynamicReference.EXPRESSION_CODEC.fieldOf("while").forGetter(LoopRemolder::condition),
				Codec.mapEither(Remolder.CODEC.fieldOf("remold"), Remolder.CODEC.fieldOf("do")).forGetter(LoopRemolder::remolder)
		).apply(instance, LoopRemolder::new);
	});

	@Override
	public void remold(Molding molding) throws Exception {
		DiscretionaryLabel continueLabel = DiscretionaryLabel.label();
		DiscretionaryLabel breakLabel = DiscretionaryLabel.label();
		Pair<DiscretionaryLabel, DiscretionaryLabel> oldLabels = null;
		// We may be inside a loop, so save old labels to restore
		if (molding.getContinueLabel() != null) {
			oldLabels = molding.beginInnerLoop(continueLabel, breakLabel);
		} else {
			molding.beginLoop(continueLabel, breakLabel);
		}
		// Optimize for while (true)
		var visitor = this.condition.visitor();
		if (visitor instanceof DataVisitors.Constant constant && constant.type().isBoolean() && (Boolean) constant.value()) {
			molding.visitLabel(continueLabel);
			Either.unwrap(this.remolder).remold(molding);
			molding.visitJumpInsn(Opcodes.GOTO, continueLabel);
		} else {
			var leftRemolder = this.remolder.left();
			if (leftRemolder.isPresent()) {
				// while () {}
				molding.visitLabel(continueLabel);
				// if condition is false, break out of loop
				ConditionalRemolder.emitBranch(molding, breakLabel, visitor, leftRemolder.get());
				// condition was true and remolder ran, so continue
				molding.visitJumpInsn(Opcodes.GOTO, continueLabel);
			} else {
				// do {} while();
				Label onTrueLabel = new Label();
				onTrueLabel.info = new LabelNode(onTrueLabel);
				molding.visitLabel(onTrueLabel);
				this.remolder.right().get().remold(molding);
				continueLabel.visitIfJumped(molding);
				// if condition is true, jump back to body
				if (DataVisitors.isLogicalVisitor(visitor)) {
					var buffer = molding.createBuffer(onTrueLabel, breakLabel, true);
					visitor.visit(buffer);
					buffer.accept(molding);
				} else {
					var buffer = molding.createBuffer();
					var type = visitor.visit(buffer);
					if (!type.isBoolean()) DataVisitors.convertToBoolean(buffer, type);
					else type.unbox(buffer);
					buffer.accept(molding);
					molding.visitJumpInsn(Opcodes.IFNE, onTrueLabel);
				}
				// condition was false, so fall through to end
			}
		}
		breakLabel.visitIfJumped(molding);
		if (oldLabels != null) molding.endLoop(oldLabels);
		else molding.endLoop();
	}

	@Override
	public MapCodec<? extends Remolder> codec() {
		return CODEC;
	}
}
