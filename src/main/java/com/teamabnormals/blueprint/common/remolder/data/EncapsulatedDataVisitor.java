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
		@Override
		public void visit(Molding molding) {
			molding.get(this.parent, this.identifier);
		}

		@Override
		public ReturnType getReturnType() {
			return ElementType.ELEMENTAL;
		}
	}
}
