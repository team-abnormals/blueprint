package com.teamabnormals.blueprint.common.remolder.data;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.objectweb.asm.Opcodes.*;

/**
 * Utility class for {@link DataVisitor} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class DataVisitors {
	static Const booleanValue(boolean value) {
		return new Const(DataType.BOOLEAN, value, value ? ICONST_1 : ICONST_0);
	}

	static DataVisitor charValue(char value) {
		return value <= 5 ? new Const(DataType.CHAR, value, value + ICONST_0) : new Ldc(DataType.CHAR, value);
	}

	static DataVisitor byteValue(byte value) {
		return value >= -1 && value <= 5 ? new Const(DataType.BYTE, value, value + ICONST_0) : new Ldc(DataType.BYTE, value);
	}

	static DataVisitor shortValue(short value) {
		return value >= -1 && value <= 5 ? new Const(DataType.SHORT, value, value + ICONST_0) : new Ldc(DataType.SHORT, value);
	}

	static DataVisitor intValue(int value) {
		return value >= -1 && value <= 5 ? new Const(DataType.INT, value, value + ICONST_0) : new Ldc(DataType.INT, value);
	}

	static DataVisitor longValue(long value) {
		if (value == 0) return new Const(DataType.LONG, value, LCONST_0);
		if (value == 1) return new Const(DataType.LONG, value, LCONST_1);
		return new Ldc(DataType.LONG, value);
	}

	static DataVisitor floatValue(float value) {
		int opcode;
		if (value == 0.0F) {
			opcode = FCONST_0;
		} else if (value == 1.0F) {
			opcode = FCONST_1;
		} else if (value == 2.0F) {
			opcode = FCONST_2;
		} else return new Ldc(DataType.FLOAT, value);
		return new Const(DataType.FLOAT, value, opcode);
	}

	static DataVisitor doubleValue(double value) {
		if (value == 0.0D) return new Const(DataType.DOUBLE, value, DCONST_0);
		if (value == 1.0D) return new Const(DataType.DOUBLE, value, DCONST_1);
		return new Ldc(DataType.DOUBLE, value);
	}

	static DataVisitor string(String value) {
		return new Ldc(DataType.STRING, value);
	}

	static void ifneResult(MethodVisitor method) {
		Label trueResult = new Label();
		method.visitJumpInsn(IFNE, trueResult);
		method.visitInsn(ICONST_0);
		Label end = new Label();
		method.visitJumpInsn(GOTO, end);
		method.visitLabel(trueResult);
		method.visitInsn(ICONST_1);
		method.visitLabel(end);
	}

	static void boolean2Int(MethodVisitor method) {
		method.visitInsn(ICONST_0);
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "compare", "(ZZ)I", false);
	}

	static void boolean2Long(MethodVisitor method) {
		Label trueLabel = new Label();
		method.visitJumpInsn(IFNE, trueLabel);
		method.visitInsn(LCONST_0);
		Label endLabel = new Label();
		method.visitJumpInsn(GOTO, endLabel);
		method.visitLabel(trueLabel);
		method.visitInsn(LCONST_1);
		method.visitLabel(endLabel);
	}

	static void boolean2Float(MethodVisitor method) {
		Label trueLabel = new Label();
		method.visitJumpInsn(IFNE, trueLabel);
		method.visitInsn(FCONST_0);
		Label endLabel = new Label();
		method.visitJumpInsn(GOTO, endLabel);
		method.visitLabel(trueLabel);
		method.visitInsn(FCONST_1);
		method.visitLabel(endLabel);
	}

	static void boolean2Double(MethodVisitor method) {
		Label trueLabel = new Label();
		method.visitJumpInsn(IFNE, trueLabel);
		method.visitInsn(DCONST_0);
		Label endLabel = new Label();
		method.visitJumpInsn(GOTO, endLabel);
		method.visitLabel(trueLabel);
		method.visitInsn(DCONST_1);
		method.visitLabel(endLabel);
	}

	static void long2Boolean(MethodVisitor method) {
		method.visitInsn(LCONST_0);
		method.visitInsn(LCMP);
		ifneResult(method);
	}

	static void float2Boolean(MethodVisitor method) {
		method.visitInsn(FCONST_0);
		method.visitInsn(FCMPL);
		ifneResult(method);
	}

	static void double2Boolean(MethodVisitor method) {
		method.visitInsn(DCONST_0);
		method.visitInsn(DCMPL);
		ifneResult(method);
	}

	static void int2Long(MethodVisitor method) {
		method.visitInsn(I2L);
	}

	static void int2Float(MethodVisitor method) {
		method.visitInsn(I2F);
	}

	static void int2Double(MethodVisitor method) {
		method.visitInsn(I2D);
	}

	static void int2Byte(MethodVisitor method) {
		method.visitInsn(I2B);
	}

	static void int2Char(MethodVisitor method) {
		method.visitInsn(I2C);
	}

	static void int2Short(MethodVisitor method) {
		method.visitInsn(I2S);
	}

	static void float2Int(MethodVisitor method) {
		method.visitInsn(F2I);
	}

	static void float2Long(MethodVisitor method) {
		method.visitInsn(F2L);
	}

	static void float2Double(MethodVisitor method) {
		method.visitInsn(F2D);
	}

	static void long2Int(MethodVisitor method) {
		method.visitInsn(L2I);
	}

	static void long2Float(MethodVisitor method) {
		method.visitInsn(L2F);
	}

	static void long2Double(MethodVisitor method) {
		method.visitInsn(L2D);
	}

	static void double2Int(MethodVisitor method) {
		method.visitInsn(D2I);
	}

	static void double2Long(MethodVisitor method) {
		method.visitInsn(D2L);
	}

	static void double2Float(MethodVisitor method) {
		method.visitInsn(D2F);
	}

	static void number2Boolean(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
		double2Boolean(method);
	}

	static void number2Char(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "charValue", "()C", false);
	}

	static void number2Byte(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B", false);
	}

	static void number2Short(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S", false);
	}

	static void number2Int(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
	}

	static void number2Long(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
	}

	static void number2Float(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
	}

	static void number2Double(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
	}

	static void boxBoolean(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
	}

	static void unboxBoolean(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
	}

	static void boxChar(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
	}

	static void unboxCharacter(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
	}

	static void boxByte(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
	}

	static void unboxByte(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
	}

	static void boxShort(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
	}

	static void unboxShort(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
	}

	static void boxInt(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
	}

	static void unboxInteger(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
	}

	static void boxLong(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
	}

	static void unboxLong(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
	}

	static void boxFloat(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
	}

	static void unboxFloat(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
	}

	static void boxDouble(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
	}

	static void unboxDouble(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
	}

	static void boxPrimitive(Molding molding, Class<?> clazz) {
		if (clazz == boolean.class) {
			boxBoolean(molding);
		} else if (clazz == int.class) {
			boxInt(molding);
		} else if (clazz == long.class) {
			boxLong(molding);
		} else if (clazz == float.class) {
			boxFloat(molding);
		} else if (clazz == double.class) {
			boxDouble(molding);
		} else if (clazz == char.class) {
			boxChar(molding);
		} else if (clazz == byte.class) {
			boxByte(molding);
		} else if (clazz == short.class) {
			boxShort(molding);
		}
	}

	static void stringLength(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
	}

	static DataVisitor element(DataVisitor visitor) {
		return new DataVisitor() {
			@Override
			public void visit(Molding molding) {
				molding.element(visitor);
			}

			@Override
			public ReturnType getReturnType() {
				return molding -> molding.getRepresentationType(visitor.getReturnType().getDataType(molding));
			}
		};
	}

	static DataVisitor str(DataVisitor visitor) {
		return new DataVisitor() {
			@Override
			public void visit(Molding molding) {
				visitor.visit(molding);
				var dataType = visitor.getReturnType().getDataType(molding);
				try {
					molding.toString(dataType);
				} catch (UnsupportedOperationException unsupported) {
					var type = dataType.getType();
					TypeVisitors.Visitors visitorsForType = TypeVisitors.getVisitors(type);
					if (visitorsForType != null) {
						var converter = visitorsForType.stringConverter();
						if (converter != null) converter.accept(molding);
						return;
					}
					int sort = type.getSort();
					if (sort == Type.OBJECT || sort == Type.ARRAY) {
						molding.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
					}
					throw new UnsupportedOperationException("Don't know how to convert " + type + " to string");
				}
			}

			@Override
			public ReturnType getReturnType() {
				return DataType.STRING;
			}
		};
	}

	static DataVisitor convertViaTypeVisitors(DataVisitor visitor, DataType<?> type, Function<TypeVisitors.Visitors, Consumer<? super Molding>> converterFromVisitors, Consumer<Molding> numberVisitor) {
		var returnType = visitor.getReturnType();
		final Consumer<? super Molding> converter;
		if (returnType instanceof DataType<?> dataType) {
			TypeVisitors.Visitors visitorsForType = TypeVisitors.getVisitors(dataType.getType());
			if (visitorsForType != null) {
				converter = converterFromVisitors.apply(visitorsForType);
				if (converter == null) return visitor;
			} else {
				if (!Number.class.isAssignableFrom(dataType.getClazz()))
					throw new UnsupportedOperationException("Don't know how to convert " + dataType + " to " + type);
				converter = numberVisitor;
			}
		} else {
			converter = molding -> molding.convert(returnType.getDataType(molding), type);
		}
		return new Pipe(visitor, converter, type);
	}

	static DataVisitor convertToBoolean(DataVisitor visitor) {
		return convertViaTypeVisitors(visitor, DataType.BOOLEAN, TypeVisitors.Visitors::booleanConverter, DataVisitors::number2Boolean);
	}

	static DataVisitor convertToBooleanWrapper(DataVisitor visitor) {
		if (visitor.getReturnType() == DataType.BOOLEAN_WRAPPER) return visitor;
		return convertToBoolean(visitor).then(DataVisitors::boxBoolean, DataType.BOOLEAN_WRAPPER);
	}

	static DataVisitor convertToChar(DataVisitor visitor) {
		// Optimize for (char) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number) {
			switch (number) {
				case Integer i -> {
					return charValue((char) number.intValue());
				}
				case Long l -> {
					return charValue((char) number.longValue());
				}
				case Double v -> {
					return charValue((char) number.doubleValue());
				}
				case Float v -> {
					return charValue((char) number.floatValue());
				}
				case Short i -> {
					return charValue((char) number.shortValue());
				}
				case Byte b -> {
					return charValue((char) number.byteValue());
				}
				default -> {
				}
			}
		}
		return convertViaTypeVisitors(visitor, DataType.CHAR, TypeVisitors.Visitors::charConverter, DataVisitors::number2Char);
	}

	static DataVisitor convertToCharWrapper(DataVisitor visitor) {
		if (visitor.getReturnType() == DataType.CHARACTER_WRAPPER) return visitor;
		return convertToChar(visitor).then(DataVisitors::boxChar, DataType.CHARACTER_WRAPPER);
	}

	static DataVisitor convertToByte(DataVisitor visitor) {
		// Optimize for (byte) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return byteValue(number.byteValue());
		return convertViaTypeVisitors(visitor, DataType.BYTE, TypeVisitors.Visitors::byteConverter, DataVisitors::number2Byte);
	}

	static DataVisitor convertToByteWrapper(DataVisitor visitor) {
		if (visitor.getReturnType() == DataType.BYTE_WRAPPER) return visitor;
		return convertToByte(visitor).then(DataVisitors::boxByte, DataType.BYTE_WRAPPER);
	}

	static DataVisitor convertToShort(DataVisitor visitor) {
		// Optimize for (short) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return shortValue(number.shortValue());
		return convertViaTypeVisitors(visitor, DataType.SHORT, TypeVisitors.Visitors::shortConverter, DataVisitors::number2Short);
	}

	static DataVisitor convertToShortWrapper(DataVisitor visitor) {
		if (visitor.getReturnType() == DataType.SHORT_WRAPPER) return visitor;
		return convertToShort(visitor).then(DataVisitors::boxShort, DataType.SHORT_WRAPPER);
	}

	static DataVisitor convertToInt(DataVisitor visitor) {
		// Optimize for (int) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return intValue(number.intValue());
		return convertViaTypeVisitors(visitor, DataType.INT, TypeVisitors.Visitors::intConverter, DataVisitors::number2Int);
	}

	static DataVisitor convertToIntWrapper(DataVisitor visitor) {
		if (visitor.getReturnType() == DataType.INTEGER_WRAPPER) return visitor;
		return convertToInt(visitor).then(DataVisitors::boxInt, DataType.INTEGER_WRAPPER);
	}

	static DataVisitor convertToLong(DataVisitor visitor) {
		// Optimize for (long) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return longValue(number.longValue());
		return convertViaTypeVisitors(visitor, DataType.LONG, TypeVisitors.Visitors::longConverter, DataVisitors::number2Long);
	}

	static DataVisitor convertToLongWrapper(DataVisitor visitor) {
		if (visitor.getReturnType() == DataType.LONG_WRAPPER) return visitor;
		return convertToLong(visitor).then(DataVisitors::boxLong, DataType.LONG_WRAPPER);
	}

	static DataVisitor convertToFloat(DataVisitor visitor) {
		// Optimize for (float) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return floatValue(number.floatValue());
		return convertViaTypeVisitors(visitor, DataType.FLOAT, TypeVisitors.Visitors::floatConverter, DataVisitors::number2Float);
	}

	static DataVisitor convertToFloatWrapper(DataVisitor visitor) {
		if (visitor.getReturnType() == DataType.FLOAT_WRAPPER) return visitor;
		return convertToFloat(visitor).then(DataVisitors::boxFloat, DataType.FLOAT_WRAPPER);
	}

	static DataVisitor convertToDouble(DataVisitor visitor) {
		// Optimize for (double) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return doubleValue(number.doubleValue());
		return convertViaTypeVisitors(visitor, DataType.DOUBLE, TypeVisitors.Visitors::doubleConverter, DataVisitors::number2Double);
	}

	static DataVisitor convertToDoubleWrapper(DataVisitor visitor) {
		if (visitor.getReturnType() == DataType.DOUBLE_WRAPPER) return visitor;
		return convertToDouble(visitor).then(DataVisitors::boxDouble, DataType.DOUBLE_WRAPPER);
	}

	interface Constant extends DataVisitor {
		Object value();
	}

	record Ldc(DataType<?> type, Object value) implements Constant {
		@Override
		public void visit(Molding molding) {
			molding.visitLdcInsn(this.value);
		}

		@Override
		public ReturnType getReturnType() {
			return this.type;
		}
	}

	record Const(DataType<?> type, Object value, int opcode) implements Constant {
		@Override
		public void visit(Molding molding) {
			molding.visitInsn(this.opcode);
		}

		@Override
		public ReturnType getReturnType() {
			return this.type;
		}
	}

	record Pipe(DataVisitor visitor, Consumer<? super Molding> consumer, ReturnType type) implements DataVisitor {
		@Override
		public void visit(Molding molding) {
			this.visitor.visit(molding);
			this.consumer.accept(molding);
		}

		@Override
		public ReturnType getReturnType() {
			return this.type;
		}
	}

	static final class TypeVisitors {
		private static final HashMap<Type, Visitors> MAP = new HashMap<>();
		public static final Visitors BOOLEAN_VISITORS;
		public static final Visitors BOOLEAN_WRAPPER_VISITORS;
		public static final Visitors CHAR_VISITORS;
		public static final Visitors CHAR_WRAPPER_VISITORS;
		public static final Visitors BYTE_VISITORS;
		public static final Visitors BYTE_WRAPPER_VISITORS;
		public static final Visitors SHORT_VISITORS;
		public static final Visitors SHORT_WRAPPER_VISITORS;
		public static final Visitors INT_VISITORS;
		public static final Visitors INT_WRAPPER_VISITORS;
		public static final Visitors LONG_VISITORS;
		public static final Visitors LONG_WRAPPER_VISITORS;
		public static final Visitors FLOAT_VISITORS;
		public static final Visitors FLOAT_WRAPPER_VISITORS;
		public static final Visitors DOUBLE_VISITORS;
		public static final Visitors DOUBLE_WRAPPER_VISITORS;
		public static final Visitors STRING_VISITORS;

		static {
			Consumer<MethodVisitor> booleanToInt = DataVisitors::boolean2Int;
			BOOLEAN_VISITORS = register(Type.BOOLEAN_TYPE, new Visitors(
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Z)Ljava/lang/String;", false),
					null,
					booleanToInt,
					booleanToInt,
					booleanToInt,
					booleanToInt,
					DataVisitors::boolean2Long,
					DataVisitors::boolean2Float,
					DataVisitors::boolean2Double
			));
			Consumer<MethodVisitor> booleanWrapperToInt = method -> {
				unboxBoolean(method);
				boolean2Int(method);
			};
			BOOLEAN_WRAPPER_VISITORS = register(DataType.BOOLEAN_WRAPPER.getType(), new Visitors(
					method -> {
						unboxBoolean(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Z)Ljava/lang/String;", false);
					},
					DataVisitors::unboxBoolean,
					booleanWrapperToInt,
					booleanWrapperToInt,
					booleanWrapperToInt,
					booleanWrapperToInt,
					method -> {
						unboxBoolean(method);
						boolean2Long(method);
					},
					method -> {
						unboxBoolean(method);
						boolean2Float(method);
					},
					method -> {
						unboxBoolean(method);
						boolean2Double(method);
					}
			));
			Consumer<MethodVisitor> ifneResult = DataVisitors::ifneResult;
			Consumer<MethodVisitor> i2b = DataVisitors::int2Byte;
			Consumer<MethodVisitor> i2s = DataVisitors::int2Short;
			Consumer<MethodVisitor> i2l = DataVisitors::int2Long;
			Consumer<MethodVisitor> i2f = DataVisitors::int2Float;
			Consumer<MethodVisitor> i2d = DataVisitors::int2Double;
			CHAR_VISITORS = register(Type.CHAR_TYPE, new Visitors(
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(C)Ljava/lang/String;", false),
					ifneResult,
					null,
					i2b,
					i2s,
					null,
					i2l,
					i2f,
					i2d
			));
			Consumer<MethodVisitor> unboxCharacter = DataVisitors::unboxCharacter;
			CHAR_WRAPPER_VISITORS = register(DataType.CHARACTER_WRAPPER.getType(), new Visitors(
					method -> {
						unboxCharacter(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(C)Ljava/lang/String;", false);
					},
					method -> {
						unboxCharacter(method);
						ifneResult(method);
					},
					unboxCharacter,
					method -> {
						unboxCharacter(method);
						int2Byte(method);
					},
					method -> {
						unboxCharacter(method);
						int2Short(method);
					},
					unboxCharacter,
					method -> {
						unboxCharacter(method);
						int2Long(method);
					},
					method -> {
						unboxCharacter(method);
						int2Float(method);
					},
					method -> {
						unboxCharacter(method);
						int2Double(method);
					}
			));
			Consumer<MethodVisitor> i2c = DataVisitors::int2Char;
			BYTE_VISITORS = register(Type.BYTE_TYPE, new Visitors(
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false),
					ifneResult,
					i2c,
					null,
					null,
					null,
					i2l,
					i2f,
					i2d
			));
			Consumer<MethodVisitor> unboxByte = DataVisitors::unboxByte;
			BYTE_WRAPPER_VISITORS = register(DataType.BYTE_WRAPPER.getType(), new Visitors(
					method -> {
						unboxByte(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
					},
					method -> {
						unboxByte(method);
						ifneResult(method);
					},
					method -> {
						unboxByte(method);
						int2Char(method);
					},
					unboxByte,
					unboxByte,
					unboxByte,
					method -> {
						unboxByte(method);
						int2Long(method);
					},
					method -> {
						unboxByte(method);
						int2Float(method);
					},
					method -> {
						unboxByte(method);
						int2Double(method);
					}
			));
			SHORT_VISITORS = register(Type.SHORT_TYPE, new Visitors(
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false),
					ifneResult,
					i2c,
					i2b,
					null,
					null,
					i2l,
					i2f,
					i2d
			));
			Consumer<MethodVisitor> unboxShort = DataVisitors::unboxShort;
			SHORT_WRAPPER_VISITORS = register(DataType.SHORT_WRAPPER.getType(), new Visitors(
					method -> {
						unboxShort(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
					},
					method -> {
						unboxShort(method);
						ifneResult(method);
					},
					method -> {
						unboxShort(method);
						int2Char(method);
					},
					method -> {
						unboxShort(method);
						int2Byte(method);
					},
					unboxShort,
					unboxShort,
					method -> {
						unboxShort(method);
						int2Long(method);
					},
					method -> {
						unboxShort(method);
						int2Float(method);
					},
					method -> {
						unboxShort(method);
						int2Double(method);
					}
			));
			INT_VISITORS = register(Type.INT_TYPE, new Visitors(
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false),
					ifneResult,
					i2c,
					i2b,
					i2s,
					null,
					i2l,
					i2f,
					i2d
			));
			INT_WRAPPER_VISITORS = register(DataType.INTEGER_WRAPPER.getType(), new Visitors(
					method -> {
						unboxInteger(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
					},
					method -> {
						unboxInteger(method);
						ifneResult(method);
					},
					method -> {
						unboxInteger(method);
						int2Char(method);
					},
					method -> {
						unboxInteger(method);
						int2Byte(method);
					},
					method -> {
						unboxInteger(method);
						int2Short(method);
					},
					DataVisitors::unboxInteger,
					method -> {
						unboxInteger(method);
						int2Long(method);
					},
					method -> {
						unboxInteger(method);
						int2Float(method);
					},
					method -> {
						unboxInteger(method);
						int2Double(method);
					}
			));
			LONG_VISITORS = register(Type.LONG_TYPE, new Visitors(
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false),
					DataVisitors::long2Boolean,
					method -> {
						long2Int(method);
						int2Char(method);
					},
					method -> {
						long2Int(method);
						int2Byte(method);
					},
					method -> {
						long2Int(method);
						int2Short(method);
					},
					DataVisitors::long2Int,
					null,
					DataVisitors::long2Float,
					DataVisitors::long2Double
			));
			LONG_WRAPPER_VISITORS = register(DataType.LONG_WRAPPER.getType(), new Visitors(
					method -> {
						unboxLong(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false);
					},
					method -> {
						unboxLong(method);
						long2Boolean(method);
					},
					method -> {
						unboxLong(method);
						long2Int(method);
						int2Char(method);
					},
					method -> {
						unboxLong(method);
						long2Int(method);
						int2Byte(method);
					},
					method -> {
						unboxLong(method);
						long2Int(method);
						int2Short(method);
					},
					method -> {
						unboxLong(method);
						long2Int(method);
					},
					DataVisitors::unboxLong,
					method -> {
						unboxLong(method);
						long2Float(method);
					},
					method -> {
						unboxLong(method);
						long2Double(method);
					}
			));
			FLOAT_VISITORS = register(Type.FLOAT_TYPE, new Visitors(
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(F)Ljava/lang/String;", false),
					DataVisitors::float2Boolean,
					method -> {
						float2Int(method);
						int2Char(method);
					},
					method -> {
						float2Int(method);
						int2Byte(method);
					},
					method -> {
						float2Int(method);
						int2Short(method);
					},
					DataVisitors::float2Int,
					DataVisitors::float2Long,
					null,
					DataVisitors::float2Double
			));
			FLOAT_WRAPPER_VISITORS = register(DataType.FLOAT_WRAPPER.getType(), new Visitors(
					method -> {
						unboxFloat(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(F)Ljava/lang/String;", false);
					},
					method -> {
						unboxFloat(method);
						float2Boolean(method);
					},
					method -> {
						unboxFloat(method);
						float2Int(method);
						int2Char(method);
					},
					method -> {
						unboxFloat(method);
						float2Int(method);
						int2Byte(method);
					},
					method -> {
						unboxFloat(method);
						float2Int(method);
						int2Short(method);
					},
					method -> {
						unboxFloat(method);
						float2Int(method);
					},
					method -> {
						unboxFloat(method);
						float2Long(method);
					},
					DataVisitors::unboxFloat,
					method -> {
						unboxFloat(method);
						float2Double(method);
					}
			));
			DOUBLE_VISITORS = register(Type.DOUBLE_TYPE, new Visitors(
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false),
					DataVisitors::double2Boolean,
					method -> {
						double2Int(method);
						int2Char(method);
					},
					method -> {
						double2Int(method);
						int2Byte(method);
					},
					method -> {
						double2Int(method);
						int2Short(method);
					},
					DataVisitors::double2Int,
					DataVisitors::double2Long,
					DataVisitors::double2Float,
					null
			));
			DOUBLE_WRAPPER_VISITORS = register(DataType.DOUBLE_WRAPPER.getType(), new Visitors(
					method -> {
						unboxDouble(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
					},
					method -> {
						unboxDouble(method);
						double2Boolean(method);
					},
					method -> {
						unboxDouble(method);
						double2Int(method);
						int2Char(method);
					},
					method -> {
						unboxDouble(method);
						double2Int(method);
						int2Byte(method);
					},
					method -> {
						unboxDouble(method);
						double2Int(method);
						int2Short(method);
					},
					method -> {
						unboxDouble(method);
						double2Int(method);
					},
					method -> {
						unboxDouble(method);
						double2Long(method);
					},
					method -> {
						unboxDouble(method);
						double2Float(method);
					},
					DataVisitors::unboxDouble
			));
			STRING_VISITORS = register(DataType.STRING.getType(), new Visitors(
					null,
					method -> {
						method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "isEmpty", "()Z", false);
						Label isNotEmpty = new Label();
						method.visitJumpInsn(IFEQ, isNotEmpty);
						method.visitInsn(ICONST_0);
						Label end = new Label();
						method.visitJumpInsn(GOTO, end);
						method.visitLabel(isNotEmpty);
						method.visitInsn(ICONST_1);
						method.visitLabel(end);
					},
					method -> {
						method.visitInsn(ICONST_0);
						method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
					},
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "parseByte", "(Ljava/lang/String;)B", false),
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "parseShort", "(Ljava/lang/String;)S", false),
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false),
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "parseLong", "(Ljava/lang/String;)J", false),
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F", false),
					method -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "parseDouble", "(Ljava/lang/String;)D", false)
			));
		}

		public static synchronized Visitors register(Type type, Visitors visitors) {
			MAP.put(type, visitors);
			return visitors;
		}

		@Nullable
		public static Visitors getVisitors(Type type) {
			return MAP.get(type);
		}

		record Visitors(@Nullable Consumer<MethodVisitor> stringConverter,
						@Nullable Consumer<MethodVisitor> booleanConverter,
						@Nullable Consumer<MethodVisitor> charConverter,
						@Nullable Consumer<MethodVisitor> byteConverter,
						@Nullable Consumer<MethodVisitor> shortConverter,
						@Nullable Consumer<MethodVisitor> intConverter,
						@Nullable Consumer<MethodVisitor> longConverter,
						@Nullable Consumer<MethodVisitor> floatConverter,
						@Nullable Consumer<MethodVisitor> doubleConverter) {
		}
	}
}
