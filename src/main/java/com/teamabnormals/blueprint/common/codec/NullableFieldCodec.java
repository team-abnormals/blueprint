package com.teamabnormals.blueprint.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link MapCodec} extension similar to {@link com.mojang.serialization.codecs.OptionalFieldCodec} with the difference being errors will not get ignored when decoding.
 * <p>{@link com.mojang.serialization.codecs.OptionalFieldCodec} is not very user friendly as it doesn't explain why values get defaulted if an error occurs when decoding.</p>
 *
 * @param <A> The type of object for the codec.
 * @author SmellyModder (Luke Tonon)
 * @see com.mojang.serialization.codecs.OptionalFieldCodec
 */
public class NullableFieldCodec<A> extends MapCodec<Optional<A>> {
	private final String name;
	private final Codec<A> elementCodec;

	public NullableFieldCodec(String name, Codec<A> elementCodec) {
		this.name = name;
		this.elementCodec = elementCodec;
	}

	public static <A> MapCodec<A> nullable(String name, Codec<A> codec, A defaultValue) {
		return new NullableFieldCodec<>(name, codec).xmap(
				o -> o.orElse(defaultValue),
				a -> Objects.equals(a, defaultValue) ? Optional.empty() : Optional.of(a)
		);
	}

	@Override
	public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
		T value = input.get(this.name);
		if (value == null) {
			return DataResult.success(Optional.empty());
		}
		DataResult<A> parsed = this.elementCodec.parse(ops, value);
		if (parsed.result().isPresent()) {
			return parsed.map(Optional::of);
		}
		return DataResult.error(() -> () -> parsed.error().get().message());
	}

	@Override
	public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
		if (input.isPresent()) {
			return prefix.add(this.name, this.elementCodec.encodeStart(ops, input.get()));
		}
		return prefix;
	}

	@Override
	public <T> Stream<T> keys(DynamicOps<T> ops) {
		return Stream.of(ops.createString(this.name));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NullableFieldCodec<?> that = (NullableFieldCodec<?>) o;
		return Objects.equals(this.name, that.name) && Objects.equals(elementCodec, that.elementCodec);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.elementCodec);
	}

	@Override
	public String toString() {
		return "ErrorableOptionalFieldCodec[" + this.name + ": " + this.elementCodec + ']';
	}
}
