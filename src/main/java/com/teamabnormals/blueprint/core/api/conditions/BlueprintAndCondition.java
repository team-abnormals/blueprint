package com.teamabnormals.blueprint.core.api.conditions;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A special version of the and condition that stops reading if a false condition is met.
 * <p>This is useful for testing another condition only if the former conditions are met.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintAndCondition implements ICondition {
	public static final Codec<List<ICondition>> SHORT_CIRCUIT_CODEC = new Codec<>() {
		@Override
		public <T> DataResult<Pair<List<ICondition>, T>> decode(DynamicOps<T> ops, T input) {
			var listResult = ops.getList(input);
			var error = listResult.error();
			if (error.isPresent()) return DataResult.error(() -> error.get().message());
			var list = listResult.result().get();
			try {
				ArrayList<ICondition> conditions = new ArrayList<>();
				list.accept(element -> {
					var conditionResult = ICondition.CODEC.decode(ops, element);
					var conditionError = conditionResult.error();
					if (conditionError.isPresent()) throw new EscapeException(conditionError.get().message());
					ICondition condition = conditionResult.result().get().getFirst();
					if (!condition.test(IContext.EMPTY)) throw new EscapeException(null);
					conditions.add(condition);
				});
				return DataResult.success(Pair.of(conditions, input));
			} catch (EscapeException e) {
				// Curse technique
				String errorMessage = e.error;
				return errorMessage != null ? DataResult.error(() -> errorMessage) : DataResult.success(Pair.of(List.of(), input));
			}
		}

		@Override
		public <T> DataResult<T> encode(List<ICondition> input, DynamicOps<T> ops, T prefix) {
			return LIST_CODEC.encode(input, ops, prefix);
		}
	};
	public static final MapCodec<BlueprintAndCondition> CODEC = RecordCodecBuilder.mapCodec(
		builder -> builder.group(
			SHORT_CIRCUIT_CODEC.fieldOf("values").forGetter(condition -> condition.children))
		.apply(builder, BlueprintAndCondition::new)
	);
	private final List<ICondition> children;

	@Deprecated
	public BlueprintAndCondition(ResourceLocation location, List<ICondition> children) {
		this(children);
	}

	public BlueprintAndCondition(List<ICondition> children) {
		this.children = children;
	}

	public BlueprintAndCondition(ICondition... children) {
		this(List.of(children));
	}

	@Override
	public boolean test(ICondition.IContext context) {
		return !this.children.isEmpty();
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}

	private static final class EscapeException extends RuntimeException {
		@Nullable
		private final String error;

		private EscapeException(String error) {
			this.error = error;
		}
	}
}