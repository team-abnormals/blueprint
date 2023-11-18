package com.teamabnormals.blueprint.common.remolder.data;

import com.teamabnormals.blueprint.common.remolder.Remold;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Interface for providing methods to interact with various expressed forms of abstract data at the bytecode generation level.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface DataAccessor extends Consumer<Remold.Fields>, Opcodes {
	DataAccessor ROOT = new Parentless() {
		@Override
		public void set(Molding<?> molding, String owner, MethodVisitor method, DataAccessor value) {
			value.visit(molding, owner, method);
			method.visitVarInsn(ASTORE, 2);
		}

		@Override
		public void visit(Molding<?> molding, String owner, MethodVisitor method) {
			method.visitVarInsn(ALOAD, 2);
		}

		@Override
		public void accept(Remold.Fields fields) {}

		@Override
		public DataType getDataType() {
			return DataType.ELEMENT;
		}

		@Override
		public String toString() {
			return "ROOT_DATA_ACCESSOR";
		}
	};
	DataAccessor META = new Parentless() {
		@Override
		public void set(Molding<?> molding, String owner, MethodVisitor method, DataAccessor value) {
			value.visit(molding, owner, method);
			method.visitVarInsn(ASTORE, 3);
		}

		@Override
		public void visit(Molding<?> molding, String owner, MethodVisitor method) {
			method.visitVarInsn(ALOAD, 3);
		}

		@Override
		public void accept(Remold.Fields fields) {}

		@Override
		public DataType getDataType() {
			return DataType.ELEMENT;
		}

		@Override
		public String toString() {
			return "META_DATA_ACCESSOR";
		}
	};
	DataAccessor VARIABLES = new Parentless() {
		@Override
		public void set(Molding<?> molding, String owner, MethodVisitor method, DataAccessor value) {
			throw new UnsupportedOperationException("Cannot re-assign the variables object to a new value");
		}

		@Override
		public void visit(Molding<?> molding, String owner, MethodVisitor method) {
			method.visitVarInsn(ALOAD, 4);
		}

		@Override
		public void accept(Remold.Fields fields) {}

		@Override
		public DataType getDataType() {
			return DataType.ELEMENT;
		}

		@Override
		public String toString() {
			return "VARIABLES_DATA_ACCESSOR";
		}
	};
	BiConsumer<Molding<?>, MethodVisitor> NOOP_VISITOR = (molding, method) -> {};
	BiConsumer<Molding<?>, MethodVisitor> NUMBER_2_BOOLEAN_VISITOR = (molding, method) -> number2Boolean(method);
	BiConsumer<Molding<?>, MethodVisitor> NUMBER_2_CHAR_VISITOR = (molding, method) -> number2Char(method);
	BiConsumer<Molding<?>, MethodVisitor> NUMBER_2_BYTE_VISITOR = (molding, method) -> number2Byte(method);
	BiConsumer<Molding<?>, MethodVisitor> NUMBER_2_SHORT_VISITOR = (molding, method) -> number2Short(method);
	BiConsumer<Molding<?>, MethodVisitor> NUMBER_2_INT_VISITOR = (molding, method) -> number2Int(method);
	BiConsumer<Molding<?>, MethodVisitor> NUMBER_2_LONG_VISITOR = (molding, method) -> number2Long(method);
	BiConsumer<Molding<?>, MethodVisitor> NUMBER_2_FLOAT_VISITOR = (molding, method) -> number2Float(method);
	BiConsumer<Molding<?>, MethodVisitor> NUMBER_2_DOUBLE_VISITOR = (molding, method) -> number2Double(method);

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

	static void int2Long(MethodVisitor method) {
		method.visitInsn(I2L);
	}

	static void boxBoolean(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
	}

	static void boxChar(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
	}

	static void unboxBoolean(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
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

	static void stringLength(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
	}

	static BiConsumer<Molding<?>, MethodVisitor> stringValueOfVisitor(Type type) {
		TypeVisitors.Visitors visitorsForType = TypeVisitors.getVisitors(type);
		if (visitorsForType != null) return visitorsForType.stringConverter();
		int sort = type.getSort();
		if (sort == Type.OBJECT || sort == Type.ARRAY) {
			return (molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
		}
		throw new IllegalArgumentException("Don't know how to convert " + type + " to string");
	}

	static Parentless string(String value) {
		return new Parentless() {
			@Override
			public void visit(Molding<?> molding, String owner, MethodVisitor method) {
				method.visitLdcInsn(value);
			}

			@Override
			public void accept(Remold.Fields fields) {}

			@Override
			public DataType getDataType() {
				return DataType.STRING;
			}
		};
	}

	static Parentless booleanValue(boolean value) {
		return new Const(value ? ICONST_1 : ICONST_0, value, DataType.BOOLEAN);
	}

	static Parentless charValue(char value) {
		return value <= 5 ? new Const(value + ICONST_0, value, DataType.CHAR) : new Ldc(value, DataType.CHAR);
	}

	static Parentless byteValue(byte value) {
		return value >= -1 && value <= 5 ? new Const(value + ICONST_0, value, DataType.BYTE) : new Ldc(value, DataType.BYTE);
	}

	static Parentless shortValue(short value) {
		return value >= -1 && value <= 5 ? new Const(value + ICONST_0, value, DataType.SHORT) : new Ldc(value, DataType.SHORT);
	}

	static Parentless intValue(int value) {
		return value >= -1 && value <= 5 ? new Const(value + ICONST_0, value, DataType.INT) : new Ldc(value, DataType.INT);
	}

	static Parentless longValue(long value) {
		if (value == 0) return new Const(LCONST_0, value, DataType.LONG);
		if (value == 1) return new Const(LCONST_1, value, DataType.LONG);
		return new Ldc(value, DataType.LONG);
	}

	static Parentless floatValue(float value) {
		int opcode;
		if (value == 0.0F) {
			opcode = FCONST_0;
		} else if (value == 1.0F) {
			opcode = FCONST_1;
		} else if (value == 2.0F) {
			opcode = FCONST_2;
		} else return new Ldc(value, DataType.FLOAT);
		return new Const(opcode, value, DataType.FLOAT);
	}

	static Parentless doubleValue(double value) {
		if (value == 0.0D) return new Const(DCONST_0, value, DataType.DOUBLE);
		if (value == 1.0D) return new Const(DCONST_1, value, DataType.DOUBLE);
		return new Ldc(value, DataType.DOUBLE);
	}

	static Child objectElement(DataAccessor parent, DataAccessor keyAccessor) {
		return new Child(parent, keyAccessor, false);
	}

	static Child arrayElement(DataAccessor parent, DataAccessor indexAccessor) {
		return new Child(parent, indexAccessor, true);
	}

	// TODO: Override the other modification methods when local variable support gets added
	static Child lastArrayElement(DataAccessor parent) {
		return new Child(parent, DataAccessor.map(parent, DataType.INT, (molding, method) -> {
			molding.arrayLength(method);
			method.visitInsn(ICONST_1);
			method.visitInsn(ISUB);
		}), true) {
			@Override
			public void add(Molding<?> molding, String owner, MethodVisitor method, DataAccessor value) {
				molding.append(
						method,
						methodVisitor -> this.visitParent(molding, owner, methodVisitor),
						methodVisitor -> value.visit(molding, owner, methodVisitor)
				);
			}
		};
	}

	static ParentlessMapping map(DataAccessor dataAccessor, DataType type, BiConsumer<Molding<?>, MethodVisitor> visitor) {
		return new ParentlessMapping(dataAccessor, type, visitor);
	}

	static ParentlessInsertion insert(DataAccessor dataAccessor, DataType type, VisitorInserter inserter) {
		return new ParentlessInsertion(dataAccessor, type, inserter);
	}

	static Parentless data(DataAccessor dataAccessor) {
		DataType dataType = dataAccessor.getDataType();
		if (dataType.elementType() != DataType.ElementType.NONE)
			throw new UnsupportedOperationException("Cannot convert data element to data element");
		Type type = dataType.getTrueType(null);
		TypeVisitors.Visitors visitorsForType = TypeVisitors.getVisitors(type);
		VisitorInserter inserter;
		if (visitorsForType != null) {
			inserter = visitorsForType.elementInserter();
		} else {
			Class<?> clazz;
			try {
				clazz = Class.forName(type.getClassName());
			} catch (ClassNotFoundException | AssertionError exception) {
				throw new UnsupportedOperationException("Don't know how to convert " + type + " to data element");
			}
			if (!Number.class.isAssignableFrom(clazz))
				throw new UnsupportedOperationException("Don't know how to convert " + clazz + " to data element");
			inserter = Molding::numberElement;
		}
		return insert(dataAccessor, DataType.ELEMENT, inserter);
	}

	static ParentlessMapping str(DataAccessor dataAccessor) {
		DataType dataType = dataAccessor.getDataType();
		return map(dataAccessor, DataType.STRING, dataType.elementType() != DataType.ElementType.NONE ? Molding::elementToString : stringValueOfVisitor(dataType.getTrueType(null)));
	}

	static BiConsumer<Molding<?>, MethodVisitor> tryToGetNumberVisitorForType(Type type, String visitorType, BiConsumer<Molding<?>, MethodVisitor> numberVisitor) {
		Class<?> clazz;
		try {
			clazz = Class.forName(type.getClassName());
		} catch (ClassNotFoundException | AssertionError exception) {
			throw new UnsupportedOperationException("Don't know how to convert " + type + " to " + visitorType);
		}
		if (!Number.class.isAssignableFrom(clazz))
			throw new UnsupportedOperationException("Don't know how to convert " + clazz + " to " + visitorType);
		return numberVisitor;
	}

	static DataAccessor convertViaTypeVisitors(DataAccessor dataAccessor, DataType type, BiConsumer<Molding<?>, MethodVisitor> elementVisitor, Function<TypeVisitors.Visitors, BiConsumer<Molding<?>, MethodVisitor>> converterFromVisitors, BiConsumer<Molding<?>, MethodVisitor> numberVisitor) {
		DataType dataType = dataAccessor.getDataType();
		BiConsumer<Molding<?>, MethodVisitor> visitor;
		if (dataType.elementType() != DataType.ElementType.NONE) {
			visitor = elementVisitor;
		} else {
			Type trueType = dataType.getTrueType(null);
			TypeVisitors.Visitors visitorsForType = TypeVisitors.getVisitors(trueType);
			visitor = visitorsForType != null ? converterFromVisitors.apply(visitorsForType) : tryToGetNumberVisitorForType(trueType, DataType.toString(dataType), numberVisitor);
		}
		return map(dataAccessor, type, visitor);
	}

	static DataAccessor convertToBoolean(DataAccessor dataAccessor) {
		return convertViaTypeVisitors(dataAccessor, DataType.BOOLEAN, Molding::elementToBoolean, TypeVisitors.Visitors::booleanConverter, NUMBER_2_BOOLEAN_VISITOR);
	}

	static DataAccessor convertToBooleanWrapper(DataAccessor dataAccessor) {
		return map(convertToBoolean(dataAccessor), DataType.BOOLEAN_WRAPPER, (molding, method) -> boxBoolean(method));
	}

	static DataAccessor convertToChar(DataAccessor dataAccessor) {
		// Optimize for char(constant)
		if (dataAccessor instanceof Constant constant && constant.getValue() instanceof Number number) {
			if (number instanceof Integer) {
				return charValue((char) number.intValue());
			} else if (number instanceof Long) {
				return charValue((char) number.longValue());
			} else if (number instanceof Double) {
				return charValue((char) number.doubleValue());
			} else if (number instanceof Float) {
				return charValue((char) number.floatValue());
			} else if (number instanceof Short) {
				return charValue((char) number.shortValue());
			} else if (number instanceof Byte) {
				return charValue((char) number.byteValue());
			}
		}
		return convertViaTypeVisitors(dataAccessor, DataType.CHAR, Molding::elementToChar, TypeVisitors.Visitors::charConverter, NUMBER_2_CHAR_VISITOR);
	}

	static DataAccessor convertToCharWrapper(DataAccessor dataAccessor) {
		return map(convertToChar(dataAccessor), DataType.CHAR_WRAPPER, (molding, method) -> boxChar(method));
	}

	static DataAccessor convertToByte(DataAccessor dataAccessor) {
		// Optimize for byte(constant)
		if (dataAccessor instanceof Constant constant && constant.getValue() instanceof Number number)
			return byteValue(number.byteValue());
		return convertViaTypeVisitors(dataAccessor, DataType.BYTE, Molding::elementToByte, TypeVisitors.Visitors::byteConverter, NUMBER_2_BYTE_VISITOR);
	}

	static DataAccessor convertToByteWrapper(DataAccessor dataAccessor) {
		return map(convertToByte(dataAccessor), DataType.BYTE_WRAPPER, (molding, method) -> boxByte(method));
	}

	static DataAccessor convertToShort(DataAccessor dataAccessor) {
		// Optimize for short(constant)
		if (dataAccessor instanceof Constant constant && constant.getValue() instanceof Number number)
			return shortValue(number.shortValue());
		return convertViaTypeVisitors(dataAccessor, DataType.SHORT, Molding::elementToShort, TypeVisitors.Visitors::shortConverter, NUMBER_2_SHORT_VISITOR);
	}

	static DataAccessor convertToShortWrapper(DataAccessor dataAccessor) {
		return map(convertToShort(dataAccessor), DataType.SHORT_WRAPPER, (molding, method) -> boxShort(method));
	}

	static DataAccessor convertToInt(DataAccessor dataAccessor) {
		// Optimize for int(constant)
		if (dataAccessor instanceof Constant constant && constant.getValue() instanceof Number number)
			return intValue(number.intValue());
		return convertViaTypeVisitors(dataAccessor, DataType.INT, Molding::elementToInt, TypeVisitors.Visitors::intConverter, NUMBER_2_INT_VISITOR);
	}

	static DataAccessor convertToIntWrapper(DataAccessor dataAccessor) {
		return map(convertToInt(dataAccessor), DataType.INT_WRAPPER, (molding, method) -> boxInt(method));
	}

	static DataAccessor convertToLong(DataAccessor dataAccessor) {
		// Optimize for long(constant)
		if (dataAccessor instanceof Constant constant && constant.getValue() instanceof Number number)
			return longValue(number.longValue());
		return convertViaTypeVisitors(dataAccessor, DataType.LONG, Molding::elementToLong, TypeVisitors.Visitors::longConverter, NUMBER_2_LONG_VISITOR);
	}

	static DataAccessor convertToLongWrapper(DataAccessor dataAccessor) {
		return map(convertToLong(dataAccessor), DataType.LONG_WRAPPER, (molding, method) -> boxLong(method));
	}

	static DataAccessor convertToFloat(DataAccessor dataAccessor) {
		// Optimize for float(constant)
		if (dataAccessor instanceof Constant constant && constant.getValue() instanceof Number number)
			return floatValue(number.floatValue());
		return convertViaTypeVisitors(dataAccessor, DataType.FLOAT, Molding::elementToFloat, TypeVisitors.Visitors::floatConverter, NUMBER_2_FLOAT_VISITOR);
	}

	static DataAccessor convertToFloatWrapper(DataAccessor dataAccessor) {
		return map(convertToFloat(dataAccessor), DataType.FLOAT_WRAPPER, (molding, method) -> boxFloat(method));
	}

	static DataAccessor convertToDouble(DataAccessor dataAccessor) {
		// Optimize for float(constant)
		if (dataAccessor instanceof Constant constant && constant.getValue() instanceof Number number)
			return doubleValue(number.doubleValue());
		return convertViaTypeVisitors(dataAccessor, DataType.DOUBLE, Molding::elementToDouble, TypeVisitors.Visitors::doubleConverter, NUMBER_2_DOUBLE_VISITOR);
	}

	static DataAccessor convertToDoubleWrapper(DataAccessor dataAccessor) {
		return map(convertToDouble(dataAccessor), DataType.DOUBLE_WRAPPER, (molding, method) -> boxDouble(method));
	}

	default void add(Molding<?> molding, String owner, MethodVisitor method, DataAccessor value) {
		molding.add(
				method,
				methodVisitor -> this.visitParent(molding, owner, methodVisitor),
				methodVisitor -> this.visitIdentifier(molding, owner, methodVisitor),
				methodVisitor -> value.visit(molding, owner, methodVisitor),
				this.isIdentifierAnIndex()
		);
	}

	default void remove(Molding<?> molding, String owner, MethodVisitor method) {
		molding.remove(
				method,
				methodVisitor -> this.visitParent(molding, owner, methodVisitor),
				methodVisitor -> this.visitIdentifier(molding, owner, methodVisitor),
				this.isIdentifierAnIndex()
		);
	}

	default void set(Molding<?> molding, String owner, MethodVisitor method, DataAccessor value) {
		molding.set(
				method,
				methodVisitor -> this.visitParent(molding, owner, methodVisitor),
				methodVisitor -> this.visitIdentifier(molding, owner, methodVisitor),
				methodVisitor -> value.visit(molding, owner, methodVisitor),
				this.isIdentifierAnIndex()
		);
	}

	void visitParent(Molding<?> molding, String owner, MethodVisitor method);

	void visitIdentifier(Molding<?> molding, String owner, MethodVisitor method);

	boolean isIdentifierAnIndex();

	void visit(Molding<?> molding, String owner, MethodVisitor method);

	void accept(Remold.Fields fields);

	DataType getDataType();

	final class TypeVisitors {
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
			BiConsumer<Molding<?>, MethodVisitor> booleanToInt = (molding, method) -> boolean2Int(method);
			BOOLEAN_VISITORS = register(Type.BOOLEAN_TYPE, new Visitors(
					Molding::booleanElementFromPrimitive,
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Z)Ljava/lang/String;", false),
					NOOP_VISITOR,
					booleanToInt,
					booleanToInt,
					booleanToInt,
					booleanToInt,
					(molding, method) -> boolean2Long(method),
					(molding, method) -> boolean2Float(method),
					(molding, method) -> boolean2Double(method)
			));
			BiConsumer<Molding<?>, MethodVisitor> booleanWrapperToInt = (molding, method) -> {
				unboxBoolean(method);
				boolean2Int(method);
			};
			BOOLEAN_WRAPPER_VISITORS = register(DataType.BOOLEAN_TYPE, new Visitors(
					Molding::booleanElementFromWrapper,
					(molding, method) -> {
						unboxBoolean(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Z)Ljava/lang/String;", false);
					},
					(molding, method) -> unboxBoolean(method),
					booleanWrapperToInt,
					booleanWrapperToInt,
					booleanWrapperToInt,
					booleanWrapperToInt,
					(molding, method) -> {
						unboxBoolean(method);
						boolean2Long(method);
					},
					(molding, method) -> {
						unboxBoolean(method);
						boolean2Float(method);
					},
					(molding, method) -> {
						unboxBoolean(method);
						boolean2Double(method);
					}
			));
			BiConsumer<Molding<?>, MethodVisitor> ifneResult = (molding, method) -> ifneResult(method);
			BiConsumer<Molding<?>, MethodVisitor> i2b = (molding, method) -> int2Byte(method);
			BiConsumer<Molding<?>, MethodVisitor> i2s = (molding, method) -> int2Short(method);
			BiConsumer<Molding<?>, MethodVisitor> i2l = (molding, method) -> int2Long(method);
			BiConsumer<Molding<?>, MethodVisitor> i2f = (molding, method) -> int2Float(method);
			BiConsumer<Molding<?>, MethodVisitor> i2d = (molding, method) -> int2Double(method);
			CHAR_VISITORS = register(Type.CHAR_TYPE, new Visitors(
					Molding::characterElementFromPrimitive,
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(C)Ljava/lang/String;", false),
					ifneResult,
					NOOP_VISITOR,
					i2b,
					i2s,
					NOOP_VISITOR,
					i2l,
					i2f,
					i2d
			));
			BiConsumer<Molding<?>, MethodVisitor> unboxCharacter = (molding, method) -> unboxCharacter(method);
			CHAR_WRAPPER_VISITORS = register(DataType.CHARACTER_TYPE, new Visitors(
					Molding::characterElementFromWrapper,
					(molding, method) -> {
						unboxCharacter(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(C)Ljava/lang/String;", false);
					},
					(molding, method) -> {
						unboxCharacter(method);
						ifneResult(method);
					},
					unboxCharacter,
					(molding, method) -> {
						unboxCharacter(method);
						int2Byte(method);
					},
					(molding, method) -> {
						unboxCharacter(method);
						int2Short(method);
					},
					unboxCharacter,
					(molding, method) -> {
						unboxCharacter(method);
						int2Long(method);
					},
					(molding, method) -> {
						unboxCharacter(method);
						int2Float(method);
					},
					(molding, method) -> {
						unboxCharacter(method);
						int2Double(method);
					}
			));
			BiConsumer<Molding<?>, MethodVisitor> i2c = (molding, method) -> int2Char(method);
			BYTE_VISITORS = register(Type.BYTE_TYPE, new Visitors(
					Molding::byteElementFromPrimitive,
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false),
					ifneResult,
					i2c,
					NOOP_VISITOR,
					NOOP_VISITOR,
					NOOP_VISITOR,
					i2l,
					i2f,
					i2d
			));
			BiConsumer<Molding<?>, MethodVisitor> unboxByte = (molding, method) -> unboxByte(method);
			BYTE_WRAPPER_VISITORS = register(DataType.BYTE_TYPE, new Visitors(
					Molding::byteElementFromWrapper,
					(molding, method) -> {
						unboxByte(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
					},
					(molding, method) -> {
						unboxByte(method);
						ifneResult(method);
					},
					(molding, method) -> {
						unboxByte(method);
						int2Char(method);
					},
					unboxByte,
					unboxByte,
					unboxByte,
					(molding, method) -> {
						unboxByte(method);
						int2Long(method);
					},
					(molding, method) -> {
						unboxByte(method);
						int2Float(method);
					},
					(molding, method) -> {
						unboxByte(method);
						int2Double(method);
					}
			));
			SHORT_VISITORS = register(Type.SHORT_TYPE, new Visitors(
					Molding::shortElementFromPrimitive,
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false),
					ifneResult,
					i2c,
					i2b,
					NOOP_VISITOR,
					NOOP_VISITOR,
					i2l,
					i2f,
					i2d
			));
			BiConsumer<Molding<?>, MethodVisitor> unboxShort = (molding, method) -> unboxShort(method);
			SHORT_WRAPPER_VISITORS = register(DataType.SHORT_TYPE, new Visitors(
					Molding::shortElementFromWrapper,
					(molding, method) -> {
						unboxShort(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
					},
					(molding, method) -> {
						unboxShort(method);
						ifneResult(method);
					},
					(molding, method) -> {
						unboxShort(method);
						int2Char(method);
					},
					(molding, method) -> {
						unboxShort(method);
						int2Byte(method);
					},
					unboxShort,
					unboxShort,
					(molding, method) -> {
						unboxShort(method);
						int2Long(method);
					},
					(molding, method) -> {
						unboxShort(method);
						int2Float(method);
					},
					(molding, method) -> {
						unboxShort(method);
						int2Double(method);
					}
			));
			INT_VISITORS = register(Type.INT_TYPE, new Visitors(
					Molding::intElementFromPrimitive,
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false),
					ifneResult,
					i2c,
					i2b,
					i2s,
					NOOP_VISITOR,
					i2l,
					i2f,
					i2d
			));
			INT_WRAPPER_VISITORS = register(DataType.INTEGER_TYPE, new Visitors(
					Molding::intElementFromWrapper,
					(molding, method) -> {
						unboxInteger(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
					},
					(molding, method) -> {
						unboxInteger(method);
						ifneResult(method);
					},
					(molding, method) -> {
						unboxInteger(method);
						int2Char(method);
					},
					(molding, method) -> {
						unboxInteger(method);
						int2Byte(method);
					},
					(molding, method) -> {
						unboxInteger(method);
						int2Short(method);
					},
					(molding, method) -> unboxInteger(method),
					(molding, method) -> {
						unboxInteger(method);
						int2Long(method);
					},
					(molding, method) -> {
						unboxInteger(method);
						int2Float(method);
					},
					(molding, method) -> {
						unboxInteger(method);
						int2Double(method);
					}
			));
			LONG_VISITORS = register(Type.LONG_TYPE, new Visitors(
					Molding::longElementFromPrimitive,
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false),
					(molding, method) -> long2Boolean(method),
					(molding, method) -> {
						long2Int(method);
						int2Char(method);
					},
					(molding, method) -> {
						long2Int(method);
						int2Byte(method);
					},
					(molding, method) -> {
						long2Int(method);
						int2Short(method);
					},
					(molding, method) -> long2Int(method),
					NOOP_VISITOR,
					(molding, method) -> long2Float(method),
					(molding, method) -> long2Double(method)
			));
			LONG_WRAPPER_VISITORS = register(DataType.LONG_TYPE, new Visitors(
					Molding::longElementFromWrapper,
					(molding, method) -> {
						unboxLong(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false);
					},
					(molding, method) -> {
						unboxLong(method);
						long2Boolean(method);
					},
					(molding, method) -> {
						unboxLong(method);
						long2Int(method);
						int2Char(method);
					},
					(molding, method) -> {
						unboxLong(method);
						long2Int(method);
						int2Byte(method);
					},
					(molding, method) -> {
						unboxLong(method);
						long2Int(method);
						int2Short(method);
					},
					(molding, method) -> {
						unboxLong(method);
						long2Int(method);
					},
					(molding, method) -> unboxLong(method),
					(molding, method) -> {
						unboxLong(method);
						long2Float(method);
					},
					(molding, method) -> {
						unboxLong(method);
						long2Double(method);
					}
			));
			FLOAT_VISITORS = register(Type.FLOAT_TYPE, new Visitors(
					Molding::floatElementFromPrimitive,
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(F)Ljava/lang/String;", false),
					(molding, method) -> float2Boolean(method),
					(molding, method) -> {
						float2Int(method);
						int2Char(method);
					},
					(molding, method) -> {
						float2Int(method);
						int2Byte(method);
					},
					(molding, method) -> {
						float2Int(method);
						int2Short(method);
					},
					(molding, method) -> float2Int(method),
					(molding, method) -> float2Long(method),
					NOOP_VISITOR,
					(molding, method) -> float2Double(method)
			));
			FLOAT_WRAPPER_VISITORS = register(DataType.FLOAT_TYPE, new Visitors(
					Molding::floatElementFromWrapper,
					(molding, method) -> {
						unboxFloat(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(F)Ljava/lang/String;", false);
					},
					(molding, method) -> {
						unboxFloat(method);
						float2Boolean(method);
					},
					(molding, method) -> {
						unboxFloat(method);
						float2Int(method);
						int2Char(method);
					},
					(molding, method) -> {
						unboxFloat(method);
						float2Int(method);
						int2Byte(method);
					},
					(molding, method) -> {
						unboxFloat(method);
						float2Int(method);
						int2Short(method);
					},
					(molding, method) -> {
						unboxFloat(method);
						float2Int(method);
					},
					(molding, method) -> {
						unboxFloat(method);
						float2Long(method);
					},
					(molding, method) -> unboxFloat(method),
					(molding, method) -> {
						unboxFloat(method);
						float2Double(method);
					}
			));
			DOUBLE_VISITORS = register(Type.DOUBLE_TYPE, new Visitors(
					Molding::doubleElementFromPrimitive,
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false),
					(molding, method) -> double2Boolean(method),
					(molding, method) -> {
						double2Int(method);
						int2Char(method);
					},
					(molding, method) -> {
						double2Int(method);
						int2Byte(method);
					},
					(molding, method) -> {
						double2Int(method);
						int2Short(method);
					},
					(molding, method) -> double2Int(method),
					(molding, method) -> double2Long(method),
					(molding, method) -> double2Float(method),
					NOOP_VISITOR
			));
			DOUBLE_WRAPPER_VISITORS = register(DataType.DOUBLE_TYPE, new Visitors(
					Molding::doubleElementFromWrapper,
					(molding, method) -> {
						unboxDouble(method);
						method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
					},
					(molding, method) -> {
						unboxDouble(method);
						double2Boolean(method);
					},
					(molding, method) -> {
						unboxDouble(method);
						double2Int(method);
						int2Char(method);
					},
					(molding, method) -> {
						unboxDouble(method);
						double2Int(method);
						int2Byte(method);
					},
					(molding, method) -> {
						unboxDouble(method);
						double2Int(method);
						int2Short(method);
					},
					(molding, method) -> {
						unboxDouble(method);
						double2Int(method);
					},
					(molding, method) -> {
						unboxDouble(method);
						double2Long(method);
					},
					(molding, method) -> {
						unboxDouble(method);
						double2Float(method);
					},
					(molding, method) -> unboxDouble(method)
			));
			STRING_VISITORS = register(DataType.STRING_TYPE, new Visitors(
					Molding::stringElement,
					NOOP_VISITOR,
					(molding, method) -> {
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
					(molding, method) -> {
						method.visitInsn(ICONST_0);
						method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
					},
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "parseByte", "(Ljava/lang/String;)B", false),
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "parseShort", "(Ljava/lang/String;)S", false),
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false),
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "parseLong", "(Ljava/lang/String;)J", false),
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F", false),
					(molding, method) -> method.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "parseDouble", "(Ljava/lang/String;)D", false)
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

		record Visitors(VisitorInserter elementInserter,
						BiConsumer<Molding<?>, MethodVisitor> stringConverter,
						BiConsumer<Molding<?>, MethodVisitor> booleanConverter,
						BiConsumer<Molding<?>, MethodVisitor> charConverter,
						BiConsumer<Molding<?>, MethodVisitor> byteConverter,
						BiConsumer<Molding<?>, MethodVisitor> shortConverter,
						BiConsumer<Molding<?>, MethodVisitor> intConverter,
						BiConsumer<Molding<?>, MethodVisitor> longConverter,
						BiConsumer<Molding<?>, MethodVisitor> floatConverter,
						BiConsumer<Molding<?>, MethodVisitor> doubleConverter) {
		}
	}

	@FunctionalInterface
	interface VisitorInserter {
		void insert(Molding<?> molding, MethodVisitor method, Consumer<MethodVisitor> visitor);
	}

	interface Parentless extends DataAccessor {
		@Override
		default void visitParent(Molding<?> molding, String owner, MethodVisitor method) {}

		@Override
		default void visitIdentifier(Molding<?> molding, String owner, MethodVisitor method) {}

		@Override
		default boolean isIdentifierAnIndex() {
			return false;
		}
	}

	abstract class Constant implements Parentless {
		private final Object value;
		private final DataType type;

		public Constant(Object value, DataType type) {
			this.value = value;
			this.type = type;
		}

		@Override
		public void accept(Remold.Fields fields) {}

		public Object getValue() {
			return this.value;
		}

		@Override
		public DataType getDataType() {
			return this.type;
		}
	}

	final class Const extends Constant {
		private final int opcode;

		public Const(int opcode, Object value, DataType type) {
			super(value, type);
			this.opcode = opcode;
		}

		@Override
		public void visit(Molding<?> molding, String owner, MethodVisitor method) {
			method.visitInsn(this.opcode);
		}
	}

	final class Ldc extends Constant {
		public Ldc(Object value, DataType type) {
			super(value, type);
		}

		@Override
		public void visit(Molding<?> molding, String owner, MethodVisitor method) {
			method.visitLdcInsn(this.getValue());
		}
	}

	class Child implements DataAccessor {
		private final DataAccessor parent;
		private final DataAccessor identifier;
		private final boolean isIdentifierAnIndex;

		public Child(DataAccessor parent, DataAccessor identifier, boolean isIdentifierAnIndex) {
			this.parent = parent;
			this.identifier = identifier;
			this.isIdentifierAnIndex = isIdentifierAnIndex;
		}

		@Override
		public void visitParent(Molding<?> molding, String owner, MethodVisitor method) {
			this.parent.visit(molding, owner, method);
		}

		@Override
		public void visitIdentifier(Molding<?> molding, String owner, MethodVisitor method) {
			this.identifier.visit(molding, owner, method);
		}

		@Override
		public boolean isIdentifierAnIndex() {
			return this.isIdentifierAnIndex;
		}

		@Override
		public void visit(Molding<?> molding, String owner, MethodVisitor method) {
			molding.get(
					method,
					methodVisitor -> this.visitParent(molding, owner, methodVisitor),
					methodVisitor -> this.visitIdentifier(molding, owner, methodVisitor),
					this.isIdentifierAnIndex()
			);
		}

		@Override
		public void accept(Remold.Fields fields) {
			this.parent.accept(fields);
			this.identifier.accept(fields);
		}

		@Override
		public DataType getDataType() {
			return DataType.ELEMENT;
		}
	}

	record ParentlessMapping(DataAccessor accessor, DataType type, BiConsumer<Molding<?>, MethodVisitor> visitor) implements Parentless {
		@Override
		public void visit(Molding<?> molding, String owner, MethodVisitor method) {
			this.accessor.visit(molding, owner, method);
			this.visitor.accept(molding, method);
		}

		@Override
		public void accept(Remold.Fields fields) {
			this.accessor.accept(fields);
		}

		@Override
		public DataType getDataType() {
			return this.type;
		}
	}

	record ParentlessInsertion(DataAccessor dataAccessor, DataType type, VisitorInserter inserter) implements Parentless {
		@Override
		public void visit(Molding<?> molding, String owner, MethodVisitor method) {
			this.inserter.insert(molding, method, methodVisitor -> this.dataAccessor.visit(molding, owner, methodVisitor));
		}

		@Override
		public void accept(Remold.Fields fields) {
			this.dataAccessor.accept(fields);
		}

		@Override
		public DataType getDataType() {
			return this.type;
		}
	}
}
