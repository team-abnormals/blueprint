package com.teamabnormals.blueprint.common.remolder.data;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

/**
 * The interface for providing various core format-specific operations to use at the bytecode level.
 * <p>Implementations of this interface define these core format-specific operations for a specific data format.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface Molding<T> extends Opcodes {
	void get(MethodVisitor method, Consumer<MethodVisitor> parentVisitor, Consumer<MethodVisitor> identifierVisitor, boolean isIdentifierAnIndex);

	void set(MethodVisitor method, Consumer<MethodVisitor> parentVisitor, Consumer<MethodVisitor> identifierVisitor, Consumer<MethodVisitor> valueVisitor, boolean isIdentifierAnIndex);

	void add(MethodVisitor method, Consumer<MethodVisitor> parentVisitor, Consumer<MethodVisitor> identifierVisitor, Consumer<MethodVisitor> valueVisitor, boolean isIdentifierAnIndex);

	void append(MethodVisitor method, Consumer<MethodVisitor> parentVisitor, Consumer<MethodVisitor> valueVisitor);

	void remove(MethodVisitor method, Consumer<MethodVisitor> parentVisitor, Consumer<MethodVisitor> identifierVisitor, boolean isIdentifierAnIndex);

	void booleanElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor);

	void booleanElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor);

	void characterElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor);

	void characterElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor);

	void byteElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor);

	void byteElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor);

	void shortElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor);

	void shortElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor);

	void intElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor);

	void intElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor);

	void longElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor);

	void longElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor);

	void floatElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor);

	void floatElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor);

	void doubleElementFromPrimitive(MethodVisitor method, Consumer<MethodVisitor> primitiveVisitor);

	void doubleElementFromWrapper(MethodVisitor method, Consumer<MethodVisitor> wrapperVisitor);

	void numberElement(MethodVisitor method, Consumer<MethodVisitor> numberVisitor);

	void stringElement(MethodVisitor method, Consumer<MethodVisitor> stringVisitor);

	void arrayLength(MethodVisitor method);

	void mapSize(MethodVisitor method);

	void size(MethodVisitor method);

	void clone(MethodVisitor method);

	void cast(MethodVisitor method);

	void elementToBoolean(MethodVisitor method);

	void elementToChar(MethodVisitor method);

	void elementToByte(MethodVisitor method);

	void elementToShort(MethodVisitor method);

	void elementToInt(MethodVisitor method);

	void elementToLong(MethodVisitor method);

	void elementToFloat(MethodVisitor method);

	void elementToDouble(MethodVisitor method);

	void elementToString(MethodVisitor method);

	Type getDataType();
}
