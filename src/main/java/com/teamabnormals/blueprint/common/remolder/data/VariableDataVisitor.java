package com.teamabnormals.blueprint.common.remolder.data;

/**
 * Implementation of {@link DataVisitor} for variables.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface VariableDataVisitor extends DataVisitor {
	VariableDataVisitor.Local OPS = new Local("ops", DataType.REMOLDING);
	VariableDataVisitor.Local THIS = new Local("this", DataType.REMOLDING);
	VariableDataVisitor.Local ROOT = new Local("root", ElementType.ELEMENTAL);
	VariableDataVisitor.Local META = new Local("meta", ElementType.ELEMENTAL);

	void set(Molding molding);

	/**
	 * Implementation of {@link VariableDataVisitor} for local variables.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	record Local(String name, ReturnType type) implements VariableDataVisitor {
		public void allocate(Molding molding) {
			molding.localVarIndex(this.name, this.type.getDataType(molding));
		}

		@Override
		public void visit(Molding molding) {
			DataType<?> type = this.type.getDataType(molding);
			molding.visitVarInsn(type.getType().getOpcode(ILOAD), molding.localVarIndex(this.name, type));
		}

		@Override
		public void set(Molding molding) {
			DataType<?> type = this.type.getDataType(molding);
			molding.visitVarInsn(type.getType().getOpcode(ISTORE), molding.localVarIndex(this.name, type));
		}

		@Override
		public ReturnType getReturnType() {
			return this.type;
		}
	}

	record Provided<T>(String name, DataType<T> type, T initial) implements VariableDataVisitor {
		@Override
		public void visit(Molding molding) {
			molding.getFieldOwnerVariable().visit(molding);
			molding.visitFieldInsn(GETFIELD, molding.getFieldOwnerTypeName(), this.name, this.type.getType().getDescriptor());
		}

		@Override
		public void set(Molding molding) {
			molding.getFieldOwnerVariable().visit(molding);
			molding.visitFieldInsn(PUTFIELD, molding.getFieldOwnerTypeName(), this.name, this.type.getType().getDescriptor());
		}

		@Override
		public DataType<?> getReturnType() {
			return this.type;
		}
	}
}
