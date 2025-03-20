package com.teamabnormals.blueprint.common.remolder.data;

import com.mojang.datafixers.util.Pair;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The abstract class for providing various core format-specific operations to use at the bytecode level.
 *
 * @author SmellyModder (Luke Tonon)
 */
public abstract class Molding extends MethodVisitor implements Opcodes {
	private final HashMap<String, Pair<DataType<?>, Integer>> localVariables = new HashMap<>();
	private final ArrayList<VariableDataVisitor.Provided<?>> providedVariables = new ArrayList<>();
	private final String fieldOwnerTypeName;
	private final VariableDataVisitor fieldOwnerVariable;
	private int localVariableOffset;

	public Molding(MethodVisitor method, String fieldOwnerTypeName, VariableDataVisitor fieldOwnerVariable, int localVariableOffset) {
		super(ASM9, method);
		this.fieldOwnerTypeName = fieldOwnerTypeName;
		this.fieldOwnerVariable = fieldOwnerVariable;
		this.localVariableOffset = localVariableOffset;
	}

	public final int localVarIndex(@Nullable String name, DataType<?> type) {
		if (name != null) {
			var variable = this.localVariables.get(name);
			if (variable == null) {
				variable = Pair.of(type, this.localVariableOffset++);
				this.localVariables.put(name, variable);
			} else if (!variable.getFirst().getClazz().isAssignableFrom(type.getClazz())) {
				throw new IllegalArgumentException("Variable with name '" + name + "' exists that has type incompatible with " + type);
			}
			return variable.getSecond();
		}
		return this.localVariableOffset++;
	}

	public final <T> VariableDataVisitor.Provided<T> provideVariable(String name, DataType<T> type, T value) {
		VariableDataVisitor.Provided<T> provided = new VariableDataVisitor.Provided<>("provided" + this.providedVariables.size() + "$" + name, type, value);
		this.providedVariables.add(provided);
		return provided;
	}

	public final ArrayList<VariableDataVisitor.Provided<?>> getProvidedVariables() {
		return this.providedVariables;
	}

	public String getFieldOwnerTypeName() {
		return this.fieldOwnerTypeName;
	}

	public final VariableDataVisitor getFieldOwnerVariable() {
		return this.fieldOwnerVariable;
	}

	public final void checkThenCast(Class<?> from, DataType<?> to) {
		if (!to.getClazz().isAssignableFrom(from))
			this.visitTypeInsn(CHECKCAST, to.getInternalName());
	}

	protected final void prepareChildVisit(DataVisitor object, DataVisitor key, Class<?> from, DataType<?> to) {
		object.visit(this);
		this.checkThenCast(from, to);
		key.visit(this);
	}

	public abstract DataType<?> getDataType();

	public abstract DataType<?> getListType();

	public abstract DataType<?> getMapType();

	public abstract DataType<?> getNullType();

	public abstract DataType<?> getRepresentationType(DataType<?> type) throws UnsupportedOperationException;

	@Nullable
	public abstract ElementType getElementType(DataType<?> type);

	public abstract void element(DataVisitor visitor) throws UnsupportedOperationException;

	public abstract void convert(DataType<?> from, DataType<?> to) throws UnsupportedOperationException;

	public abstract void toString(DataType<?> type) throws UnsupportedOperationException;

	public abstract void testElementalList();

	public abstract void testElementalMap();

	public abstract void testStringElement();

	public abstract void testNumericalElement();

	public abstract void testBooleanElement();

	public abstract void size(DataType<?> collectionType) throws UnsupportedOperationException;

	public abstract void get(DataVisitor object, @Nullable DataVisitor key) throws UnsupportedOperationException;

	public abstract void set(DataVisitor object, @Nullable DataVisitor key, DataVisitor value) throws UnsupportedOperationException;

	public abstract void add(DataVisitor object, @Nullable DataVisitor key, DataVisitor value) throws UnsupportedOperationException;

	public abstract void remove(DataVisitor object, @Nullable DataVisitor key) throws UnsupportedOperationException;

	public abstract void clear(DataType<?> type) throws UnsupportedOperationException;

	/**
	 * Interface for creating a {@link Molding} instance for an injection point.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public interface Factory {
		Molding create(MethodVisitor method, String fieldOwnerTypeName, VariableDataVisitor fieldOwnerVariable, int localVariableOffset);
	}
}
