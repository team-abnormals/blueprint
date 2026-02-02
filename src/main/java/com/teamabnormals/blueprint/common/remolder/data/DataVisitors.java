package com.teamabnormals.blueprint.common.remolder.data;

import com.teamabnormals.blueprint.common.remolder.util.DataExpression;
import com.teamabnormals.blueprint.common.remolder.util.DiscretionaryLabel;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.teamabnormals.blueprint.common.remolder.data.DataType.BOOLEAN;
import static org.objectweb.asm.Opcodes.*;

/**
 * Utility class for {@link DataVisitor} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class DataVisitors {
	public static final Const NULL = new Const(DataType.VOID, null, ACONST_NULL);
	public static final Const FALSE = new Const(DataType.BOOLEAN, false, ICONST_0);
	public static final Const TRUE = new Const(DataType.BOOLEAN, true, ICONST_1);
	public static final Handle MAKE_CONCAT_WITH_CONSTANTS = new Handle(
			H_INVOKESTATIC,
			"java/lang/invoke/StringConcatFactory",
			"makeConcatWithConstants",
			"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;",
			false
	);

	public static Const booleanValue(boolean value) {
		return value ? TRUE : FALSE;
	}

	public static DataVisitor charValue(char value) {
		if (value <= 5) return new Const(DataType.CHAR, value, value + ICONST_0);
		if (value <= Byte.MAX_VALUE) return new IntPush(DataType.CHAR, BIPUSH, (int) value);
		if (value <= Short.MAX_VALUE) return new IntPush(DataType.CHAR, SIPUSH, (int) value);
		return new Ldc(DataType.CHAR, value);
	}

	public static DataVisitor byteValue(byte value) {
		return value >= -1 && value <= 5 ? new Const(DataType.BYTE, value, value + ICONST_0) : new IntPush(DataType.BYTE, BIPUSH, value);
	}

	public static DataVisitor shortValue(short value) {
		if (value >= -1 && value <= 5) return new Const(DataType.SHORT, value, value + ICONST_0);
		if (value == (byte) value) return new IntPush(DataType.SHORT, BIPUSH, value);
		return new IntPush(DataType.SHORT, SIPUSH, value);
	}

	public static DataVisitor intValue(int value) {
		if (value >= -1 && value <= 5) return new Const(DataType.INT, value, value + ICONST_0);
		if (value == (byte) value) return new IntPush(DataType.INT, BIPUSH, value);
		if (value == (short) value) return new IntPush(DataType.INT, SIPUSH, value);
		return new Ldc(DataType.INT, value);
	}

	public static DataVisitor longValue(long value) {
		if (value == 0) return new Const(DataType.LONG, value, LCONST_0);
		if (value == 1) return new Const(DataType.LONG, value, LCONST_1);
		return new Ldc(DataType.LONG, value);
	}

	public static DataVisitor floatValue(float value) {
		int opcode;
		if (Float.floatToIntBits(value) == 0) {
			opcode = FCONST_0;
		} else if (value == 1.0F) {
			opcode = FCONST_1;
		} else if (value == 2.0F) {
			opcode = FCONST_2;
		} else return new Ldc(DataType.FLOAT, value);
		return new Const(DataType.FLOAT, value, opcode);
	}

	public static DataVisitor doubleValue(double value) {
		if (Double.doubleToLongBits(value) == 0L) return new Const(DataType.DOUBLE, value, DCONST_0);
		if (value == 1.0D) return new Const(DataType.DOUBLE, value, DCONST_1);
		return new Ldc(DataType.DOUBLE, value);
	}

	public static DataVisitor string(String value) {
		return new Ldc(DataType.STRING, value);
	}

	public static void visitInsn(Molding molding, DataVisitor visitor, int opcode) {
		visitor.visit(molding);
		molding.visitInsn(opcode);
	}

	public static void ifneResult(MethodVisitor method) {
		Label trueResult = new Label();
		method.visitJumpInsn(IFNE, trueResult);
		method.visitInsn(ICONST_0);
		Label end = new Label();
		method.visitJumpInsn(GOTO, end);
		method.visitLabel(trueResult);
		method.visitInsn(ICONST_1);
		method.visitLabel(end);
	}

	public static void boolean2Int(MethodVisitor method) {
		method.visitInsn(ICONST_0);
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "compare", "(ZZ)I", false);
	}

	public static void boolean2Long(MethodVisitor method) {
		Label trueLabel = new Label();
		method.visitJumpInsn(IFNE, trueLabel);
		method.visitInsn(LCONST_0);
		Label endLabel = new Label();
		method.visitJumpInsn(GOTO, endLabel);
		method.visitLabel(trueLabel);
		method.visitInsn(LCONST_1);
		method.visitLabel(endLabel);
	}

	public static void boolean2Float(MethodVisitor method) {
		Label trueLabel = new Label();
		method.visitJumpInsn(IFNE, trueLabel);
		method.visitInsn(FCONST_0);
		Label endLabel = new Label();
		method.visitJumpInsn(GOTO, endLabel);
		method.visitLabel(trueLabel);
		method.visitInsn(FCONST_1);
		method.visitLabel(endLabel);
	}

	public static void boolean2Double(MethodVisitor method) {
		Label trueLabel = new Label();
		method.visitJumpInsn(IFNE, trueLabel);
		method.visitInsn(DCONST_0);
		Label endLabel = new Label();
		method.visitJumpInsn(GOTO, endLabel);
		method.visitLabel(trueLabel);
		method.visitInsn(DCONST_1);
		method.visitLabel(endLabel);
	}

	public static void long2Boolean(MethodVisitor method) {
		method.visitInsn(LCONST_0);
		method.visitInsn(LCMP);
		ifneResult(method);
	}

	public static void float2Boolean(MethodVisitor method) {
		method.visitInsn(FCONST_0);
		method.visitInsn(FCMPL);
		ifneResult(method);
	}

	public static void double2Boolean(MethodVisitor method) {
		method.visitInsn(DCONST_0);
		method.visitInsn(DCMPL);
		ifneResult(method);
	}

	public static void int2Long(MethodVisitor method) {
		method.visitInsn(I2L);
	}

	public static void int2Float(MethodVisitor method) {
		method.visitInsn(I2F);
	}

	public static void int2Double(MethodVisitor method) {
		method.visitInsn(I2D);
	}

	public static void int2Byte(MethodVisitor method) {
		method.visitInsn(I2B);
	}

	public static void int2Char(MethodVisitor method) {
		method.visitInsn(I2C);
	}

	public static void int2Short(MethodVisitor method) {
		method.visitInsn(I2S);
	}

	public static void float2Int(MethodVisitor method) {
		method.visitInsn(F2I);
	}

	public static void float2Long(MethodVisitor method) {
		method.visitInsn(F2L);
	}

	public static void float2Double(MethodVisitor method) {
		method.visitInsn(F2D);
	}

	public static void long2Int(MethodVisitor method) {
		method.visitInsn(L2I);
	}

	public static void long2Float(MethodVisitor method) {
		method.visitInsn(L2F);
	}

	public static void long2Double(MethodVisitor method) {
		method.visitInsn(L2D);
	}

	public static void double2Int(MethodVisitor method) {
		method.visitInsn(D2I);
	}

	public static void double2Long(MethodVisitor method) {
		method.visitInsn(D2L);
	}

	public static void double2Float(MethodVisitor method) {
		method.visitInsn(D2F);
	}

	public static void number2Boolean(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
		double2Boolean(method);
	}

	public static void number2Char(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "charValue", "()C", false);
	}

	public static void number2Byte(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B", false);
	}

	public static void number2Short(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S", false);
	}

	public static void number2Int(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
	}

	public static void number2Long(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
	}

	public static void number2Float(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
	}

	public static void number2Double(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
	}

	public static void boxBoolean(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
	}

	public static void unboxBoolean(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
	}

	public static void boxChar(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
	}

	public static void unboxCharacter(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
	}

	public static void boxByte(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
	}

	public static void unboxByte(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
	}

	public static void boxShort(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
	}

	public static void unboxShort(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
	}

	public static void boxInt(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
	}

	public static void unboxInteger(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
	}

	public static void boxLong(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
	}

	public static void unboxLong(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
	}

	public static void boxFloat(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
	}

	public static void unboxFloat(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
	}

	public static void boxDouble(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
	}

	public static void unboxDouble(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
	}

	public static void boxPrimitive(Molding molding, Class<?> clazz) {
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

	public static DataVisitor negate(DataVisitor visitor) {
		return molding -> {
			var dataType = visitor.visit(molding);
			if (dataType.isSmallInteger()) {
				dataType.unbox(molding);
				molding.visitInsn(INEG);
				return DataType.INT;
			} else if (dataType.isDouble()) {
				dataType.unbox(molding);
				molding.visitInsn(DNEG);
				return DataType.DOUBLE;
			} else if (dataType.isFloat()) {
				dataType.unbox(molding);
				molding.visitInsn(FNEG);
				return DataType.FLOAT;
			} else if (dataType.isLong()) {
				dataType.unbox(molding);
				molding.visitInsn(LNEG);
				return DataType.LONG;
			}
			throw new UnsupportedOperationException("Cannot negate type: " + dataType);
		};
	}

	public static DataVisitor logicalComplement(DataVisitor visitor) {
		if (visitor instanceof BinaryOp binaryOp && (binaryOp.op == DataExpression.BinaryOp.AND || binaryOp.op == DataExpression.BinaryOp.OR))
			return new LogicalComplement(visitor, LogicalComplement.Type.CONNECTIVE);
		return new LogicalComplement(visitor, isLogicalVisitor(visitor) ? LogicalComplement.Type.VISITOR : LogicalComplement.Type.LITERAL);
	}

	public static DataVisitor integerComplement(DataVisitor visitor) {
		return molding -> {
			var dataType = visitor.visit(molding);
			if (dataType.isSmallInteger()) {
				dataType.unbox(molding);
				molding.visitInsn(ICONST_M1);
				molding.visitInsn(IXOR);
				return DataType.INT;
			} else if (dataType.isLong()) {
				dataType.unbox(molding);
				molding.visitLdcInsn(-1L);
				molding.visitInsn(LXOR);
				return DataType.LONG;
			} else throw new UnsupportedOperationException("Cannot numerically complement type: " + dataType);
		};
	}

	public static void stringLength(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
	}

	public static DataVisitor element(DataVisitor visitor) {
		return molding -> molding.element(visitor);
	}

	public static DataVisitor elementalTest(DataVisitor visitor, BiConsumer<Molding, DataType<?>> consumer) {
		return molding -> {
			consumer.accept(molding, visitor.visit(molding));
			return BOOLEAN;
		};
	}

	public static DataVisitor str(DataVisitor visitor) {
		return molding -> {
			var dataType = visitor.visit(molding);
			if (dataType == DataType.STRING) return DataType.STRING;
			try {
				molding.toString(dataType);
			} catch (UnsupportedOperationException unsupported) {
				var type = dataType.getType();
				TypeVisitors.Visitors visitorsForType = TypeVisitors.getVisitors(type);
				if (visitorsForType != null) {
					var converter = visitorsForType.stringConverter();
					if (converter != null) converter.accept(molding);
					return DataType.STRING;
				}
				int sort = type.getSort();
				if (sort == Type.OBJECT || sort == Type.ARRAY) {
					molding.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
				}
				throw new UnsupportedOperationException("Don't know how to convert " + type + " to string");
			}
			return DataType.STRING;
		};
	}

	public static void convertViaTypeVisitors(Molding molding, DataType<?> fromType, DataType<?> type, Function<TypeVisitors.Visitors, Consumer<? super Molding>> converterFromVisitors, Consumer<Molding> numberVisitor) {
		if (fromType == type) return;
		TypeVisitors.Visitors visitorsForType = TypeVisitors.getVisitors(fromType.getType());
		if (visitorsForType != null) {
			var converter = converterFromVisitors.apply(visitorsForType);
			if (converter != null) converter.accept(molding);
		} else if (Number.class.isAssignableFrom(fromType.getClazz())) {
			numberVisitor.accept(molding);
		} else {
			try {
				molding.convert(fromType, type);
			} catch (UnsupportedOperationException unsupported) {
				throw new UnsupportedOperationException("Don't know how to convert " + fromType + " to " + type);
			}
		}
	}

	public static void convertToBoolean(Molding molding, DataType<?> fromType) {
		convertViaTypeVisitors(molding, fromType, DataType.BOOLEAN, TypeVisitors.Visitors::booleanConverter, DataVisitors::number2Boolean);
	}

	public static DataVisitor convertToBoolean(DataVisitor visitor) {
		return molding -> {
			convertToBoolean(molding, visitor.visit(molding));
			return DataType.BOOLEAN;
		};
	}

	public static DataVisitor convertToBooleanWrapper(DataVisitor visitor) {
		return convertToBoolean(visitor).then(DataVisitors::boxBoolean, DataType.BOOLEAN_WRAPPER);
	}

	public static void convertToChar(Molding molding, DataType<?> fromType) {
		convertViaTypeVisitors(molding, fromType, DataType.CHAR, TypeVisitors.Visitors::charConverter, DataVisitors::number2Char);
	}

	public static DataVisitor convertToChar(DataVisitor visitor) {
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
		return molding -> {
			convertToChar(molding, visitor.visit(molding));
			return DataType.CHAR;
		};
	}

	public static DataVisitor convertToCharWrapper(DataVisitor visitor) {
		return convertToChar(visitor).then(DataVisitors::boxChar, DataType.CHARACTER_WRAPPER);
	}

	public static void convertToByte(Molding molding, DataType<?> fromType) {
		convertViaTypeVisitors(molding, fromType, DataType.BYTE, TypeVisitors.Visitors::byteConverter, DataVisitors::number2Byte);
	}

	public static DataVisitor convertToByte(DataVisitor visitor) {
		// Optimize for (byte) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return byteValue(number.byteValue());
		return molding -> {
			convertToByte(molding, visitor.visit(molding));
			return DataType.BYTE;
		};
	}

	public static DataVisitor convertToByteWrapper(DataVisitor visitor) {
		return convertToByte(visitor).then(DataVisitors::boxByte, DataType.BYTE_WRAPPER);
	}

	public static void convertToShort(Molding molding, DataType<?> fromType) {
		convertViaTypeVisitors(molding, fromType, DataType.SHORT, TypeVisitors.Visitors::shortConverter, DataVisitors::number2Short);
	}

	public static DataVisitor convertToShort(DataVisitor visitor) {
		// Optimize for (short) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return shortValue(number.shortValue());
		return molding -> {
			convertToShort(molding, visitor.visit(molding));
			return DataType.SHORT;
		};
	}

	public static DataVisitor convertToShortWrapper(DataVisitor visitor) {
		return convertToShort(visitor).then(DataVisitors::boxShort, DataType.SHORT_WRAPPER);
	}

	public static void convertToInt(Molding molding, DataType<?> fromType) {
		convertViaTypeVisitors(molding, fromType, DataType.INT, TypeVisitors.Visitors::intConverter, DataVisitors::number2Int);
	}

	public static DataVisitor convertToInt(DataVisitor visitor) {
		// Optimize for (int) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return intValue(number.intValue());
		return molding -> {
			convertToInt(molding, visitor.visit(molding));
			return DataType.INT;
		};
	}

	public static DataVisitor convertToIntWrapper(DataVisitor visitor) {
		return convertToInt(visitor).then(DataVisitors::boxInt, DataType.INTEGER_WRAPPER);
	}

	public static void convertToLong(Molding molding, DataType<?> fromType) {
		convertViaTypeVisitors(molding, fromType, DataType.LONG, TypeVisitors.Visitors::longConverter, DataVisitors::number2Long);
	}

	public static DataVisitor convertToLong(DataVisitor visitor) {
		// Optimize for (long) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return longValue(number.longValue());
		return molding -> {
			convertToLong(molding, visitor.visit(molding));
			return DataType.LONG;
		};
	}

	public static DataVisitor convertToLongWrapper(DataVisitor visitor) {
		return convertToLong(visitor).then(DataVisitors::boxLong, DataType.LONG_WRAPPER);
	}

	public static void convertToFloat(Molding molding, DataType<?> fromType) {
		convertViaTypeVisitors(molding, fromType, DataType.FLOAT, TypeVisitors.Visitors::floatConverter, DataVisitors::number2Float);
	}

	public static DataVisitor convertToFloat(DataVisitor visitor) {
		// Optimize for (float) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return floatValue(number.floatValue());
		return molding -> {
			convertToFloat(molding, visitor.visit(molding));
			return DataType.FLOAT;
		};
	}

	public static DataVisitor convertToFloatWrapper(DataVisitor visitor) {
		return convertToFloat(visitor).then(DataVisitors::boxFloat, DataType.FLOAT_WRAPPER);
	}

	public static void convertToDouble(Molding molding, DataType<?> fromType) {
		convertViaTypeVisitors(molding, fromType, DataType.DOUBLE, TypeVisitors.Visitors::doubleConverter, DataVisitors::number2Double);
	}

	public static DataVisitor convertToDouble(DataVisitor visitor) {
		// Optimize for (double) constant
		if (visitor instanceof Constant constant && constant.value() instanceof Number number)
			return doubleValue(number.doubleValue());
		return molding -> {
			convertToDouble(molding, visitor.visit(molding));
			return DataType.DOUBLE;
		};
	}

	public static DataVisitor convertToDoubleWrapper(DataVisitor visitor) {
		return convertToDouble(visitor).then(DataVisitors::boxDouble, DataType.DOUBLE_WRAPPER);
	}

	public static void convertToString(Molding molding, DataType<?> fromType) {
		convertViaTypeVisitors(molding, fromType, DataType.STRING, TypeVisitors.Visitors::stringConverter, m -> {
			molding.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
		});
	}

	public static DataVisitor convertToString(DataVisitor visitor) {
		return molding -> {
			convertViaTypeVisitors(molding, visitor.visit(molding), DataType.STRING, TypeVisitors.Visitors::stringConverter, m -> {
				molding.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
			});
			return DataType.STRING;
		};
	}

	@Nullable
	public static DataVisitor tryToConvert(DataVisitor visitor, Class<?> type) {
		if (type.isPrimitive()) {
			if (type == int.class) {
				return convertToInt(visitor);
			} else if (type == long.class) {
				return convertToLong(visitor);
			} else if (type == double.class) {
				return convertToDouble(visitor);
			} else if (type == float.class) {
				return convertToFloat(visitor);
			} else if (type == short.class) {
				return convertToShort(visitor);
			} else if (type == byte.class) {
				return convertToByte(visitor);
			} else if (type == char.class) {
				return convertToChar(visitor);
			}
			return convertToBoolean(visitor);
		} else if (type == Integer.class) {
			return convertToIntWrapper(visitor);
		} else if (type == Long.class) {
			return convertToLongWrapper(visitor);
		} else if (type == Double.class) {
			return convertToDoubleWrapper(visitor);
		} else if (type == Float.class) {
			return convertToFloatWrapper(visitor);
		} else if (type == Short.class) {
			return convertToShortWrapper(visitor);
		} else if (type == Byte.class) {
			return convertToByteWrapper(visitor);
		} else if (type == Character.class) {
			return convertToCharWrapper(visitor);
		} else if (type == Boolean.class) {
			return convertToBooleanWrapper(visitor);
		}
		return null;
	}

	public static DataVisitor cast(DataVisitor visitor, DataType<?> type) {
		var toClazz = type.getClazz();
		if (toClazz.isPrimitive())
			return tryToConvert(visitor, type.getClazz());
		if (toClazz == String.class)
			return convertToString(visitor);
		return molding -> {
			var visitorClazz = visitor.visit(molding).getClazz();
			if (visitorClazz.isPrimitive())
				throw new UnsupportedOperationException("Can't cast primitive type to non-primitive type");
			if (toClazz.isAssignableFrom(visitorClazz))
				return type;
			if (shouldCheckcast(visitorClazz, toClazz)) {
				molding.visitTypeInsn(CHECKCAST, type.getInternalName());
				return type;
			}
			throw new UnsupportedOperationException("Can't cast " + visitorClazz + " to " + toClazz);
		};
	}

	public static boolean shouldCheckcast(Class<?> fromClazz, Class<?> toClazz) {
		if (fromClazz.isAssignableFrom(toClazz) || toClazz.isInterface())
			return true;
		var componentType = toClazz.getComponentType();
		if (componentType != null) {
			var fromComponentType = fromClazz.getComponentType();
			if (fromComponentType == null)
				return false;
			if (componentType.isPrimitive())
				return toClazz == fromClazz;
			return canCastArrayComponents(fromComponentType, componentType);
		} else return false;
	}

	private static boolean canCastArrayComponents(Class<?> fromClazz, Class<?> toClazz) {
		if (toClazz.isAssignableFrom(fromClazz))
			return true;
		if (fromClazz.isAssignableFrom(toClazz))
			return true;
		var componentType = toClazz.getComponentType();
		if (componentType != null) {
			var visitorComponentType = fromClazz.getComponentType();
			if (visitorComponentType == null)
				return false;
			if (componentType.isPrimitive())
				return toClazz == fromClazz;
			return canCastArrayComponents(visitorComponentType, componentType);
		} else return toClazz.isInterface(); // For rare cases like (Serializable[])
	}

	public static void assertCanAcceptInstanceof(DataType<?> type) {
		if (type.getClazz().isPrimitive())
			throw new UnsupportedOperationException("Expected " + type + " to be non-primitive to use instanceof");
	}

	public static boolean isLogicalVisitor(DataVisitor visitor) {
		if (visitor instanceof LogicalComplement) return true;
		if (!(visitor instanceof BinaryOp op)) return false;
		return DataExpression.BinaryOp.LOGICAL_OPERATORS.contains(op.op());
	}

	public static void unboxBoolean(DataVisitor visitor, Molding molding) {
		var rightType = visitor.visit(molding);
		if (!rightType.isBoolean())
			throw new UnsupportedOperationException("Logical operation cannot apply to non-booleans");
		rightType.unbox(molding);
	}

	public interface Constant extends DataVisitor {
		Object value();

		DataType<?> type();
	}

	public record Ldc(DataType<?> type, Object value) implements Constant {
		@Override
		public DataType<?> visit(Molding molding) {
			molding.visitLdcInsn(this.value);
			return this.type;
		}

		@Override
		public DataType<?> type() {
			return this.type;
		}
	}

	public record Const(DataType<?> type, Object value, int opcode) implements Constant {
		@Override
		public DataType<?> visit(Molding molding) {
			molding.visitInsn(this.opcode);
			return this.type;
		}

		@Override
		public DataType<?> type() {
			return this.type;
		}
	}

	public record IntPush(DataType<?> type, int opcode, Number value) implements Constant {
		@Override
		public DataType<?> visit(Molding molding) {
			molding.visitIntInsn(this.opcode, this.value.intValue());
			return this.type;
		}

		@Override
		public DataType<?> type() {
			return this.type;
		}
	}

	public record Pipe(DataVisitor visitor, Consumer<? super Molding> consumer, DataType<?> type) implements DataVisitor {
		@Override
		public DataType<?> visit(Molding molding) {
			this.visitor.visit(molding);
			this.consumer.accept(molding);
			return this.type;
		}
	}

	public record LogicalComplement(DataVisitor visitor, Type type) implements DataVisitor {
		@Override
		public DataType<?> visit(Molding molding) {
			if (this.type == Type.LITERAL) {
				var buffer = molding.createBuffer();
				var dataType = this.visitor.visit(buffer);
				if (dataType.isBoolean()) {
					dataType.unbox(molding);
				} else throw new UnsupportedOperationException("Cannot logically complement type: " + dataType);
				buffer.accept(molding);
				Label continueLabel = molding.getTrueLabel();
				Label exitLabel = molding.getExitLabel();
				if (continueLabel != null) {
					if (exitLabel == null || molding.shortCircuitsWithOr()) {
						molding.visitJumpInsn(IFEQ, continueLabel);
						DiscretionaryLabel.tryToMark(continueLabel);
					} else {
						molding.visitJumpInsn(IFNE, exitLabel);
						DiscretionaryLabel.tryToMark(exitLabel);
					}
				} else if (exitLabel != null) {
					molding.visitJumpInsn(IFNE, exitLabel);
					DiscretionaryLabel.tryToMark(exitLabel);
				} else {
					Label trueResult = new Label();
					molding.visitJumpInsn(IFNE, trueResult);
					molding.visitInsn(ICONST_1);
					Label end = new Label();
					molding.visitJumpInsn(GOTO, end);
					molding.visitLabel(trueResult);
					molding.visitInsn(ICONST_0);
					molding.visitLabel(end);
				}
			} else {
				Molding buffer = molding.negate(this.type == Type.CONNECTIVE);
				this.visitor.visit(buffer);
				buffer.accept(molding);
			}
			return DataType.BOOLEAN;
		}

		public enum Type {
			LITERAL,
			CONNECTIVE,
			VISITOR
		}
	}

	public record BinaryOp(DataExpression.BinaryOp op, DataVisitor left, DataVisitor right) implements DataVisitor {
		@Override
		public DataType<?> visit(Molding molding) {
			return this.op.visit(molding, this.left, this.right);
		}
	}

	public static final class TypeVisitors {
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

		public record Visitors(@Nullable Consumer<MethodVisitor> stringConverter,
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
