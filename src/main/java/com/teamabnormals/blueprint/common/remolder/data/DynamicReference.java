package com.teamabnormals.blueprint.common.remolder.data;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.teamabnormals.blueprint.common.remolder.util.DataExpression;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;

import java.text.ParseException;
import java.util.function.Function;

import static com.teamabnormals.blueprint.common.remolder.data.VariableDataVisitor.OPS;

/**
 * The interface for facilitating data-driven access to direct and expressed {@link DataVisitor} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public sealed interface DynamicReference {
	Codec<Direct> DIRECT_CODEC = new Codec<>() {
		@Override
		public <T> DataResult<Pair<Direct, T>> decode(DynamicOps<T> ops, T input) {
			return DataResult.success(Pair.of(new Direct(dynamicOps -> ops.convertTo(dynamicOps, input)), ops.empty()));
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> DataResult<T> encode(Direct direct, DynamicOps<T> ops, T prefix) {
			return DataResult.success((T) direct.getter.apply(ops));
		}
	};
	Codec<Expression> EXPRESSION_CODEC = Codec.STRING.flatXmap(string -> {
		try {
			return DataResult.success(eval(string));
		} catch (ParseException exception) {
			return DataResult.error(exception::getMessage);
		}
	}, expression -> DataResult.success(expression.getRawExpression()));
	MapCodec<DynamicReference> MAP_CODEC = Codec.mapEither(
			DIRECT_CODEC.fieldOf("value"),
			EXPRESSION_CODEC.fieldOf("expressed_value")
	).xmap(either -> {
		var left = either.left();
		if (left.isPresent()) return left.get();
		return either.right().get();
	}, reference -> {
		if (reference instanceof Direct direct) return Either.left(direct);
		return Either.right((Expression) reference);
	});

	static Direct value(Function<DynamicOps<?>, ?> getter) {
		return new Direct(getter);
	}

	static <A> Direct value(A value, Encoder<A> encoder) {
		return new Direct(dynamicOps -> {
			var dataResult = encoder.encodeStart(dynamicOps, value);
			var error = dataResult.error();
			if (error.isPresent()) throw new RuntimeException(error.get().message());
			return dataResult.result().get();
		});
	}

	static <A> Direct value(A value, Function<A, JsonElement> encoder) {
		return value(value, new Encoder<>() {
			@Override
			public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
				return DataResult.success(JsonOps.INSTANCE.convertTo(ops, encoder.apply(input)));
			}
		});
	}

	static Expression target(String expression) {
		return new Expression(expression, null);
	}

	static Expression eval(String expression) throws ParseException {
		try {
			return new Expression(expression);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	// TODO: Maybe rename
	static Expression parse(String expression) {
		try {
			return new Expression(expression);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	DataVisitor visitor();

	final class Direct implements DynamicReference {
		private final Function<DynamicOps<?>, ?> getter;
		private final DataVisitor visitor;

		public Direct(Function<DynamicOps<?>, ?> getter) {
			this.getter = getter;
			this.visitor = molding -> {
				molding.provideVariable("direct", DataType.FUNCTION, getter).visit(molding);
				OPS.visit(molding);
				molding.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
				var type = molding.getDataType();
				molding.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName());
				return type;
			};
		}

		@Override
		public DataVisitor visitor() {
			return this.visitor;
		}
	}

	final class Expression implements DynamicReference {
		private final String rawExpression;
		private final DataVisitor visitor;

		public Expression(String rawExpression) throws ParseException {
			this(rawExpression, DataExpression.parse(rawExpression));
		}

		public Expression(String rawExpression, @Nullable DataVisitor visitor) {
			this.rawExpression = rawExpression;
			this.visitor = visitor;
		}

		public String getRawExpression() {
			return this.rawExpression;
		}

		@Override
		public DataVisitor visitor() {
			return this.visitor;
		}
	}
}
