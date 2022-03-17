package com.teamabnormals.blueprint.core.endimator;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.core.endimator.effects.ConfiguredEndimationEffect;
import com.teamabnormals.blueprint.core.util.DataUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import javax.annotation.Nonnull;
import java.util.*;

import static com.teamabnormals.blueprint.common.codec.ErrorableOptionalFieldCodec.errorableOptional;

/**
 * The class that represents a keyframe animation usable by {@link Endimator}.
 * <p>Use {@link #CODEC} for serialization and deserialization of instances of this class.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see Endimator#apply(Endimation, float, Endimator.ResetMode)
 * @see ConfiguredEndimationEffect
 * @see PartKeyframes
 * @see Endimation.Builder
 */
@SuppressWarnings("unused")
public final class Endimation {
	public static final Endimation BLANK = Endimation.builder().build();
	private static final List<ConfiguredEndimationEffect<?, ?>> NO_EFFECTS = new ArrayList<>();
	public static final Codec<Endimation> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				Codec.FLOAT.optionalFieldOf("length").forGetter(endimation -> Optional.of(endimation.length)),
				Codec.FLOAT.optionalFieldOf("blend_weight", 1.0F).forGetter(endimation -> endimation.blendWeight),
				KeyframesCodec.INSTANCE.fieldOf("parts").forGetter(endimation -> endimation.partKeyframes),
				errorableOptional("effects", ConfiguredEndimationEffect.CODEC.listOf(), NO_EFFECTS).forGetter(endimation -> Arrays.asList(endimation.effects))
		).apply(instance, ((length, blendWeight, keyframes, effects) -> {
			ConfiguredEndimationEffect<?, ?>[] effectArray = effects.toArray(new ConfiguredEndimationEffect[0]);
			Arrays.sort(effectArray);
			float largestTime = 0.0F;
			if (length.isPresent()) {
				largestTime = length.get();
			} else {
				for (PartKeyframes partKeyframes : keyframes.values()) {
					for (KeyframeType keyframeType : KeyframeType.values()) {
						EndimationKeyframe[] keyframeArray = keyframeType.getFrames(partKeyframes);
						int arrayLength = keyframeArray.length;
						if (arrayLength > 0) {
							float time = keyframeArray[arrayLength - 1].time;
							if (time > largestTime) {
								largestTime = time;
							}
						}
					}
				}
				int effectsLength = effectArray.length;
				if (effectsLength > 0) {
					float largestEffectTime = effectArray[effectsLength - 1].getTime();
					if (largestEffectTime > largestTime) {
						largestTime = largestEffectTime;
					}
				}
			}
			return new Endimation(largestTime, blendWeight, keyframes, effectArray);
		}));
	});
	private final float length;
	private final float blendWeight;
	private final Object2ObjectArrayMap<String, PartKeyframes> partKeyframes;
	private final ConfiguredEndimationEffect<?, ?>[] effects;

	public Endimation(float length, float blendWeight, Object2ObjectArrayMap<String, PartKeyframes> partKeyframes, ConfiguredEndimationEffect<?, ?>[] effects) {
		this.length = length;
		this.blendWeight = blendWeight;
		this.partKeyframes = partKeyframes;
		this.effects = effects;
	}

	/**
	 * Creates a new {@link Endimation.Builder}.
	 * <p>Use this to ease the creation of {@link Endimation} instances.</p>
	 *
	 * @return A new {@link Endimation.Builder}.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Gets the {@link #length}.
	 *
	 * @return The {@link #length}.
	 */
	public float getLength() {
		return this.length;
	}

	/**
	 * Gets the {@link #blendWeight}.
	 *
	 * @return The {@link #blendWeight}.
	 */
	public float getBlendWeight() {
		return this.blendWeight;
	}

	/**
	 * Gets the {@link #partKeyframes}.
	 *
	 * @return The {@link #partKeyframes}.
	 */
	public Object2ObjectArrayMap<String, PartKeyframes> getPartKeyframes() {
		return this.partKeyframes;
	}

	/**
	 * Gets the {@link #effects}.
	 *
	 * @return The {@link #effects}.
	 */
	public ConfiguredEndimationEffect<?, ?>[] getEffects() {
		return this.effects;
	}

	/**
	 * A singleton enum {@link Codec} implementation for {@link Endimation#partKeyframes}.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public enum KeyframesCodec implements Codec<Object2ObjectArrayMap<String, PartKeyframes>> {
		INSTANCE;

		@Override
		public <T> DataResult<Pair<Object2ObjectArrayMap<String, PartKeyframes>, T>> decode(DynamicOps<T> ops, T input) {
			var mapLikeDataResult = ops.getMap(input);
			var mapLikeDataResultError = mapLikeDataResult.error();
			if (mapLikeDataResultError.isPresent()) {
				return DataResult.error(mapLikeDataResultError.get().message());
			} else {
				MapLike<T> mapLike = mapLikeDataResult.result().get();
				Iterator<Pair<T, T>> iterator = mapLike.entries().iterator();
				Object2ObjectArrayMap<String, PartKeyframes> map = new Object2ObjectArrayMap<>();
				while (iterator.hasNext()) {
					Pair<T, T> pair = iterator.next();
					var partResult = ops.getStringValue(pair.getFirst());
					var partResultError = partResult.error();
					if (partResultError.isEmpty()) {
						String name = partResult.result().get();
						var partKeyframesResult = PartKeyframes.CODEC.decode(ops, pair.getSecond());
						var partKeyframesError = partKeyframesResult.error();
						if (partKeyframesError.isEmpty()) {
							map.put(name, partKeyframesResult.result().get().getFirst());
						} else {
							return DataResult.error(partKeyframesError.get().message());
						}
					} else {
						return DataResult.error(partResultError.get().message());
					}
				}
				return DataResult.success(Pair.of(map, input));
			}
		}

		@Override
		public <T> DataResult<T> encode(Object2ObjectArrayMap<String, PartKeyframes> input, DynamicOps<T> ops, T prefix) {
			RecordBuilder<T> keyframes = ops.mapBuilder();
			input.forEach((partName, partKeyframes) -> keyframes.add(partName, PartKeyframes.CODEC.encode(partKeyframes, ops, ops.empty())));
			return keyframes.build(prefix);
		}
	}

	/**
	 * A singleton enum {@link Codec} implementation for {@link Endimation#effects}.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public enum EffectsCodec implements Codec<Int2ObjectOpenHashMap<ConfiguredEndimationEffect<?, ?>[]>> {
		INSTANCE;

		private static final Codec<List<ConfiguredEndimationEffect<?, ?>>> ARRAY_CODEC = ConfiguredEndimationEffect.CODEC.listOf();

		@Override
		public <T> DataResult<Pair<Int2ObjectOpenHashMap<ConfiguredEndimationEffect<?, ?>[]>, T>> decode(DynamicOps<T> ops, T input) {
			var mapLikeDataResult = ops.getMap(input);
			var mapLikeDataResultError = mapLikeDataResult.error();
			if (mapLikeDataResultError.isPresent()) {
				return DataResult.error(mapLikeDataResultError.get().message());
			} else {
				MapLike<T> mapLike = mapLikeDataResult.result().get();
				Int2ObjectOpenHashMap<ConfiguredEndimationEffect<?, ?>[]> effects = new Int2ObjectOpenHashMap<>((int) mapLike.entries().count());
				Iterator<Pair<T, T>> iterator = mapLike.entries().iterator();
				while (iterator.hasNext()) {
					Pair<T, T> pair = iterator.next();
					var tickResult = ops.getStringValue(pair.getFirst());
					var tickResultError = tickResult.error();
					if (tickResultError.isEmpty()) {
						String tickString = tickResult.result().get();
						try {
							int tick = Integer.parseInt(tickString);
							var configuredEffectsResult = ops.getList(pair.getSecond());
							var configuredEffectsResultError = configuredEffectsResult.error();
							if (configuredEffectsResultError.isEmpty()) {
								List<T> configuredEndimationEffectsRaw = new ArrayList<>();
								configuredEffectsResult.result().get().accept(configuredEndimationEffectsRaw::add);
								List<ConfiguredEndimationEffect<?, ?>> configuredEndimationEffects = new ArrayList<>(configuredEndimationEffectsRaw.size());
								for (T effectRaw : configuredEndimationEffectsRaw) {
									var configuredResult = ConfiguredEndimationEffect.CODEC.decode(ops, effectRaw);
									var configuredResultError = configuredResult.error();
									if (configuredResultError.isPresent()) {
										return DataResult.error(configuredResultError.get().message());
									} else {
										configuredEndimationEffects.add(configuredResult.result().get().getFirst());
									}
								}
								effects.put(tick, configuredEndimationEffects.toArray(new ConfiguredEndimationEffect[0]));
							} else {
								return DataResult.error(configuredEffectsResultError.get().message());
							}
						} catch (NumberFormatException exception) {
							return DataResult.error("Failed to convert effect tick " + tickString + " to an integer!");
						}
					} else {
						return DataResult.error(tickResultError.get().message());
					}
				}
				return DataResult.success(Pair.of(effects, input));
			}
		}

		@Override
		public <T> DataResult<T> encode(Int2ObjectOpenHashMap<ConfiguredEndimationEffect<?, ?>[]> input, DynamicOps<T> ops, T prefix) {
			RecordBuilder<T> effects = ops.mapBuilder();
			input.forEach((tick, configuredEndimationEffects) -> effects.add(String.valueOf(tick), ARRAY_CODEC.encode(Arrays.asList(configuredEndimationEffects), ops, ops.empty())));
			return effects.build(prefix);
		}
	}

	/**
	 * A builder class for {@link Endimation} instances.
	 * <p>Use this to ease the creation of {@link Endimation} instances.</p>
	 * <p>Use instances of this class to make multiple {@link Endimation} instances with caution. The internal maps are NOT immutable!</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class Builder {
		private Optional<Float> length = Optional.empty();
		private float blendWeight = 1.0F;
		private Object2ObjectArrayMap<String, PartKeyframes> keyframes = new Object2ObjectArrayMap<>();
		private ConfiguredEndimationEffect<?, ?>[] effects = new ConfiguredEndimationEffect[0];

		/**
		 * Sets the {@link #length} for the {@link Endimation}.
		 *
		 * @param length The length for the {@link Endimation}.
		 * @return This builder.
		 */
		public Builder length(float length) {
			this.length = Optional.of(length);
			return this;
		}

		/**
		 * Sets the {@link #blendWeight} for the {@link Endimation}.
		 *
		 * @param blendWeight The blend weight for the {@link Endimation}.
		 * @return This builder.
		 */
		public Builder blendWeight(float blendWeight) {
			this.blendWeight = blendWeight;
			return this;
		}

		/**
		 * Sets the {@link #keyframes} for the {@link Endimation}.
		 *
		 * @param keyframes The keyframes for the {@link Endimation}.
		 * @return This builder.
		 */
		public Builder keyframes(Object2ObjectArrayMap<String, PartKeyframes> keyframes) {
			this.keyframes = keyframes;
			return this;
		}

		/**
		 * Sets the {@link #keyframes} for the {@link Endimation}.
		 *
		 * @param keyframes The keyframes for the {@link Endimation}.
		 * @return This builder.
		 */
		public Builder keyframes(Keyframes keyframes) {
			this.keyframes = keyframes.build();
			return this;
		}

		/**
		 * Sets the {@link #effects} for the {@link Endimation}.
		 *
		 * @param effects The effects for the {@link Endimation}.
		 * @return This builder.
		 */
		public Builder effects(ConfiguredEndimationEffect<?, ?>[] effects) {
			this.effects = effects;
			return this;
		}

		/**
		 * Adds multiple {@link ConfiguredEndimationEffect} instances.
		 *
		 * @param configuredEffects An array of {@link ConfiguredEndimationEffect} instances to add.
		 * @return This builder.
		 */
		public Builder addEffects(@Nonnull ConfiguredEndimationEffect<?, ?>... configuredEffects) {
			this.effects = DataUtil.concatArrays(this.effects, configuredEffects);
			return this;
		}

		/**
		 * Builds a new {@link Endimation} from this builder.
		 *
		 * @return This builder.
		 */
		public Endimation build() {
			ConfiguredEndimationEffect<?, ?>[] effects = this.effects;
			Arrays.sort(effects);
			Optional<Float> length = this.length;
			float largestLength = 0.0F;
			if (length.isEmpty()) {
				int effectsLength = effects.length;
				if (effectsLength > 0) {
					float largestEffectTime = effects[effectsLength - 1].getTime();
					if (largestLength < largestEffectTime) {
						largestLength = largestEffectTime;
					}
				}
				for (PartKeyframes partKeyframes : this.keyframes.values()) {
					for (KeyframeType keyframeType : KeyframeType.values()) {
						EndimationKeyframe[] keyframeArray = keyframeType.getFrames(partKeyframes);
						int arrayLength = keyframeArray.length;
						if (arrayLength > 0) {
							float time = keyframeArray[arrayLength - 1].time;
							if (time > largestLength) {
								largestLength = time;
							}
						}
					}
				}
			} else {
				largestLength = length.get();
			}
			return new Endimation(largestLength, this.blendWeight, this.keyframes, this.effects);
		}

		/**
		 * A builder class for {@link Builder#keyframes}.
		 * <p>The internal {@link #keyframes} map is NOT immutable when built.</p>
		 *
		 * @author SmellyModder (Luke Tonon)
		 */
		public static final class Keyframes {
			private final Object2ObjectArrayMap<String, PartKeyframes> keyframes = new Object2ObjectArrayMap<>();

			private Keyframes() {
			}

			/**
			 * Creates a new {@link Keyframes} instance.
			 *
			 * @return A new {@link Keyframes} instance.
			 */
			public static Keyframes keyframes() {
				return new Keyframes();
			}

			/**
			 * Puts a {@link PartKeyframes} instance for a given part name.
			 *
			 * @param part       The part's name.
			 * @param partFrames A {@link PartKeyframes} instance to put.
			 * @return This builder.
			 */
			public Keyframes part(String part, PartKeyframes partFrames) {
				this.keyframes.put(part, partFrames);
				return this;
			}

			/**
			 * Puts a {@link PartKeyframes} instance built from a given {@link PartKeyframes.Builder} for a given part name.
			 *
			 * @param part       The part's name.
			 * @param partFrames A {@link PartKeyframes.Builder} to use to build a new {@link PartKeyframes} instance.
			 * @return This builder.
			 */
			public Keyframes part(String part, PartKeyframes.Builder partFrames) {
				this.keyframes.put(part, partFrames.build());
				return this;
			}

			/**
			 * Gets the {@link #keyframes} map.
			 *
			 * @return The {@link #keyframes} map.
			 */
			public Object2ObjectArrayMap<String, PartKeyframes> build() {
				return this.keyframes;
			}
		}
	}

	/**
	 * A class representing all the typed keyframes a part can have.
	 * <p>Use {@link #CODEC} for serialization and deserialization of instances of this class.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 * @see EndimationKeyframe
	 * @see KeyframeType
	 */
	public static final class PartKeyframes {
		private static final List<EndimationKeyframe> EMPTY = new ArrayList<>(0);
		public static final Codec<PartKeyframes> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(
					errorableOptional("position", EndimationKeyframe.CODEC.listOf(), EMPTY).forGetter(partKeyframes -> Arrays.asList(partKeyframes.position)),
					errorableOptional("rotation", EndimationKeyframe.CODEC.listOf(), EMPTY).forGetter(partKeyframes -> Arrays.asList(partKeyframes.rotation)),
					errorableOptional("offset", EndimationKeyframe.CODEC.listOf(), EMPTY).forGetter(partKeyframes -> Arrays.asList(partKeyframes.offset)),
					errorableOptional("scale", EndimationKeyframe.CODEC.listOf(), EMPTY).forGetter(partKeyframes -> Arrays.asList(partKeyframes.scale))
			).apply(instance, (move, rotate, offset, scale) -> new PartKeyframes(convertAndSort(move), convertAndSort(rotate), convertAndSort(offset), convertAndSort(scale)));
		});
		private final EndimationKeyframe[] position;
		private final EndimationKeyframe[] rotation;
		private final EndimationKeyframe[] offset;
		private final EndimationKeyframe[] scale;

		public PartKeyframes(EndimationKeyframe[] position, EndimationKeyframe[] rotation, EndimationKeyframe[] offset, EndimationKeyframe[] scale) {
			this.position = position;
			this.rotation = rotation;
			this.offset = offset;
			this.scale = scale;
		}

		private static EndimationKeyframe[] convertAndSort(List<EndimationKeyframe> frames) {
			EndimationKeyframe[] array = frames.toArray(new EndimationKeyframe[0]);
			if (frames.size() > 1) {
				Arrays.sort(array);
			}
			return array;
		}

		/**
		 * Gets the {@link #position} frames.
		 *
		 * @return The {@link #position} frames.
		 */
		public EndimationKeyframe[] getPosFrames() {
			return this.position;
		}

		/**
		 * Gets the {@link #rotation} frames.
		 *
		 * @return The {@link #rotation} frames.
		 */
		public EndimationKeyframe[] getRotationFrames() {
			return this.rotation;
		}

		/**
		 * Gets the {@link #offset} frames.
		 *
		 * @return The {@link #offset} frames.
		 */
		public EndimationKeyframe[] getOffsetFrames() {
			return this.offset;
		}

		/**
		 * Gets the {@link #scale} frames.
		 *
		 * @return The {@link #scale} frames.
		 */
		public EndimationKeyframe[] getScaleFrames() {
			return this.scale;
		}

		/**
		 * A builder class for {@link PartKeyframes}.
		 * <p>Use this to ease the creation of {@link PartKeyframes} instances.</p>
		 *
		 * @author SmellyModder (Luke Tonon)
		 */
		public static final class Builder {
			private final TreeSet<EndimationKeyframe> pos = new TreeSet<>();
			private final TreeSet<EndimationKeyframe> rotate = new TreeSet<>();
			private final TreeSet<EndimationKeyframe> offset = new TreeSet<>();
			private final TreeSet<EndimationKeyframe> scale = new TreeSet<>();

			private Builder() {
			}

			/**
			 * Creates a new {@link PartKeyframes.Builder}.
			 *
			 * @return A new {@link Endimation.Builder}.
			 */
			public static Builder partKeyframes() {
				return new Builder();
			}

			/**
			 * Makes the {@link #pos} frames to contain a new array of {@link EndimationKeyframe}s.
			 *
			 * @param endimationKeyframes A new array of {@link EndimationKeyframe}s.
			 * @return This builder.
			 */
			public Builder pos(EndimationKeyframe... endimationKeyframes) {
				TreeSet<EndimationKeyframe> pos = this.pos;
				pos.clear();
				Collections.addAll(pos, endimationKeyframes);
				return this;
			}

			/**
			 * Makes the {@link #rotate} frames to contain a new array of {@link EndimationKeyframe}s.
			 *
			 * @param endimationKeyframes A new array of {@link EndimationKeyframe}s.
			 * @return This builder.
			 */
			public Builder rotate(EndimationKeyframe... endimationKeyframes) {
				TreeSet<EndimationKeyframe> rotate = this.rotate;
				rotate.clear();
				Collections.addAll(rotate, endimationKeyframes);
				return this;
			}

			/**
			 * Makes the {@link #offset} frames to contain a new array of {@link EndimationKeyframe}s.
			 *
			 * @param endimationKeyframes A new array of {@link EndimationKeyframe}s.
			 * @return This builder.
			 */
			public Builder offset(EndimationKeyframe... endimationKeyframes) {
				TreeSet<EndimationKeyframe> offset = this.offset;
				offset.clear();
				Collections.addAll(offset, endimationKeyframes);
				return this;
			}

			/**
			 * Makes the {@link #scale} frames to contain a new array of {@link EndimationKeyframe}s.
			 *
			 * @param endimationKeyframes A new array of {@link EndimationKeyframe}s.
			 * @return This builder.
			 */
			public Builder scale(EndimationKeyframe... endimationKeyframes) {
				TreeSet<EndimationKeyframe> scale = this.scale;
				scale.clear();
				Collections.addAll(scale, endimationKeyframes);
				return this;
			}

			/**
			 * Creates a new {@link PartKeyframes} instance from this builder.
			 *
			 * @return A new {@link PartKeyframes} instance from this builder.
			 */
			public PartKeyframes build() {
				return new PartKeyframes(this.pos.toArray(new EndimationKeyframe[0]), this.rotate.toArray(new EndimationKeyframe[0]), this.offset.toArray(new EndimationKeyframe[0]), this.scale.toArray(new EndimationKeyframe[0]));
			}
		}
	}
}
