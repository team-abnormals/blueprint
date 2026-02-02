package com.teamabnormals.blueprint.common.remolder.util;

import java.lang.reflect.Field;

/**
 * A utility class for searching for class features based on identifiers.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class JavaIdentifierUtil {

	public static Field findField(Class<?> cls, String name) {
		// Walk class hierarchy
		for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
			try {
				return c.getDeclaredField(name);
			} catch (NoSuchFieldException ignored) {
			}
		}
		return null;
	}

}
