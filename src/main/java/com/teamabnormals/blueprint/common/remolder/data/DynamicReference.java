package com.teamabnormals.blueprint.common.remolder.data;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.teamabnormals.blueprint.common.remolder.Remold;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.text.ParseException;
import java.util.function.Function;

/**
 * The interface for facilitating data-driven access to direct and expressed {@link DataAccessor} instances.
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
		return new Expression(expression);
	}

	DataAccessor access();

	final class Direct implements DynamicReference {
		private final Function<DynamicOps<?>, ?> getter;
		private final DataAccessor dataAccessor;

		public Direct(Function<DynamicOps<?>, ?> getter) {
			this.getter = getter;
			String fieldName = "getter" + System.nanoTime();
			this.dataAccessor = new DataAccessor() {
				@Override
				public void visitParent(Molding<?> molding, String owner, MethodVisitor method) {}

				@Override
				public void visitIdentifier(Molding<?> molding, String owner, MethodVisitor method) {}

				@Override
				public boolean isIdentifierAnIndex() {
					return false;
				}

				@Override
				public void visit(Molding<?> molding, String owner, MethodVisitor method) {
					method.visitVarInsn(Opcodes.ALOAD, 0);
					method.visitFieldInsn(Opcodes.GETFIELD, owner, fieldName, "Ljava/util/function/Function;");
					method.visitVarInsn(Opcodes.ALOAD, 1);
					method.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
					molding.cast(method);
				}

				@Override
				public void accept(Remold.Fields fields) {
					fields.addField(fieldName, new Remold.Field(() -> getter, "java/util/function/Function", "Ljava/util/function/Function;", (molding, methodVisitor) -> {}));
				}

				@Override
				public DataType getDataType() {
					return DataType.ELEMENT;
				}
			};
		}

		@Override
		public DataAccessor access() {
			return this.dataAccessor;
		}
	}

	final class Expression implements DynamicReference {
		private final String rawExpression;
		private final DataAccessor accessor;

		public Expression(String rawExpression) throws ParseException {
			this(rawExpression, DataExpressionParser.parse(rawExpression));
		}

		public Expression(String rawExpression, @Nullable DataAccessor accessor) {
			this.rawExpression = rawExpression;
			this.accessor = accessor;
		}

		public String getRawExpression() {
			return this.rawExpression;
		}

		@Override
		public DataAccessor access() {
			return this.accessor;
		}
	}
}
