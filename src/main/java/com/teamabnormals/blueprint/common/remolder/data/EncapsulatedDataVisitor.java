package com.teamabnormals.blueprint.common.remolder.data;

import javax.annotation.Nullable;

/**
 * Implementation of {@link DataVisitor} for children of elements.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface EncapsulatedDataVisitor extends DataVisitor {
	DataVisitor parent();

	@Nullable
	DataVisitor identifier();

	record Elemental(DataVisitor parent, @Nullable DataVisitor identifier) implements EncapsulatedDataVisitor {
		public static Elemental childOfRoot(String key) {
			return new Elemental(VariableDataVisitor.ROOT, DataVisitors.string(key));
		}

		@Override
		public DataType<?> visit(Molding molding) {
			molding.get(this.parent, this.identifier);
			return molding.getDataType();
		}
	}
}
