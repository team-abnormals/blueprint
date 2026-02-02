package com.teamabnormals.blueprint.common.remolder.data;

import com.google.gson.*;
import com.teamabnormals.blueprint.common.remolder.util.DiscretionaryLabel;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import javax.annotation.Nullable;

import java.util.Iterator;

import static com.teamabnormals.blueprint.common.remolder.data.DataType.*;

/**
 * A {@link Molding} extension for interacting with {@link JsonElement} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class JsonMolding extends Molding {
	public static final DataType<JsonElement> ELEMENT_DATA_TYPE = type(JsonElement.class);
	public static final DataType<JsonArray> ARRAY_DATA_TYPE = type(JsonArray.class);
	public static final DataType<JsonObject> OBJECT_DATA_TYPE = type(JsonObject.class);
	public static final DataType<JsonNull> NULL_DATA_TYPE = type(JsonNull.class);
	public static final DataType<JsonPrimitive> PRIMITIVE_DATA_TYPE = type(JsonPrimitive.class);

	public JsonMolding(MethodVisitor method, String fieldOwnerTypeName, VariableDataVisitor fieldOwnerVariable, DiscretionaryLabel continueLabel, DiscretionaryLabel breakLabel, int localVariableOffset) {
		super(method, fieldOwnerTypeName, fieldOwnerVariable, continueLabel, breakLabel, localVariableOffset);
	}

	public JsonMolding(JsonMolding molding) {
		super(molding);
	}

	@Override
	public JsonMolding createBuffer() {
		return new JsonMolding(this);
	}

	@Override
	public DataType<?> getDataType() {
		return ELEMENT_DATA_TYPE;
	}

	@Override
	public DataType<?> getListType() {
		return ARRAY_DATA_TYPE;
	}

	@Override
	public DataType<?> getMapType() {
		return OBJECT_DATA_TYPE;
	}

	@Override
	public DataType<?> getNullType() {
		return NULL_DATA_TYPE;
	}

	@Override
	public DataType<?> getRepresentationType(DataType<?> type) {
		if (type.getClazz().isPrimitive() || type == BOOLEAN_WRAPPER || type == CHARACTER_WRAPPER || type == STRING || Number.class.isAssignableFrom(type.getClazz()))
			return PRIMITIVE_DATA_TYPE;
		if (ELEMENT_DATA_TYPE.getClazz().isAssignableFrom(type.getClazz()))
			return type;
		throw new UnsupportedOperationException("Can't represent type: " + type);
	}

	@Override
	@Nullable
	public ElementType getElementType(DataType<?> type) {
		if (type == ARRAY_DATA_TYPE) return ElementType.LIST;
		if (type == OBJECT_DATA_TYPE) return ElementType.MAP;
		if (ELEMENT_DATA_TYPE.getClazz().isAssignableFrom(type.getClazz())) return ElementType.ELEMENTAL;
		return null;
	}

	@Override
	public DataType<?> element(DataVisitor visitor) throws UnsupportedOperationException {
		if (visitor == DataVisitors.NULL) {
			this.visitFieldInsn(GETSTATIC, "com/google/gson/JsonNull", "INSTANCE", NULL_DATA_TYPE.getType().getDescriptor());
			return NULL_DATA_TYPE;
		}
		var buffer = this.createBuffer();
		DataType<?> dataType = visitor.visit(buffer);
		Class<?> clazz = dataType.getClazz();
		if (ELEMENT_DATA_TYPE.getClazz().isAssignableFrom(clazz)) {
			buffer.accept(this);
			this.visitMethodInsn(INVOKEVIRTUAL, ELEMENT_DATA_TYPE.getInternalName(), "deepCopy", "()Lcom/google/gson/JsonElement;", false);
		} else if (dataType == STRING) {
			this.newPrimitive(buffer, "(Ljava/lang/String;)V", null);
		} else if (dataType == CHARACTER_WRAPPER) {
			this.newPrimitive(buffer, "(Ljava/lang/Character;)V", null);
		} else if (dataType == BOOLEAN_WRAPPER) {
			this.newPrimitive(buffer, "(Ljava/lang/Boolean;)V", null);
		} else if (NUMBER.getClazz().isAssignableFrom(clazz)) {
			this.newPrimitive(buffer, "(Ljava/lang/Number;)V", null);
		} else if (clazz == boolean.class) {
			this.newPrimitive(buffer, "(Ljava/lang/Boolean;)V", clazz);
		} else if (clazz.isPrimitive()) {
			this.newPrimitive(buffer, "(Ljava/lang/Number;)V", clazz);
		} else
			throw new UnsupportedOperationException("Don't know to create element from parameter of type: " + dataType);
		return PRIMITIVE_DATA_TYPE;
	}

	@Override
	public DataType<?> listElement(DataVisitor visitor) throws UnsupportedOperationException {
		var type = visitor.visit(this);
		if (type != ARRAY_DATA_TYPE) {
			if (!type.getClazz().isAssignableFrom(ARRAY_DATA_TYPE.getClazz()))
				throw new UnsupportedOperationException("Can't cast " + type + " to JsonArray");
			this.visitTypeInsn(CHECKCAST, ARRAY_DATA_TYPE.getInternalName());
		}
		return ARRAY_DATA_TYPE;
	}

	@Override
	public DataType<?> mapElement(DataVisitor visitor) throws UnsupportedOperationException {
		var type = visitor.visit(this);
		if (type != OBJECT_DATA_TYPE) {
			if (!type.getClazz().isAssignableFrom(OBJECT_DATA_TYPE.getClazz()))
				throw new UnsupportedOperationException("Can't cast " + type + " to JsonObject");
			this.visitTypeInsn(CHECKCAST, OBJECT_DATA_TYPE.getInternalName());
		}
		return OBJECT_DATA_TYPE;
	}

	@Override
	public void convert(DataType<?> from, DataType<?> to) throws UnsupportedOperationException {
		Class<?> toClazz = to.getClazz();
		if (toClazz.isAssignableFrom(from.getClazz())) return;
		if (ELEMENT_DATA_TYPE == from) {
			this.visitTypeInsn(CHECKCAST, PRIMITIVE_DATA_TYPE.getInternalName());
		} else if (PRIMITIVE_DATA_TYPE != from) {
			throw new UnsupportedOperationException("Don't know to convert from " + from);
		}
		if (to == STRING) {
			this.invokeVirtualPrimitive("getAsString", "()Ljava/lang/String;");
		} else if (to == BOOLEAN) {
			this.invokeVirtualPrimitive("getAsBoolean", "()Z");
		} else if (to == INT) {
			this.invokeVirtualPrimitive("getAsInt", "()I");
		} else if (to == DataType.LONG) {
			this.invokeVirtualPrimitive("getAsLong", "()J");
		} else if (to == DataType.FLOAT) {
			this.invokeVirtualPrimitive("getAsFloat", "()F");
		} else if (to == DataType.DOUBLE) {
			this.invokeVirtualPrimitive("getAsDouble", "()D");
		} else if (to == CHAR) {
			this.invokeVirtualPrimitive("getAsCharacter", "()C");
		} else if (to == BYTE) {
			this.invokeVirtualPrimitive("getAsByte", "()B");
		} else if (to == SHORT) {
			this.invokeVirtualPrimitive("getAsShort", "()S");
		} else if (Number.class.isAssignableFrom(toClazz)) {
			this.invokeVirtualPrimitive("getAsNumber", "()Ljava/lang/Number;");
			if (toClazz != Number.class) this.visitTypeInsn(CHECKCAST, to.getInternalName());
		} else throw new UnsupportedOperationException("Don't know to convert to " + to);
	}

	@Override
	public void toString(DataType<?> type) throws UnsupportedOperationException {
		if (!ELEMENT_DATA_TYPE.getClazz().isAssignableFrom(type.getClazz()))
			throw new UnsupportedOperationException("Conversion to string is only supported for elemental types");
		this.visitMethodInsn(INVOKEVIRTUAL, ELEMENT_DATA_TYPE.getInternalName(), "getAsString", "()Ljava/lang/String;", false);
	}

	@Override
	public void testElementalList(DataType<?> type) {
		DataVisitors.assertCanAcceptInstanceof(type);
		this.visitTypeInsn(INSTANCEOF, ARRAY_DATA_TYPE.getInternalName());
	}

	@Override
	public void testElementalMap(DataType<?> type) {
		DataVisitors.assertCanAcceptInstanceof(type);
		this.visitTypeInsn(INSTANCEOF, OBJECT_DATA_TYPE.getInternalName());
	}

	@Override
	public void testNullElement(DataType<?> type) {
		DataVisitors.assertCanAcceptInstanceof(type);
		this.visitTypeInsn(INSTANCEOF, NULL_DATA_TYPE.getInternalName());
	}

	@Override
	public void testStringElement(DataType<?> type) {
		this.testPrimitive("isString", type);
	}

	@Override
	public void testNumericalElement(DataType<?> type) {
		this.testPrimitive("isNumber", type);
	}

	@Override
	public void testBooleanElement(DataType<?> type) {
		this.testPrimitive("isBoolean", type);
	}

	@Override
	public void testEquality() {
		this.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
	}

	@Override
	public void size(DataType<?> collectionType) {
		if (collectionType == ARRAY_DATA_TYPE) {
			this.invokeVirtualArray("size", "()I");
		} else if (collectionType == OBJECT_DATA_TYPE) {
			this.invokeVirtualObject("size", "()I");
		} else if (collectionType == ELEMENT_DATA_TYPE) {
			Label isJsonArrayLabel = new Label();
			Label endLabel = new Label();
			// Check if it's an instance of JsonArray
			this.visitInsn(DUP);
			this.visitTypeInsn(INSTANCEOF, ARRAY_DATA_TYPE.getInternalName());
			this.visitJumpInsn(IFNE, isJsonArrayLabel);
			// If not JsonArray, assume JsonObject and get size
			this.visitTypeInsn(CHECKCAST, OBJECT_DATA_TYPE.getInternalName());
			this.invokeVirtualObject("size", "()I");
			// Jump to endLabel after getting size
			this.visitJumpInsn(GOTO, endLabel);
			// If JsonArray, get size
			this.visitLabel(isJsonArrayLabel);
			this.invokeVirtualArray("size", "()I");
			// Decisions complete
			this.visitLabel(endLabel);
		} else throw new UnsupportedOperationException("Don't know how to get size from type " + collectionType);
	}

	@Override
	public void get(DataVisitor object, @Nullable DataVisitor key) throws UnsupportedOperationException {
		var dataType = object.visit(this);
		var clazz = dataType.getClazz();
		assertElemental(dataType, clazz, "Cannot get child of non-elemental type: ");
		if (key == null) {
			this.prepareLastElementVisit(clazz);
			this.invokeVirtualArray("get", "(I)Lcom/google/gson/JsonElement;");
		} else {
			var keyBuffer = this.createBuffer();
			var keyDataType = key.visit(keyBuffer);
			if (keyDataType == INT) {
				this.checkThenCast(clazz, ARRAY_DATA_TYPE);
				keyBuffer.accept(this);
				this.invokeVirtualArray("get", "(I)Lcom/google/gson/JsonElement;");
			} else if (keyDataType == STRING) {
				this.checkThenCast(clazz, OBJECT_DATA_TYPE);
				keyBuffer.accept(this);
				this.invokeVirtualObject("get", "(Ljava/lang/String;)Lcom/google/gson/JsonElement;");
			} else throw new UnsupportedOperationException("Unknown key type for child getting: " + keyDataType);
		}
	}

	@Override
	public void set(DataVisitor object, @Nullable DataVisitor key, DataVisitor value, boolean noReturn) throws UnsupportedOperationException {
		var dataType = object.visit(this);
		var clazz = dataType.getClazz();
		assertElemental(dataType, clazz, "Cannot set child of non-elemental type: ");
		if (key == null) {
			this.prepareLastElementVisit(clazz);
			var valueType = value.visit(this);
			assertElemental(valueType, valueType.getClazz(), "Cannot set last child to non-elemental type: ");
			this.invokeVirtualArray("set", "(ILcom/google/gson/JsonElement;)Lcom/google/gson/JsonElement;");
			this.popIf(noReturn);
		} else {
			var buffer = this.createBuffer();
			var keyDataType = key.visit(buffer);
			if (keyDataType == INT) {
				this.checkThenCast(clazz, ARRAY_DATA_TYPE);
				buffer.accept(this);
				var valueType = value.visit(this);
				assertElemental(valueType, valueType.getClazz(), "Cannot set array child to non-elemental type: ");
				this.invokeVirtualArray("set", "(ILcom/google/gson/JsonElement;)Lcom/google/gson/JsonElement;");
				this.popIf(noReturn);
			} else if (keyDataType == STRING) {
				this.checkThenCast(clazz, OBJECT_DATA_TYPE);
				var valueType = value.visit(buffer);
				assertElemental(valueType, valueType.getClazz(), "Cannot set object child to non-elemental type: ");
				if (noReturn) {
					buffer.accept(this);
					this.invokeVirtualObject("add", "(Ljava/lang/String;Lcom/google/gson/JsonElement;)V");
				} else {
					this.invokeVirtualObject("asMap", "()Ljava/util/Map;");
					buffer.accept(this);
					this.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
				}
			} else throw new UnsupportedOperationException("Unknown key type for setting child: " + keyDataType);
		}
	}

	@Override
	public void add(DataVisitor object, @Nullable DataVisitor key, DataVisitor value) throws UnsupportedOperationException {
		var dataType = object.visit(this);
		var clazz = dataType.getClazz();
		assertElemental(dataType, clazz, "Cannot add child of non-elemental type: ");
		if (key == null) {
			this.checkThenCast(clazz, ARRAY_DATA_TYPE);
			var valueType = value.visit(this);
			assertElemental(valueType, valueType.getClazz(), "Cannot append child as non-elemental type: ");
			this.invokeVirtualArray("add", "(Lcom/google/gson/JsonElement;)V");
		} else {
			var keyBuffer = this.createBuffer();
			var keyDataType = key.visit(keyBuffer);
			if (keyDataType == INT) {
				this.checkThenCast(clazz, ARRAY_DATA_TYPE);
				this.invokeVirtualArray("asList", "()Ljava/util/List;");
				keyBuffer.accept(this);
				var valueType = value.visit(this);
				assertElemental(valueType, valueType.getClazz(), "Cannot add array child of non-elemental type: ");
				this.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(ILjava/lang/Object;)V", true);
			} else if (keyDataType == STRING) {
				this.checkThenCast(clazz, OBJECT_DATA_TYPE);
				this.visitInsn(DUP);
				keyBuffer.accept(this);
				this.visitInsn(DUP_X1);
				this.invokeVirtualObject("has", "(Ljava/lang/String;)Z");
				// If the object has the key, do not add
				Label keyExistsLabel = new Label();
				this.visitJumpInsn(IFNE, keyExistsLabel);
				// Otherwise, add to the object and goto end
				var valueType = value.visit(this);
				assertElemental(valueType, valueType.getClazz(), "Cannot add object child of non-elemental type: ");
				this.invokeVirtualObject("add", "(Ljava/lang/String;Lcom/google/gson/JsonElement;)V");
				Label endLabel = new Label();
				this.visitJumpInsn(GOTO, endLabel);
				// When key present, pop "parent" object off the stack to make the stack empty
				this.visitLabel(keyExistsLabel);
				this.visitInsn(POP2);
				// Decisions complete
				this.visitLabel(endLabel);
			} else throw new UnsupportedOperationException("Unknown key type for setting child: " + keyDataType);
		}
	}

	@Override
	public void remove(DataVisitor object, @Nullable DataVisitor key, boolean noReturn) throws UnsupportedOperationException {
		var dataType = object.visit(this);
		var clazz = dataType.getClazz();
		assertElemental(dataType, clazz, "Cannot remove child of non-elemental type: ");
		if (key == null) {
			this.prepareLastElementVisit(clazz);
			this.invokeVirtualArray("remove", "(I)Lcom/google/gson/JsonElement;");
			this.popIf(noReturn);
		} else {
			var keyBuffer = this.createBuffer();
			var keyDataType = key.visit(keyBuffer);
			if (keyDataType == INT) {
				this.checkThenCast(clazz, ARRAY_DATA_TYPE);
				keyBuffer.accept(this);
				this.invokeVirtualArray("remove", "(I)Lcom/google/gson/JsonElement;");
				this.popIf(noReturn);
			} else if (keyDataType == STRING) {
				this.checkThenCast(clazz, OBJECT_DATA_TYPE);
				keyBuffer.accept(this);
				this.invokeVirtualObject("remove", "(Ljava/lang/String;)Lcom/google/gson/JsonElement;");
				this.popIf(noReturn);
			} else throw new UnsupportedOperationException("Unknown key type for removing child: " + keyDataType);
		}
	}

	@Override
	public void clear(DataType<?> type) throws UnsupportedOperationException {
		if (type == ARRAY_DATA_TYPE) {
			this.invokeVirtualArray("asList", "()Ljava/util/List;");
			this.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "clear", "()V", true);
		} else if (type == OBJECT_DATA_TYPE) {
			this.invokeVirtualObject("asMap", "()Ljava/util/Map;");
			this.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "clear", "()V", true);
		} else throw new UnsupportedOperationException("Cannot clear variable of type: " + type);
	}

	@SuppressWarnings("RawUseOfParameterized")
	@Override
	public DataType<Iterator> elements(DataType<?> type) throws UnsupportedOperationException {
		if (type != ARRAY_DATA_TYPE) {
			if (type.getClazz().isAssignableFrom(ARRAY_DATA_TYPE.getClazz()))
				this.visitTypeInsn(CHECKCAST, ARRAY_DATA_TYPE.getInternalName());
			else throw new UnsupportedOperationException("Can't get array elements from value of " + type);
		}
		this.invokeVirtualArray("iterator", "()Ljava/util/Iterator;");
		return ITERATOR;
	}

	@SuppressWarnings("RawUseOfParameterized")
	@Override
	public DataType<Iterator> keys(DataType<?> type) throws UnsupportedOperationException {
		if (type != OBJECT_DATA_TYPE) {
			if (type.getClazz().isAssignableFrom(OBJECT_DATA_TYPE.getClazz()))
				this.visitTypeInsn(CHECKCAST, OBJECT_DATA_TYPE.getInternalName());
			else throw new UnsupportedOperationException("Can't get object keys from value of " + type);
		}
		this.invokeVirtualObject("keySet", "()Ljava/util/Set;");
		this.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;", true);
		return ITERATOR;
	}

	@SuppressWarnings("RawUseOfParameterized")
	@Override
	public DataType<Iterator> entries(DataType<?> type) throws UnsupportedOperationException {
		if (type != OBJECT_DATA_TYPE) {
			if (type.getClazz().isAssignableFrom(OBJECT_DATA_TYPE.getClazz()))
				this.visitTypeInsn(CHECKCAST, OBJECT_DATA_TYPE.getInternalName());
			else throw new UnsupportedOperationException("Can't get object entries from value of " + type);
		}
		this.invokeVirtualObject("entrySet", "()Ljava/util/Set;");
		this.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;", true);
		return ITERATOR;
	}

	private static void assertElemental(DataType<?> type, Class<?> clazz, String message) {
		if (!ELEMENT_DATA_TYPE.getClazz().isAssignableFrom(clazz))
			throw new UnsupportedOperationException(message + type);
	}

	private void newPrimitive(Molding buffer, String constructorDesc, @Nullable Class<?> primitiveClass) {
		this.visitTypeInsn(NEW, PRIMITIVE_DATA_TYPE.getInternalName());
		this.visitInsn(DUP);
		buffer.accept(this);
		if (primitiveClass != null) DataVisitors.boxPrimitive(this, primitiveClass);
		this.visitMethodInsn(INVOKESPECIAL, PRIMITIVE_DATA_TYPE.getInternalName(), "<init>", constructorDesc, false);
	}

	private void invokeVirtualPrimitive(String method, String descriptor) {
		this.visitMethodInsn(INVOKEVIRTUAL, PRIMITIVE_DATA_TYPE.getInternalName(), method, descriptor, false);
	}

	private void testPrimitive(String method, DataType<?> type) {
		if (type == PRIMITIVE_DATA_TYPE) {
			this.invokeVirtualPrimitive(method, "()Z");
		} else {
			Label falseLabel = new Label();
			Label endLabel = new Label();
			this.visitInsn(DUP);
			this.visitTypeInsn(INSTANCEOF, PRIMITIVE_DATA_TYPE.getInternalName());
			this.visitJumpInsn(IFEQ, falseLabel);
			this.visitTypeInsn(CHECKCAST, PRIMITIVE_DATA_TYPE.getInternalName());
			this.invokeVirtualPrimitive(method, "()Z");
			this.visitJumpInsn(GOTO, endLabel);
			this.visitLabel(falseLabel);
			this.visitInsn(POP);
			this.visitInsn(ICONST_0);
			this.visitLabel(endLabel);
		}
	}

	private void invokeVirtualArray(String method, String descriptor) {
		this.visitMethodInsn(INVOKEVIRTUAL, ARRAY_DATA_TYPE.getInternalName(), method, descriptor, false);
	}

	private void invokeVirtualObject(String method, String descriptor) {
		this.visitMethodInsn(INVOKEVIRTUAL, OBJECT_DATA_TYPE.getInternalName(), method, descriptor, false);
	}

	private void prepareLastElementVisit(Class<?> clazz) {
		this.checkThenCast(clazz, ARRAY_DATA_TYPE);
		this.visitInsn(DUP);
		this.invokeVirtualArray("size", "()I");
		this.visitInsn(ICONST_1);
		this.visitInsn(ISUB);
	}
}
