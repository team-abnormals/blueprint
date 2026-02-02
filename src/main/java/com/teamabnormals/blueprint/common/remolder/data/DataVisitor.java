package com.teamabnormals.blueprint.common.remolder.data;

import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

/**
 * Interface for bytecode visiting of {@link Molding} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
@FunctionalInterface
public interface DataVisitor extends Consumer<Molding>, Opcodes {
	DataType<?> visit(Molding molding);

	@Override
	default void accept(Molding molding) {
		this.visit(molding);
	}

	default DataVisitor then(Consumer<? super Molding> consumer, DataType<?> type) {
		return new DataVisitors.Pipe(this, consumer, type);
	}
}
