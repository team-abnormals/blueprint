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
	void visit(Molding molding);

	@Override
	default void accept(Molding molding) {
		this.visit(molding);
	}

	default ReturnType getReturnType() {
		return DataType.VOID;
	}

	default DataVisitor then(Consumer<? super Molding> consumer, ReturnType type) {
		return new DataVisitors.Pipe(this, consumer, type);
	}
}
