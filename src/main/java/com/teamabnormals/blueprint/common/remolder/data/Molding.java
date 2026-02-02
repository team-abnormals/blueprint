package com.teamabnormals.blueprint.common.remolder.data;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.remolder.util.DiscretionaryLabel;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The abstract class for providing various core format-specific operations to use at the bytecode level.
 *
 * @author SmellyModder (Luke Tonon)
 */
public abstract class Molding extends MethodVisitor implements Opcodes {
	private final HashMap<String, Pair<DataType<?>, Integer>> localVariables;
	private final ArrayList<VariableDataVisitor.Provided<?>> providedVariables;
	private final String fieldOwnerTypeName;
	private final VariableDataVisitor fieldOwnerVariable;
	@Nullable
	private Label trueLabel, exitLabel;
	@Nullable
	private DiscretionaryLabel continueLabel, breakLabel;
	private boolean shortCircuitsWithOr, logicallyComplemented;
	private int localVariableOffset;

	public Molding(MethodVisitor method, String fieldOwnerTypeName, VariableDataVisitor fieldOwnerVariable, @Nullable DiscretionaryLabel continueLabel, @Nullable DiscretionaryLabel breakLabel, int localVariableOffset) {
		super(ASM9, method);
		this.localVariables = new HashMap<>();
		this.providedVariables = new ArrayList<>();
		this.fieldOwnerTypeName = fieldOwnerTypeName;
		this.fieldOwnerVariable = fieldOwnerVariable;
		this.continueLabel = continueLabel;
		this.breakLabel = breakLabel;
		this.localVariableOffset = localVariableOffset;
	}

	protected Molding(Molding molding) {
		super(ASM9, new MethodNode());
		this.localVariables = molding.localVariables;
		this.providedVariables = molding.providedVariables;
		this.fieldOwnerTypeName = molding.fieldOwnerTypeName;
		this.fieldOwnerVariable = molding.fieldOwnerVariable;
		this.localVariableOffset = molding.localVariableOffset;
	}

	public final Pair<DataType<?>, Integer> getLocalVar(String name) {
		var variable = this.localVariables.get(name);
		if (variable == null)
			throw new IllegalStateException("Local variable with name " + name + " was never declared or allocated");
		return variable;
	}

