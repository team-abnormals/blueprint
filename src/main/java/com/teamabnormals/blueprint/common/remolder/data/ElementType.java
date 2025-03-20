package com.teamabnormals.blueprint.common.remolder.data;

/**
 * Enum implementation of {@link ReturnType} for molding-specific element types.
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum ElementType implements ReturnType {
	ELEMENTAL {
		@Override
		public DataType<?> getDataType(Molding molding) {
			return molding.getDataType();
		}
	},
	LIST {
		@Override
		public DataType<?> getDataType(Molding molding) {
			return molding.getListType();
		}
	},
	MAP {
		@Override
		public DataType<?> getDataType(Molding molding) {
			return molding.getMapType();
		}
	}
}
