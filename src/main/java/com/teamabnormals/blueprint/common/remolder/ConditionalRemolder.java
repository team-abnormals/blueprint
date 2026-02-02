package com.teamabnormals.blueprint.common.remolder;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.teamabnormals.blueprint.common.remolder.data.DataVisitor;
import com.teamabnormals.blueprint.common.remolder.data.DataVisitors;
import com.teamabnormals.blueprint.common.remolder.data.DynamicReference;
import com.teamabnormals.blueprint.common.remolder.data.Molding;
import com.teamabnormals.blueprint.common.remolder.util.DiscretionaryLabel;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A {@link Remolder} implementation for if-else branching.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record ConditionalRemolder(List<Pair<DynamicReference.Expression, Remolder>> branches, @Nullable Remolder elseRemolder) implements Remolder {
	public static final MapCodec<ConditionalRemolder> CODEC = new MapCodec<>() {
		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.of(ops.createString("else"), ops.createString(""));
		}

		@Override
		public <T> DataResult<ConditionalRemolder> decode(DynamicOps<T> ops, MapLike<T> input) {
			var rawBranches = input.get("");
			var typeKey = ops.createString("type");
			var elseKey = ops.createString("else");
			if (rawBranches == null) {
				var iterator = input.entries().iterator();
				Pair<DynamicReference.Expression, Remolder> trueBranch;
				while (true) {
					if (!iterator.hasNext())
						return DataResult.error(() -> "No true branch found");
                    var next = iterator.next();
					var key = next.getFirst();
					if (!key.equals(typeKey) && !key.equals(elseKey)) {
						var expressionResult = DynamicReference.EXPRESSION_CODEC.decode(ops, key);
						if (expressionResult.isError())
							return DataResult.error(() -> expressionResult.error().get().message());
						var remolderResult = Remolder.CODEC.decode(ops, next.getSecond());
						if (remolderResult.isError())
							return DataResult.error(() -> remolderResult.error().get().message());
						trueBranch = Pair.of(expressionResult.result().get().getFirst(), remolderResult.result().get().getFirst());
						break;
					}
				}
				var elseRemolder = input.get("else");
				if (elseRemolder != null) {
					var remolderResult = Remolder.CODEC.decode(ops, elseRemolder);
					if (remolderResult.isError())
						return DataResult.error(() -> remolderResult.error().get().message());
					return DataResult.success(new ConditionalRemolder(List.of(trueBranch), remolderResult.result().get().getFirst()));
				}
				return DataResult.success(new ConditionalRemolder(List.of(trueBranch), null));
			}
			List<Pair<DynamicReference.Expression, Remolder>> branches = new ArrayList<>();
			var branchesResult = ops.getStream(rawBranches);
			if (branchesResult.isError())
				return DataResult.error(() -> branchesResult.error().get().message());
			var branchesIterator = branchesResult.result().get().iterator();
			Remolder elseRemolder = null;
			while (branchesIterator.hasNext()) {
				var rawBranch = branchesIterator.next();
				var branchResult = ops.getMap(rawBranch);
				if (branchResult.isError()) return DataResult.error(() -> branchResult.error().get().message());
				var branch = branchResult.result().get();
				var elseData = branch.get("else");
				if (elseData != null) {
					var remolderResult = Remolder.CODEC.decode(ops, elseData);
					if (remolderResult.isError())
						return DataResult.error(() -> remolderResult.error().get().message());
					elseRemolder = remolderResult.result().get().getFirst();
				} else {
					var optionalKeyValuePair = branch.entries().findFirst();
					if (optionalKeyValuePair.isEmpty()) continue;
					var keyValuePair = optionalKeyValuePair.get();
					var expressionResult = DynamicReference.EXPRESSION_CODEC.decode(ops, keyValuePair.getFirst());
					if (expressionResult.isError())
						return DataResult.error(() -> expressionResult.error().get().message());
					var remolderResult = Remolder.CODEC.decode(ops, keyValuePair.getSecond());
					if (remolderResult.isError())
						return DataResult.error(() -> remolderResult.error().get().message());
					branches.add(Pair.of(expressionResult.result().get().getFirst(), remolderResult.result().get().getFirst()));
				}
			}
			return DataResult.success(new ConditionalRemolder(branches, elseRemolder));
		}

		@Override
		public <T> RecordBuilder<T> encode(ConditionalRemolder input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			var branches = input.branches;
			var elseRemolder = input.elseRemolder;
			DataResult<T> remolderResult;
			if (branches.size() == 1) {
				var ifTrue = branches.getFirst();
				if ((remolderResult = Remolder.CODEC.encodeStart(ops, ifTrue.getSecond())).isError())
					return prefix.withErrorsFrom(remolderResult);
				prefix.add(ifTrue.getFirst().getRawExpression(), remolderResult.result().get());
				if (elseRemolder != null) {
					if ((remolderResult = Remolder.CODEC.encodeStart(ops, elseRemolder)).isError())
						return prefix.withErrorsFrom(remolderResult);
					prefix.add("else", remolderResult.result().get());
				}
			} else {
				var branchesList = ops.listBuilder();
				RecordBuilder<T> builder;
				for (var branch : branches) {
					if ((remolderResult = Remolder.CODEC.encodeStart(ops, branch.getSecond())).isError())
						return prefix.withErrorsFrom(remolderResult);
					builder = ops.mapBuilder();
					builder.add(branch.getFirst().getRawExpression(), remolderResult.result().get());
					branchesList.add(builder.build(ops.empty()));
				}
				if (elseRemolder != null) {
					builder = ops.mapBuilder();
					if ((remolderResult = Remolder.CODEC.encodeStart(ops, elseRemolder)).isError())
						return prefix.withErrorsFrom(remolderResult);
					builder.add("else", remolderResult.result().get());
					branchesList.add(builder.build(ops.empty()));
				}
				prefix.add("", branchesList.build(ops.empty()));
			}
			return prefix;
		}
	};

	@Override
	public void remold(Molding molding) throws Exception {
		var branches = this.branches;
		int branchCount = branches.size();
		var elseRemolder = this.elseRemolder;
		if (branchCount == 0) {
			// Who would do this? Anyway...
			if (elseRemolder != null) elseRemolder.remold(molding);
			return;
		}
		int lastBranchIndex = branchCount - 1;
		// Handle initial branches
		DiscretionaryLabel onFalse;
		DiscretionaryLabel end = DiscretionaryLabel.label();
		for (int i = 0; i < lastBranchIndex; i++) {
			onFalse = DiscretionaryLabel.label();
			var branch = branches.get(i);
			emitBranch(molding, onFalse, branch.getFirst().visitor(), branch.getSecond());
			molding.visitJumpInsn(Opcodes.GOTO, end);
			onFalse.visitIfJumped(molding);
		}
		// Handle the final branch
		var finalBranch = branches.get(lastBranchIndex);
		if (elseRemolder != null) {
			emitBranch(molding, onFalse = DiscretionaryLabel.label(), finalBranch.getFirst().visitor(), finalBranch.getSecond());
			molding.visitJumpInsn(Opcodes.GOTO, end);
			onFalse.visitIfJumped(molding);
			elseRemolder.remold(molding);
		} else {
			emitBranch(molding, onFalse = end, finalBranch.getFirst().visitor(), finalBranch.getSecond());
			if (lastBranchIndex == 0) {
				onFalse.visitIfJumped(molding);
				return;
			}
		}
		molding.visitLabel(end);
	}

	static void emitBranch(Molding molding, DiscretionaryLabel onFalse, DataVisitor visitor, Remolder remolder) throws Exception {
		DiscretionaryLabel onTrue = DiscretionaryLabel.label();
		if (DataVisitors.isLogicalVisitor(visitor)) {
			var buffer = molding.createBuffer(onTrue, onFalse, false);
			visitor.visit(buffer);
			buffer.accept(molding);
		} else {
			var buffer = molding.createBuffer();
			var type = visitor.visit(buffer);
			if (!type.isBoolean()) DataVisitors.convertToBoolean(buffer, type);
			else type.unbox(buffer);
			buffer.accept(molding);
			onFalse.jump(molding, Opcodes.IFEQ);
		}
		onTrue.visitIfJumped(molding);
		remolder.remold(molding);
	}

	@Override
	public MapCodec<? extends Remolder> codec() {
		return CODEC;
	}
}
