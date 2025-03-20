package com.teamabnormals.blueprint.common.remolder.data;

import com.mojang.serialization.DynamicOps;
import com.teamabnormals.blueprint.common.remolder.Remolding;
import org.objectweb.asm.Type;

import java.util.IdentityHashMap;
import java.util.function.Function;

/**
 * Immutable class for providing detailed data types.
 *
 * @param <T> The type of the data.
 * @author SmellyModder (Luke Tonon)
 */
public final class DataType<T> implements ReturnType {
	private static final IdentityHashMap<Class<?>, DataType<?>> CACHE = new IdentityHashMap<>();
	@SuppressWarnings("RawUseOfParameterized")
	public static final DataType<Remolding> REMOLDING = type(Remolding.class);
	@SuppressWarnings("RawUseOfParameterized")
	public static final DataType<DynamicOps> DYNAMIC_OPS = type(DynamicOps.class);
	@SuppressWarnings("RawUseOfParameterized")
	public static final DataType<Function> FUNCTION = type(Function.class);
	public static final DataType<Void> VOID = type(void.class);
	public static final DataType<Byte> BYTE = type(byte.class);
	public static final DataType<Short> SHORT = type(short.class);
	public static final DataType<Integer> INT = type(int.class);
	public static final DataType<Long> LONG = type(long.class);
	public static final DataType<Float> FLOAT = type(float.class);
	public static final DataType<Double> DOUBLE = type(double.class);
	public static final DataType<Boolean> BOOLEAN = type(boolean.class);
	public static final DataType<Character> CHAR = type(char.class);
	public static final DataType<Number> NUMBER = type(Number.class);
	public static final DataType<Byte> BYTE_WRAPPER = type(Byte.class);
	public static final DataType<Short> SHORT_WRAPPER = type(Short.class);
	public static final DataType<Integer> INTEGER_WRAPPER = type(Integer.class);
	public static final DataType<Long> LONG_WRAPPER = type(Long.class);
	public static final DataType<Float> FLOAT_WRAPPER = type(Float.class);
	public static final DataType<Double> DOUBLE_WRAPPER = type(Double.class);
	public static final DataType<Boolean> BOOLEAN_WRAPPER = type(Boolean.class);
	public static final DataType<Character> CHARACTER_WRAPPER = type(Character.class);
	public static final DataType<String> STRING = type(String.class);
	private final Class<T> clazz;
	private final Type type;
	private final String internalName;

	private DataType(Class<T> clazz, Type type) {
		this.clazz = clazz;
		this.type = type;
		this.internalName = type.getInternalName();
	}

	@SuppressWarnings("unchecked")
	public static <T> DataType<T> type(Class<T> clazz) {
		synchronized (CACHE) {
			return (DataType<T>) CACHE.computeIfAbsent(clazz, __ -> new DataType<>(__, Type.getType(__)));
		}
	}

	@Override
	public DataType<?> getDataType(Molding molding) {
		return this;
	}

	public Class<T> getClazz() {
		return this.clazz;
	}

	public Type getType() {
		return this.type;
	}

	public String getInternalName() {
		return this.internalName;
	}

	@Override
	public String toString() {
		return this.type.toString();
	}
}
