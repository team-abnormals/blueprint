package com.teamabnormals.blueprint.common.remolder.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

/**
 * A {@link Molding} implementation for interacting with {@link JsonElement} instances at the bytecode level.
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum JsonMolding implements Molding<JsonElement> {
	INSTANCE;

	private static final Type ELEMENT_TYPE = Type.getType(JsonElement.class);
	private static final String ELEMENT_TYPE_NAME = Type.getInternalName(JsonElement.class);
	private static final String PRIMITIVE_TYPE_NAME = Type.getInternalName(JsonPrimitive.class);
	private static final String ARRAY_TYPE_NAME = Type.getInternalName(JsonArray.class);
	private static final String OBJECT_TYPE_NAME = Type.getInternalName(JsonObject.class);

	@Override
	public void get(MethodVisitor method, Consumer<MethodVisitor> parentVisitor, Consumer<MethodVisitor> identifierVisitor, boolean isIdentifierAnIndex) {
		parentVisitor.accept(method);
		if (isIdentifierAnIndex) {
			method.visitTypeInsn(CHECKCAST, ARRAY_TYPE_NAME);
			identifierVisitor.accept(method);
			method.visitMethodInsn(INVOKEVIRTUAL, ARRAY_TYPE_NAME, "get", "(I)Lcom/google/gson/JsonElement;", false);
		} else {
			method.visitTypeInsn(CHECKCAST, OBJECT_TYPE_NAME);
			identifierVisitor.accept(method);
			method.visitMethodInsn(INVOKEVIRTUAL, OBJECT_TYPE_NAME, "get", "(Ljava/lang/String;)Lcom/google/gson/JsonElement;", false);
		}
	}

	@Override
	public void set(MethodVisitor method, Consumer<MethodVisitor> parentVisitor, Consumer<MethodVisitor> identifierVisitor, Consumer<MethodVisitor> valueVisitor, boolean isIdentifierAnIndex) {
		parentVisitor.accept(method);
		if (isIdentifierAnIndex) {
			method.visitTypeInsn(CHECKCAST, ARRAY_TYPE_NAME);
			identifierVisitor.accept(method);
			valueVisitor.accept(method);
			method.visitMethodInsn(INVOKEVIRTUAL, ARRAY_TYPE_NAME, "set", "(ILcom/google/gson/JsonElement;)Lcom/google/gson/JsonElement;", false);
		} else {
			method.visitTypeInsn(CHECKCAST, OBJECT_TYPE_NAME);
			method.visitMethodInsn(INVOKEVIRTUAL, OBJECT_TYPE_NAME, "asMap", "()Ljava/util/Map;", false);
			identifierVisitor.accept(method);
			valueVisitor.accept(method);
			method.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
			this.cast(method);
		}
	}

	@Override
	public void add(MethodVisitor method, Consumer<MethodVisitor> parentVisitor, Consumer<MethodVisitor> identifierVisitor, Consumer<MethodVisitor> valueVisitor, boolean isIdentifierAnIndex) {
		parentVisitor.accept(method);
		if (isIdentifierAnIndex) {
			method.visitTypeInsn(CHECKCAST, ARRAY_TYPE_NAME);
			method.visitMethodInsn(INVOKEVIRTUAL, ARRAY_TYPE_NAME, "asList", "()Ljava/util/List;", false);
			identifierVisitor.accept(method);
			valueVisitor.accept(method);
			method.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(ILjava/lang/Object;)V", true);
		} else {
			method.visitTypeInsn(CHECKCAST, OBJECT_TYPE_NAME);
			method.visitInsn(DUP);
			identifierVisitor.accept(method);
			method.visitInsn(DUP_X1);
			method.visitMethodInsn(INVOKEVIRTUAL, OBJECT_TYPE_NAME, "has", "(Ljava/lang/String;)Z", false);

			// If the object has the key, do not add
			Label keyExistsLabel = new Label();
			method.visitJumpInsn(IFNE, keyExistsLabel);

			// Otherwise, add to the object and goto end
			valueVisitor.accept(method);
			method.visitMethodInsn(INVOKEVIRTUAL, OBJECT_TYPE_NAME, "add", "(Ljava/lang/String;Lcom/google/gson/JsonElement;)V", false);
			Label endLabel = new Label();
			method.visitJumpInsn(GOTO, endLabel);

			// When key present, pop "parent" object off the stack to make the stack empty
			method.visitLabel(keyExistsLabel);
			method.visitInsn(POP2);
			// Decisions complete
			method.visitLabel(endLabel);
		}
	}

	@Override
	public void append(MethodVisitor method, Consumer<MethodVisitor> arrayVisitor, Consumer<MethodVisitor> valueVisitor) {
		arrayVisitor.accept(method);
		method.visitTypeInsn(CHECKCAST, ARRAY_TYPE_NAME);
		valueVisitor.accept(method);
		method.visitMethodInsn(INVOKEVIRTUAL, ARRAY_TYPE_NAME, "add", "(Lcom/google/gson/JsonElement;)V", false);
	}

	@Override
	public void remove(MethodVisitor method, Consumer<MethodVisitor> parentVisitor, Consumer<MethodVisitor> identifierVisitor, boolean isIdentifierAnIndex) {
		parentVisitor.accept(method);
		if (isIdentifierAnIndex) {
			method.visitTypeInsn(CHECKCAST, ARRAY_TYPE_NAME);
			identifierVisitor.accept(method);
			method.visitMethodInsn(INVOKEVIRTUAL, ARRAY_TYPE_NAME, "remove", "(I)Lcom/google/gson/JsonElement;", false);
		} else {
			method.visitTypeInsn(CHECKCAST, OBJECT_TYPE_NAME);
			identifierVisitor.accept(method);
			method.visitMethodInsn(INVOKEVIRTUAL, OBJECT_TYPE_NAME, "remove", "(Ljava/lang/String;)Lcom/google/gson/JsonElement;", false);
		}
	}

	@Override
	public void booleanElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor) {
		this.booleanElementFromWrapper(method, methodVisitor -> {
			primitiveVisitor.accept(methodVisitor);
			DataAccessor.boxBoolean(method);
		});
	}

	@Override
	public void booleanElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor) {
		method.visitTypeInsn(NEW, PRIMITIVE_TYPE_NAME);
		method.visitInsn(DUP);
		wrapperVisitor.accept(method);
		method.visitMethodInsn(INVOKESPECIAL, PRIMITIVE_TYPE_NAME, "<init>", "(Ljava/lang/Boolean;)V", false);
	}

	@Override
	public void characterElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor) {
		this.characterElementFromWrapper(method, methodVisitor -> {
			primitiveVisitor.accept(methodVisitor);
			DataAccessor.boxChar(method);
		});
	}

	@Override
	public void characterElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor) {
		method.visitTypeInsn(NEW, PRIMITIVE_TYPE_NAME);
		method.visitInsn(DUP);
		wrapperVisitor.accept(method);
		method.visitMethodInsn(INVOKESPECIAL, PRIMITIVE_TYPE_NAME, "<init>", "(Ljava/lang/Character;)V", false);
	}

	@Override
	public void byteElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor) {
		this.numberElement(method, methodVisitor -> {
			primitiveVisitor.accept(method);
			DataAccessor.boxByte(method);
		});
	}

	@Override
	public void byteElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor) {
		this.numberElement(method, methodVisitor -> wrapperVisitor.accept(method));
	}

	@Override
	public void shortElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor) {
		this.numberElement(method, methodVisitor -> {
			primitiveVisitor.accept(method);
			DataAccessor.boxShort(method);
		});
	}

	@Override
	public void shortElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor) {
		this.numberElement(method, methodVisitor -> wrapperVisitor.accept(method));
	}

	@Override
	public void intElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor) {
		this.numberElement(method, methodVisitor -> {
			primitiveVisitor.accept(method);
			DataAccessor.boxInt(method);
		});
	}

	@Override
	public void intElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor) {
		this.numberElement(method, methodVisitor -> wrapperVisitor.accept(method));
	}

	@Override
	public void longElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor) {
		this.numberElement(method, methodVisitor -> {
			primitiveVisitor.accept(method);
			DataAccessor.boxLong(method);
		});
	}

	@Override
	public void longElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor) {
		this.numberElement(method, methodVisitor -> wrapperVisitor.accept(method));
	}

	@Override
	public void floatElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor) {
		this.numberElement(method, methodVisitor -> {
			primitiveVisitor.accept(method);
			DataAccessor.boxFloat(method);
		});
	}

	@Override
	public void floatElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor) {
		this.numberElement(method, methodVisitor -> wrapperVisitor.accept(method));
	}

	@Override
	public void doubleElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor) {
		this.numberElement(method, methodVisitor -> {
			primitiveVisitor.accept(method);
			DataAccessor.boxDouble(method);
		});
	}

	@Override
	public void doubleElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor) {
		this.numberElement(method, methodVisitor -> wrapperVisitor.accept(method));
	}

	@Override
	public void numberElement(MethodVisitor method, Consumer<MethodVisitor> numberVisitor) {
		method.visitTypeInsn(NEW, PRIMITIVE_TYPE_NAME);
		method.visitInsn(DUP);
		numberVisitor.accept(method);
		method.visitMethodInsn(INVOKESPECIAL, PRIMITIVE_TYPE_NAME, "<init>", "(Ljava/lang/Number;)V", false);
	}

	@Override
	public void stringElement(MethodVisitor method, Consumer<MethodVisitor> stringVisitor) {
		method.visitTypeInsn(NEW, PRIMITIVE_TYPE_NAME);
		method.visitInsn(DUP);
		stringVisitor.accept(method);
		method.visitMethodInsn(INVOKESPECIAL, PRIMITIVE_TYPE_NAME, "<init>", "(Ljava/lang/String;)V", false);
	}

	@Override
	public void arrayLength(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, ARRAY_TYPE_NAME);
		method.visitMethodInsn(INVOKEVIRTUAL, ARRAY_TYPE_NAME, "size", "()I", false);
	}

	@Override
	public void mapSize(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, OBJECT_TYPE_NAME);
		method.visitMethodInsn(INVOKEVIRTUAL, OBJECT_TYPE_NAME, "size", "()I", false);
	}

	@Override
	public void size(MethodVisitor method) {
		Label isJsonArrayLabel = new Label();
		Label endLabel = new Label();
		// Check if it's an instance of JsonArray
		method.visitInsn(DUP);
		method.visitTypeInsn(INSTANCEOF, ARRAY_TYPE_NAME);
		method.visitJumpInsn(IFNE, isJsonArrayLabel);
		// If not JsonArray, assume JsonObject and get size
		this.mapSize(method);
		// Jump to endLabel after getting size
		method.visitJumpInsn(GOTO, endLabel);
		// If JsonArray, get size
		method.visitLabel(isJsonArrayLabel);
		this.arrayLength(method);
		// Decisions complete
		method.visitLabel(endLabel);
	}

	@Override
	public void clone(MethodVisitor method) {
		method.visitMethodInsn(INVOKEVIRTUAL, ELEMENT_TYPE_NAME, "deepCopy", "()Lcom/google/gson/JsonElement;", false);
	}

	@Override
	public void cast(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, ELEMENT_TYPE_NAME);
	}

	@Override
	public void elementToBoolean(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, PRIMITIVE_TYPE_NAME);
		method.visitMethodInsn(INVOKEVIRTUAL, PRIMITIVE_TYPE_NAME, "getAsBoolean", "()Z", false);
	}

	@Override
	public void elementToChar(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, PRIMITIVE_TYPE_NAME);
		method.visitMethodInsn(INVOKEVIRTUAL, PRIMITIVE_TYPE_NAME, "getAsCharacter", "()C", false);
	}

	@Override
	public void elementToByte(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, PRIMITIVE_TYPE_NAME);
		method.visitMethodInsn(INVOKEVIRTUAL, PRIMITIVE_TYPE_NAME, "getAsByte", "()B", false);
	}

	@Override
	public void elementToShort(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, PRIMITIVE_TYPE_NAME);
		method.visitMethodInsn(INVOKEVIRTUAL, PRIMITIVE_TYPE_NAME, "getAsShort", "()S", false);
	}

	@Override
	public void elementToInt(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, PRIMITIVE_TYPE_NAME);
		method.visitMethodInsn(INVOKEVIRTUAL, PRIMITIVE_TYPE_NAME, "getAsInt", "()I", false);
	}

	@Override
	public void elementToLong(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, PRIMITIVE_TYPE_NAME);
		method.visitMethodInsn(INVOKEVIRTUAL, PRIMITIVE_TYPE_NAME, "getAsLong", "()J", false);
	}

	@Override
	public void elementToFloat(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, PRIMITIVE_TYPE_NAME);
		method.visitMethodInsn(INVOKEVIRTUAL, PRIMITIVE_TYPE_NAME, "getAsFloat", "()F", false);
	}

	@Override
	public void elementToDouble(MethodVisitor method) {
		method.visitTypeInsn(CHECKCAST, PRIMITIVE_TYPE_NAME);
		method.visitMethodInsn(INVOKEVIRTUAL, PRIMITIVE_TYPE_NAME, "getAsDouble", "()D", false);
	}

	@Override
	public void elementToString(MethodVisitor method) {
		method.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
	}

	@Override
	public Type getDataType() {
		return ELEMENT_TYPE;
	}
}
