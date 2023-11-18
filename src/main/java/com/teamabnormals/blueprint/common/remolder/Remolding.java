package com.teamabnormals.blueprint.common.remolder;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;

import javax.annotation.Nullable;

/**
 * A generic functional interface that modifies objects of its type argument.
 *
 * @author SmellyModder (Luke Tonon)
 */
@FunctionalInterface
public interface Remolding<T> {
	Pair<T, T> apply(DynamicOps<T> ops, T root, @Nullable T meta, T variables);
}
