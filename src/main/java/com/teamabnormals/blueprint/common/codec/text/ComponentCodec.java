package com.teamabnormals.blueprint.common.codec.text;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A codec for {@link Component}s.
 * <p>Current missing the ability to serialize styles.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum ComponentCodec implements Codec<Component> {
	INSTANCE;

	private static <T> Either<String, String> getString(DynamicOps<T> ops, T value) {
		DataResult<String> stringDataResult = ops.getStringValue(value);
		Optional<DataResult.PartialResult<String>> error = stringDataResult.error();
		return error.<Either<String, String>>map(stringPartialResult -> Either.right(stringPartialResult.message())).orElseGet(() -> Either.left(stringDataResult.result().get()));
	}

	private static <T> boolean has(@Nonnull MapLike<T> mapLike, @Nonnull String key) {
		return mapLike.get(key) != null;
	}

	@Override
	public <T> DataResult<Pair<Component, T>> decode(DynamicOps<T> ops, T input) {
		DataResult<String> stringDataResult = ops.getStringValue(input);
		if (!stringDataResult.error().isPresent()) {
			return DataResult.success(Pair.of(new TextComponent(stringDataResult.result().get()), input));
		}
		DataResult<MapLike<T>> mapLikeDataResult = ops.getMap(input);
		if (!mapLikeDataResult.error().isPresent()) {
			Optional<MapLike<T>> optional = mapLikeDataResult.result();
			if (optional.isPresent()) {
				MutableComponent formattableTextComponent;
				MapLike<T> mapLike = optional.get();
				if (has(mapLike, "text")) {
					Either<String, String> textOrError = getString(ops, mapLike.get("text"));
					if (textOrError.left().isPresent()) {
						formattableTextComponent = new TextComponent(textOrError.left().get());
					} else {
						return DataResult.error(textOrError.right().get());
					}
				} else {
					String string;
					if (has(mapLike, "translate")) {
						Optional<String> stringOptional = ops.getStringValue(mapLike.get("translate")).result();
						if (stringOptional.isPresent()) {
							string = stringOptional.get();
							if (has(mapLike, "with")) {
								Optional<Stream<T>> withStream = ops.getStream(mapLike.get("with")).result();
								if (withStream.isPresent()) {
									Stream<T> stream = withStream.get();
									List<T> list = stream.collect(Collectors.toList());
									Object[] objects = new Object[list.size()];
									for (int i = 0; i < objects.length; i++) {
										DataResult<Pair<Component, T>> dataResult = this.decode(ops, list.get(i));
										Optional<DataResult.PartialResult<Pair<Component, T>>> error = dataResult.error();
										if (error.isPresent()) {
											return DataResult.error(error.get().message());
										} else {
											objects[i] = dataResult.result().get();
											if (objects[i] instanceof TextComponent stringTextComponent) {
												if (stringTextComponent.getStyle().isEmpty() && stringTextComponent.getSiblings().isEmpty()) {
													objects[i] = stringTextComponent.getText();
												}
											}
										}
									}
									formattableTextComponent = new TranslatableComponent(string, objects);
								} else {
									return DataResult.error("Expected 'with' to be a JsonArray");
								}
							} else {
								formattableTextComponent = new TranslatableComponent(string);
							}
						} else {
							return DataResult.error("Missing 'translate', expected to find string");
						}
					} else if (has(mapLike, "score")) {
						Optional<MapLike<T>> optionalScoreMap = ops.getMap(mapLike.get("score")).result();
						if (optionalScoreMap.isPresent()) {
							MapLike<T> scoreMap = optionalScoreMap.get();
							if (!has(scoreMap, "name") || !has(scoreMap, "objective")) {
								return DataResult.error("A score component needs at least a name and an objective");
							}
							Either<String, String> stringNameOrError = getString(ops, scoreMap.get("name"));
							if (stringNameOrError.right().isPresent()) {
								return DataResult.error(stringNameOrError.right().get());
							} else {
								Either<String, String> errorOrStringObjective = getString(ops, scoreMap.get("objective"));
								if (errorOrStringObjective.right().isPresent()) {
									return DataResult.error(errorOrStringObjective.right().get());
								}
								formattableTextComponent = new ScoreComponent(stringNameOrError.left().get(), errorOrStringObjective.left().get());
							}
						} else {
							return DataResult.error("Expected 'score' to be a JsonObject");
						}
					} else if (has(mapLike, "selector")) {
						Either<String, String> selectorOrError = getString(ops, mapLike.get("selector"));
						if (selectorOrError.left().isPresent()) {
							formattableTextComponent = new SelectorComponent(selectorOrError.left().get(), Optional.empty());
						} else {
							return DataResult.error(selectorOrError.right().get());
						}
					} else if (has(mapLike, "keybind")) {
						Either<String, String> selectorOrError = getString(ops, mapLike.get("keybind"));
						if (selectorOrError.left().isPresent()) {
							formattableTextComponent = new KeybindComponent(selectorOrError.left().get());
						} else {
							return DataResult.error(selectorOrError.right().get());
						}
					} else {
						if (!has(mapLike, "nbt")) {
							return DataResult.error("Don't know how to turn " + mapLike + " into a Component");
						}

						Either<String, String> nbtOrError = getString(ops, mapLike.get("nbt"));
						if (nbtOrError.left().isPresent()) {
							string = nbtOrError.left().get();
							boolean interpret = has(mapLike, "interpret");
							if (interpret) {
								DataResult<Boolean> interpretResult = ops.getBooleanValue(mapLike.get("interpret"));
								if (interpretResult.error().isPresent()) {
									return DataResult.error("Expected 'interpret' to be a boolean");
								} else {
									interpret = interpretResult.result().get();
								}
							}
							if (has(mapLike, "block")) {
								Either<String, String> blockOrError = getString(ops, mapLike.get("block"));
								if (blockOrError.left().isPresent()) {
									formattableTextComponent = new NbtComponent.BlockNbtComponent(string, interpret, blockOrError.left().get(), Optional.empty());
								} else {
									return DataResult.error(blockOrError.right().get());
								}
							} else if (has(mapLike, "entity")) {
								Either<String, String> entityOrError = getString(ops, mapLike.get("entity"));
								if (entityOrError.left().isPresent()) {
									formattableTextComponent = new NbtComponent.EntityNbtComponent(string, interpret, entityOrError.left().get(), Optional.empty());
								} else {
									return DataResult.error(entityOrError.right().get());
								}
							} else {
								if (!has(mapLike, "storage")) {
									return DataResult.error("Don't know how to turn " + mapLike + " into a Component");
								}

								Either<String, String> storageOrError = getString(ops, mapLike.get("storage"));
								if (storageOrError.left().isPresent()) {
									formattableTextComponent = new NbtComponent.StorageNbtComponent(string, interpret, new ResourceLocation(storageOrError.left().get()), Optional.empty());
								} else {
									return DataResult.error(storageOrError.right().get());
								}
							}
						} else {
							return DataResult.error(nbtOrError.right().get());
						}
					}
				}

				T extra = mapLike.get("extra");
				if (extra != null) {
					DataResult<Stream<T>> extraResult = ops.getStream(extra);
					Optional<DataResult.PartialResult<Stream<T>>> error = extraResult.error();
					if (error.isPresent()) {
						return DataResult.error(error.get().message());
					} else {
						Stream<T> extraStream = extraResult.result().get();
						List<T> entries = extraStream.collect(Collectors.toList());
						if (entries.isEmpty()) {
							return DataResult.error("Unexpected empty array of components");
						} else {
							for (T entry : entries) {
								DataResult<Pair<Component, T>> entryResult = this.decode(ops, entry);
								Optional<DataResult.PartialResult<Pair<Component, T>>> entryError = entryResult.error();
								if (entryError.isPresent()) {
									return DataResult.error(entryError.get().message());
								}
								formattableTextComponent.append(entryResult.result().get().getFirst());
							}
						}
					}
				}

				//TODO: Add style support
				return DataResult.success(Pair.of(formattableTextComponent, input));
			}
		}
		DataResult<Stream<T>> streamDataResult = ops.getStream(input);
		Optional<DataResult.PartialResult<Stream<T>>> error = streamDataResult.error();
		if (error.isPresent()) {
			return DataResult.error("Don't know how to turn " + input + " into a Component");
		}
		Optional<Stream<T>> stringOptional = streamDataResult.result();
		if (stringOptional.isPresent()) {
			MutableComponent component = new MarkerTextComponent();
			for (T entry : stringOptional.get().collect(Collectors.toList())) {
				DataResult<Pair<Component, T>> entryResult = this.decode(ops, entry);
				Optional<DataResult.PartialResult<Pair<Component, T>>> entryError = entryResult.error();
				if (entryError.isPresent()) {
					return DataResult.error(entryError.get().message());
				} else {
					Optional<Pair<Component, T>> optional = entryResult.result();
					if (optional.isPresent()) {
						if (component instanceof MarkerTextComponent) {
							component = (MutableComponent) optional.get().getFirst();
						} else {
							component.append(optional.get().getFirst());
						}
					} else {
						return DataResult.error("No Component found in " + entry);
					}
				}
			}
			return DataResult.success(Pair.of(component, input));
		}
		return DataResult.error("Don't know how to turn " + input + " into a Component");
	}

	@Override
	public <T> DataResult<T> encode(Component input, DynamicOps<T> ops, T prefix) {
		RecordBuilder<T> mapBuilder = ops.mapBuilder();

		//TODO: Add style support
		if (!input.getSiblings().isEmpty()) {
			ListBuilder<T> siblings = ops.listBuilder();
			for (Component sibling : input.getSiblings()) {
				DataResult<T> encodedSibling = this.encode(sibling, ops, ops.empty());
				if (encodedSibling.error().isPresent()) {
					return DataResult.error(encodedSibling.error().get().message());
				}
				siblings.add(encodedSibling);
			}
			mapBuilder.add("extra", siblings.build(ops.empty()));
		}

		if (input instanceof TextComponent) {
			mapBuilder.add("text", ops.createString(((TextComponent) input).getText()));
		} else if (input instanceof TranslatableComponent translationTextComponent) {
			mapBuilder.add("translate", ops.createString(((TranslatableComponent) input).getKey()));
			Object[] formatArgs = translationTextComponent.getArgs();
			if (formatArgs != null && formatArgs.length > 0) {
				ListBuilder<T> with = ops.listBuilder();
				for (Object arg : formatArgs) {
					if (arg instanceof Component) {
						DataResult<T> encodedArg = this.encode((Component) arg, ops, ops.empty());
						if (encodedArg.error().isPresent()) {
							return DataResult.error(encodedArg.error().get().message());
						}
						with.add(encodedArg);
					} else {
						with.add(ops.createString(String.valueOf(arg)));
					}
				}
				mapBuilder.add("with", with.build(ops.empty()));
			}
		} else if (input instanceof ScoreComponent scoreTextComponent) {
			RecordBuilder<T> scoreMapBuilder = ops.mapBuilder();
			scoreMapBuilder.add("name", ops.createString(scoreTextComponent.getName()));
			scoreMapBuilder.add("objective", ops.createString(scoreTextComponent.getObjective()));
			mapBuilder.add("score", scoreMapBuilder.build(ops.empty()));
		} else if (input instanceof SelectorComponent) {
			mapBuilder.add("selector", ops.createString(((SelectorComponent) input).getPattern()));
		} else if (input instanceof KeybindComponent) {
			mapBuilder.add("keybind", ops.createString(((KeybindComponent) input).getName()));
		} else {
			if (!(input instanceof NbtComponent nbtTextComponent)) {
				return DataResult.error("Don't know how to encode " + input + " as a Component");
			}

			mapBuilder.add("nbt", ops.createString(nbtTextComponent.getNbtPath()));
			mapBuilder.add("interpret", ops.createBoolean(nbtTextComponent.isInterpreting()));
			if (nbtTextComponent instanceof NbtComponent.BlockNbtComponent) {
				mapBuilder.add("block", ops.createString(((NbtComponent.BlockNbtComponent) nbtTextComponent).getPos()));
			} else if (nbtTextComponent instanceof NbtComponent.EntityNbtComponent) {
				mapBuilder.add("entity", ops.createString(((NbtComponent.EntityNbtComponent) nbtTextComponent).getSelector()));
			} else {
				if (!(nbtTextComponent instanceof NbtComponent.StorageNbtComponent)) {
					return DataResult.error("Don't know to encode " + nbtTextComponent + " as a Component");
				}
				mapBuilder.add("storage", ops.createString(((NbtComponent.StorageNbtComponent) nbtTextComponent).getId().toString()));
			}
		}
		return mapBuilder.build(prefix);
	}

	static class MarkerTextComponent extends TextComponent {

		public MarkerTextComponent() {
			super("");
		}

	}
}
