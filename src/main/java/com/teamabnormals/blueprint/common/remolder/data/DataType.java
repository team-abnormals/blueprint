package com.teamabnormals.blueprint.common.remolder.data;

import com.mojang.serialization.DynamicOps;
import com.teamabnormals.blueprint.common.remolder.Remolding;
import org.objectweb.asm.Type;

import java.lang.reflect.Array;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

/**
 * Immutable class for providing detailed data types.
 *
 * @param <T> The type of the data.
 * @author SmellyModder (Luke Tonon)
 */
public class DataType<T> implements ReturnType {
	private static final IdentityHashMap<Class<?>, DataType<?>> CACHE = new IdentityHashMap<>();
	public static final DataType<Object> OBJECT = type(Object.class);
	@SuppressWarnings("RawUseOfParameterized")
	public static final DataType<Remolding> REMOLDING = type(Remolding.class);
	@SuppressWarnings("RawUseOfParameterized")
	public static final DataType<DynamicOps> DYNAMIC_OPS = type(DynamicOps.class);
	@SuppressWarnings("RawUseOfParameterized")
	public static final DataType<Iterator> ITERATOR = type(Iterator.class);
	@SuppressWarnings("RawUseOfParameterized")
	public static final DataType<Map.Entry> MAP_ENTRY = type(Map.Entry.class);
	@SuppressWarnings("RawUseOfParameterized")
	public static final DataType<Function> FUNCTION = type(Function.class);
	public static final DataType<Void> VOID = type(void.class);
	public static final DataType<Byte> BYTE = new DataType<>(byte.class) {
		@Override
		public boolean isSmallInteger() {
			return true;
		}
	};
	public static final DataType<Short> SHORT = new DataType<>(short.class) {
		@Override
		public boolean isSmallInteger() {
			return true;
		}
	};
	public static final DataType<Integer> INT = new DataType<>(int.class) {
		@Override
		public boolean isSmallInteger() {
			return true;
		}
	};
	public static final DataType<Long> LONG = new DataType<>(long.class) {
		@Override
		public boolean isLong() {
			return true;
		}
	};
	public static final DataType<Float> FLOAT = new DataType<>(float.class) {
		@Override
		public boolean isFloat() {
			return true;
		}
	};
	public static final DataType<Double> DOUBLE = new DataType<>(double.class) {
		@Override
		public boolean isDouble() {
			return true;
		}
	};
	public static final DataType<Boolean> BOOLEAN = new DataType<>(boolean.class) {
		@Override
		public boolean isBoolean() {
			return true;
		}
	};
	public static final DataType<Character> CHAR = new DataType<>(char.class) {
		@Override
		public boolean isSmallInteger() {
			return true;
		}
	};
	public static final DataType<Number> NUMBER = type(Number.class);
	public static final DataType<Byte> BYTE_WRAPPER = new DataType<>(Byte.class) {
		@Override
		public boolean isSmallInteger() {
			return true;
		}

		@Override
		public void unbox(Molding molding) {
			DataVisitors.unboxByte(molding);
		}
	};
	public static final DataType<Short> SHORT_WRAPPER = new DataType<>(Short.class) {
		@Override
		public boolean isSmallInteger() {
			return true;
		}

		@Override
		public void unbox(Molding molding) {
			DataVisitors.unboxShort(molding);
		}
	};
	public static final DataType<Integer> INTEGER_WRAPPER = new DataType<>(Integer.class) {
		@Override
		public boolean isSmallInteger() {
			return true;
		}

		@Override
		public void unbox(Molding molding) {
			DataVisitors.unboxInteger(molding);
		}
	};
	public static final DataType<Long> LONG_WRAPPER = new DataType<>(Long.class) {
		@Override
		public boolean isLong() {
			return true;
		}

		@Override
		public void unbox(Molding molding) {
			DataVisitors.unboxLong(molding);
		}
	};
	public static final DataType<Float> FLOAT_WRAPPER = new DataType<>(Float.class) {
		@Override
		public boolean isFloat() {
			return true;
		}

		@Override
		public void unbox(Molding molding) {
			DataVisitors.unboxFloat(molding);
		}
	};
	public static final DataType<Double> DOUBLE_WRAPPER = new DataType<>(Double.class) {
		@Override
		public boolean isDouble() {
			return true;
		}

		@Override
		public void unbox(Molding molding) {
			DataVisitors.unboxDouble(molding);
		}
	};
	public static final DataType<Boolean> BOOLEAN_WRAPPER = new DataType<>(Boolean.class) {
		@Override
		public boolean isBoolean() {
			return true;
		}

		@Override
		public void unbox(Molding molding) {
			DataVisitors.unboxBoolean(molding);
		}
	};
	public static final DataType<Character> CHARACTER_WRAPPER = new DataType<>(Character.class) {
		@Override
		public boolean isSmallInteger() {
			return true;
		}

		@Override
		public void unbox(Molding molding) {
			DataVisitors.unboxCharacter(molding);
		}
	};
	public static final DataType<String> STRING = type(String.class);
	private final Class<T> clazz;
	private final Type type;
	private final String internalName;

	private DataType(Class<T> clazz, Type type) {
		this.clazz = clazz;
		this.type = type;
		this.internalName = type.getInternalName();
	}

	private DataType(Class<T> clazz) {
		this(clazz, Type.getType(clazz));
		CACHE.put(clazz, this);
	}

	@SuppressWarnings("unchecked")
	public static <T> DataType<T> type(Class<T> clazz) {
		synchronized (CACHE) {
			return (DataType<T>) CACHE.computeIfAbsent(clazz, __ -> new DataType<>(__, Type.getType(__)));
		}
	}

	public static DataType<?> array(Class<?> clazz, int dimensionality) {
		return type(Array.newInstance(clazz, new int[dimensionality]).getClass());
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

	public boolean isBoolean() {
		return false;
	}

	public boolean isSmallInteger() {
		return false;
	}

	public boolean isLong() {
		return false;
	}

	public boolean isFloat() {
		return false;
	}

	public boolean isDouble() {
		return false;
	}

	public void unbox(Molding molding) {}

	@Override
	public String toString() {
		return this.type.toString();
	}
}
