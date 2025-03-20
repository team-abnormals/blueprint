package com.teamabnormals.blueprint.common.remolder.data;

/**
 * Functional interface for telling which {@link DataType} is returned relative to a {@link Molding} instance.
 *
 * @author SmellyModder (Luke Tonon)
 */
@FunctionalInterface
public interface ReturnType {
	DataType<?> getDataType(Molding molding);
}
