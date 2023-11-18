package com.teamabnormals.blueprint.common.remolder.data;

import org.objectweb.asm.Type;

/**
 * The class for representing format-relative data types.
 *
 * @author SmellyModder (Luke Tonon)
 */
@FunctionalInterface
public interface DataType {
	Type OBJECT_TYPE = Type.getType(Object.class);
	Type STRING_TYPE = Type.getType(String.class);
	Type BOOLEAN_TYPE = Type.getType(Boolean.class);
	Type CHARACTER_TYPE = Type.getType(Character.class);
	Type BYTE_TYPE = Type.getType(Byte.class);
	Type SHORT_TYPE = Type.getType(Short.class);
	Type INTEGER_TYPE = Type.getType(Integer.class);
	Type FLOAT_TYPE = Type.getType(Float.class);
	Type LONG_TYPE = Type.getType(Long.class);
	Type DOUBLE_TYPE = Type.getType(Double.class);
	DataType BOOLEAN = molding -> Type.BOOLEAN_TYPE;
	DataType BOOLEAN_WRAPPER = molding -> BOOLEAN_TYPE;
	DataType CHAR = molding -> Type.CHAR_TYPE;
	DataType CHAR_WRAPPER = molding -> CHARACTER_TYPE;
	DataType BYTE = molding -> Type.BYTE_TYPE;
	DataType BYTE_WRAPPER = molding -> BYTE_TYPE;
	DataType SHORT = molding -> Type.SHORT_TYPE;
	DataType SHORT_WRAPPER = molding -> SHORT_TYPE;
	DataType INT = molding -> Type.INT_TYPE;
	DataType INT_WRAPPER = molding -> INTEGER_TYPE;
	DataType LONG = molding -> Type.LONG_TYPE;
	DataType LONG_WRAPPER = molding -> LONG_TYPE;
	DataType FLOAT = molding -> Type.FLOAT_TYPE;
	DataType FLOAT_WRAPPER = molding -> FLOAT_TYPE;
	DataType DOUBLE = molding -> Type.DOUBLE_TYPE;
	DataType DOUBLE_WRAPPER = molding -> DOUBLE_TYPE;
	DataType STRING = molding -> STRING_TYPE;
	DataType ELEMENT = new DataType() {
		@Override
		public ElementType elementType() {
			return ElementType.ELEMENT;
		}

		@Override
		public Type getTrueType(Molding<?> molding) {
			return molding.getDataType();
		}
	};

	static String toString(DataType dataType) {
		ElementType elementType = dataType.elementType();
		return elementType != ElementType.NONE ? elementType.name() : dataType.getTrueType(null).getClassName();
	}

	default ElementType elementType() {
		return ElementType.NONE;
	}

	Type getTrueType(Molding<?> molding);

	// TODO: Maybe turn this into a boolean in the future
	enum ElementType {
		NONE,
		ELEMENT,
		LIST,
		MAP
	}
}