	public final int allocateLocalVar(@Nullable String name, DataType<?> type) {
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

	public final int storeLocalVar(String name, DataVisitor visitor, boolean expectAllocated) {
		var variable = this.localVariables.get(name);
		if (variable == null) {
			if (expectAllocated)
				throw new UnsupportedOperationException("Cannot dynamically chose type of statically enforced local variable");
			DataType<?> type = visitor.visit(this);
			this.visitVarInsn(type.getType().getOpcode(ISTORE), this.localVariableOffset);
			this.localVariables.put(name, Pair.of(type, this.localVariableOffset));
			return this.localVariableOffset++;
		} else {
			var variableType = variable.getFirst();
			var variableClazz = variableType.getClazz();
			if (visitor == DataVisitors.NULL) {
				if (variableClazz.isPrimitive())
					throw new UnsupportedOperationException("Value to assign to primitive variable " + name + " must be non-null");
				visitor.visit(this);
			} else {
				var convertedVisitor = DataVisitors.tryToConvert(visitor, variableClazz);
				if (convertedVisitor == null) {
					var visitorType = visitor.visit(this);
					if (!variableClazz.isAssignableFrom(visitorType.getClazz()))
						throw new IllegalArgumentException("Variable with name '" + name + "' exists that has type incompatible with " + visitorType);
				} else convertedVisitor.visit(this);
			}
			this.visitVarInsn(variableType.getType().getOpcode(ISTORE), variable.getSecond());
			return variable.getSecond();
		}
	}

	public int storeLocalVar(DataVisitor visitor) {
		this.visitVarInsn(visitor.visit(this).getType().getOpcode(ISTORE), this.localVariableOffset);
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

	@Nullable
	public Label getTrueLabel() {
		return this.trueLabel;
	}

	@Nullable
	public Label getExitLabel() {
		return this.exitLabel;
	}

	@Override
	public final void visitMaxs(int maxStack, int maxLocals) {}

	public final void visitMaxs() {
		this.getDelegate().visitMaxs(0, 0);
	}

	public final void accept(MethodVisitor visitor) {
		((MethodNode) this.getDelegate()).accept(visitor);
	}

	public final void checkThenCast(Class<?> from, DataType<?> to) {
		if (!to.getClazz().isAssignableFrom(from))
			this.visitTypeInsn(CHECKCAST, to.getInternalName());
	}

	public final void popIf(boolean pop) {
		if (pop) this.visitInsn(POP);
	}

	public abstract Molding createBuffer();

	public final Molding createBuffer(@Nullable Label trueLabel, @Nullable Label exitLabel) {
		Molding molding = this.createBuffer();
		molding.trueLabel = trueLabel;
		molding.exitLabel = exitLabel;
		return molding;
	}

	public final Molding createBuffer(@Nullable Label trueLabel, @Nullable Label exitLabel, boolean shortCircuitsWithOr) {
		Molding molding = this.createBuffer(trueLabel, exitLabel);
		molding.shortCircuitsWithOr = shortCircuitsWithOr;
		return molding;
	}

	public final Molding pass() {
		Molding molding = this.createBuffer(this.trueLabel, this.exitLabel);
		molding.shortCircuitsWithOr = this.shortCircuitsWithOr;
		return molding;
	}

	public final Molding negate(boolean connective) {
		Molding molding;
		if (connective) {
			molding = this.createBuffer(this.getExitLabel(), this.getTrueLabel(), !this.shortCircuitsWithOr());
		} else {
			molding = this.createBuffer(this.getTrueLabel(), this.getExitLabel(), this.shortCircuitsWithOr());
		}
		molding.logicallyComplemented = !this.logicallyComplemented;
		return molding;
	}

	public void beginLoop(DiscretionaryLabel continueLabel, DiscretionaryLabel breakLabel) {
		this.continueLabel = continueLabel;
		this.breakLabel = breakLabel;
	}

	public Pair<DiscretionaryLabel, DiscretionaryLabel> beginInnerLoop(DiscretionaryLabel continueLabel, DiscretionaryLabel breakLabel) {
		Pair<DiscretionaryLabel, DiscretionaryLabel> oldLabels = new Pair<>(this.continueLabel, this.breakLabel);
		this.continueLabel = continueLabel;
		this.breakLabel = breakLabel;
		return oldLabels;
	}

	public void endLoop() {
		this.continueLabel = null;
		this.breakLabel = null;
	}

	public void endLoop(Pair<DiscretionaryLabel, DiscretionaryLabel> oldLabels) {
		this.continueLabel = oldLabels.getFirst();
		this.breakLabel = oldLabels.getSecond();
	}

	@Nullable
	public DiscretionaryLabel getContinueLabel() {
		return this.continueLabel;
	}

	@Nullable
	public DiscretionaryLabel getBreakLabel() {
		return this.breakLabel;
	}

	public boolean shortCircuitsWithOr() {
		return this.shortCircuitsWithOr;
	}

	public boolean isLogicallyComplemented() {
		return this.logicallyComplemented;
	}

	public abstract DataType<?> getDataType();

	public abstract DataType<?> getListType();

	public abstract DataType<?> getMapType();

	public abstract DataType<?> getNullType();

	public abstract DataType<?> getRepresentationType(DataType<?> type) throws UnsupportedOperationException;

	@Nullable
	public abstract ElementType getElementType(DataType<?> type);

	public abstract DataType<?> element(DataVisitor visitor) throws UnsupportedOperationException;

	public abstract DataType<?> listElement(DataVisitor visitor) throws UnsupportedOperationException;

	public abstract DataType<?> mapElement(DataVisitor visitor) throws UnsupportedOperationException;

	public abstract void convert(DataType<?> from, DataType<?> to) throws UnsupportedOperationException;

	public abstract void toString(DataType<?> type) throws UnsupportedOperationException;

	public abstract void testElementalList(DataType<?> type);

	public abstract void testElementalMap(DataType<?> type);

	public abstract void testNullElement(DataType<?> type);

	public abstract void testStringElement(DataType<?> type);

	public abstract void testNumericalElement(DataType<?> type);

	public abstract void testBooleanElement(DataType<?> type);

    public abstract void testEquality();

	public abstract void size(DataType<?> collectionType) throws UnsupportedOperationException;

	public abstract void get(DataVisitor object, @Nullable DataVisitor key) throws UnsupportedOperationException;

	public abstract void set(DataVisitor object, @Nullable DataVisitor key, DataVisitor value, boolean pop) throws UnsupportedOperationException;

	public abstract void add(DataVisitor object, @Nullable DataVisitor key, DataVisitor value) throws UnsupportedOperationException;

	public abstract void remove(DataVisitor object, @Nullable DataVisitor key, boolean pop) throws UnsupportedOperationException;

	public abstract void clear(DataType<?> type) throws UnsupportedOperationException;

	@SuppressWarnings("RawUseOfParameterized")
	public abstract DataType<Iterator> elements(DataType<?> type) throws UnsupportedOperationException;

	@SuppressWarnings("RawUseOfParameterized")
	public abstract DataType<Iterator> keys(DataType<?> type) throws UnsupportedOperationException;

	@SuppressWarnings("RawUseOfParameterized")
	public abstract DataType<Iterator> entries(DataType<?> type) throws UnsupportedOperationException;

	/**
	 * Interface for creating a {@link Molding} instance for an injection point.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public interface Factory {
		Molding create(MethodVisitor method, String fieldOwnerTypeName, VariableDataVisitor fieldOwnerVariable, @Nullable DiscretionaryLabel continueLabel, @Nullable DiscretionaryLabel breakLabel, int localVariableOffset);
	}
}
