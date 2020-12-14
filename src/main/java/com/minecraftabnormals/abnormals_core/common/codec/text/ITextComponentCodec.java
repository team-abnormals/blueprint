package com.minecraftabnormals.abnormals_core.common.codec.text;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A codec for {@link ITextComponent}s.
 * <p>Current missing the ability to serialize styles.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public enum ITextComponentCodec implements Codec<ITextComponent> {
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
	public <T> DataResult<Pair<ITextComponent, T>> decode(DynamicOps<T> ops, T input) {
		DataResult<String> stringDataResult = ops.getStringValue(input);
		if (!stringDataResult.error().isPresent()) {
			return DataResult.success(Pair.of(new StringTextComponent(stringDataResult.result().get()), input));
		}
		DataResult<MapLike<T>> mapLikeDataResult = ops.getMap(input);
		if (!mapLikeDataResult.error().isPresent()) {
			Optional<MapLike<T>> optional = mapLikeDataResult.result();
			if (optional.isPresent()) {
				IFormattableTextComponent formattableTextComponent;
				MapLike<T> mapLike = optional.get();
				if (has(mapLike, "text")) {
					Either<String, String> textOrError = getString(ops, mapLike.get("text"));
					if (textOrError.left().isPresent()) {
						formattableTextComponent = new StringTextComponent(textOrError.left().get());
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
										DataResult<Pair<ITextComponent, T>> dataResult = this.decode(ops, list.get(i));
										Optional<DataResult.PartialResult<Pair<ITextComponent, T>>> error = dataResult.error();
										if (error.isPresent()) {
											return DataResult.error(error.get().message());
										} else {
											objects[i] = dataResult.result().get();
											if (objects[i] instanceof StringTextComponent) {
												StringTextComponent stringTextComponent = (StringTextComponent) objects[i];
												if (stringTextComponent.getStyle().isEmpty() && stringTextComponent.getSiblings().isEmpty()) {
													objects[i] = stringTextComponent.getText();
												}
											}
										}
									}
									formattableTextComponent = new TranslationTextComponent(string, objects);
								} else {
									return DataResult.error("Expected 'with' to be a JsonArray");
								}
							} else {
								formattableTextComponent = new TranslationTextComponent(string);
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
								formattableTextComponent = new ScoreTextComponent(stringNameOrError.left().get(), errorOrStringObjective.left().get());
							}
						} else {
							return DataResult.error("Expected 'score' to be a JsonObject");
						}
					} else if (has(mapLike, "selector")) {
						Either<String, String> selectorOrError = getString(ops, mapLike.get("selector"));
						if (selectorOrError.left().isPresent()) {
							formattableTextComponent = new SelectorTextComponent(selectorOrError.left().get());
						} else {
							return DataResult.error(selectorOrError.right().get());
						}
					} else if (has(mapLike, "keybind")) {
						Either<String, String> selectorOrError = getString(ops, mapLike.get("keybind"));
						if (selectorOrError.left().isPresent()) {
							formattableTextComponent = new KeybindTextComponent(selectorOrError.left().get());
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
									formattableTextComponent = new NBTTextComponent.Block(string, interpret, blockOrError.left().get());
								} else {
									return DataResult.error(blockOrError.right().get());
								}
							} else if (has(mapLike, "entity")) {
								Either<String, String> entityOrError = getString(ops, mapLike.get("entity"));
								if (entityOrError.left().isPresent()) {
									formattableTextComponent = new NBTTextComponent.Entity(string, interpret, entityOrError.left().get());
								} else {
									return DataResult.error(entityOrError.right().get());
								}
							} else {
								if (!has(mapLike, "storage")) {
									return DataResult.error("Don't know how to turn " + mapLike + " into a Component");
								}

								Either<String, String> storageOrError = getString(ops, mapLike.get("storage"));
								if (storageOrError.left().isPresent()) {
									formattableTextComponent = new NBTTextComponent.Storage(string, interpret, new ResourceLocation(storageOrError.left().get()));
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
								DataResult<Pair<ITextComponent, T>> entryResult = this.decode(ops, entry);
								Optional<DataResult.PartialResult<Pair<ITextComponent, T>>> entryError = entryResult.error();
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
			IFormattableTextComponent component = new MarkerTextComponent();
			for (T entry : stringOptional.get().collect(Collectors.toList())) {
				DataResult<Pair<ITextComponent, T>> entryResult = this.decode(ops, entry);
				Optional<DataResult.PartialResult<Pair<ITextComponent, T>>> entryError = entryResult.error();
				if (entryError.isPresent()) {
					return DataResult.error(entryError.get().message());
				} else {
					Optional<Pair<ITextComponent, T>> optional = entryResult.result();
					if (optional.isPresent()) {
						if (component instanceof MarkerTextComponent) {
							component = (IFormattableTextComponent) optional.get().getFirst();
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
	public <T> DataResult<T> encode(ITextComponent input, DynamicOps<T> ops, T prefix) {
		RecordBuilder<T> mapBuilder = ops.mapBuilder();

		//TODO: Add style support
		if (!input.getSiblings().isEmpty()) {
			ListBuilder<T> siblings = ops.listBuilder();
			for (ITextComponent sibling : input.getSiblings()) {
				siblings.add(this.encode(sibling, ops, ops.empty()));
			}
			mapBuilder.add("extra", siblings.build(ops.empty()));
		}

		if (input instanceof StringTextComponent) {
			mapBuilder.add("text", ops.createString(((StringTextComponent) input).getText()));
		} else if (input instanceof TranslationTextComponent) {
			TranslationTextComponent translationTextComponent = (TranslationTextComponent) input;
			mapBuilder.add("translate", ops.createString(((TranslationTextComponent) input).getKey()));
			Object[] formatArgs = translationTextComponent.getFormatArgs();
			if (formatArgs != null && formatArgs.length > 0) {
				ListBuilder<T> with = ops.listBuilder();
				for (Object arg : formatArgs) {
					if (arg instanceof ITextComponent) {
						with.add(this.encode((ITextComponent) arg, ops, ops.empty()));
					} else {
						with.add(ops.createString(String.valueOf(arg)));
					}
				}
				mapBuilder.add("with", with.build(ops.empty()));
			}
		} else if (input instanceof ScoreTextComponent) {
			ScoreTextComponent scoreTextComponent = (ScoreTextComponent) input;
			RecordBuilder<T> scoreMapBuilder = ops.mapBuilder();
			scoreMapBuilder.add("name", ops.createString(scoreTextComponent.getName()));
			scoreMapBuilder.add("objective", ops.createString(scoreTextComponent.getObjective()));
			mapBuilder.add("score", scoreMapBuilder.build(ops.empty()));
		} else if (input instanceof SelectorTextComponent) {
			mapBuilder.add("selector", ops.createString(((SelectorTextComponent) input).getSelector()));
		} else if (input instanceof KeybindTextComponent) {
			mapBuilder.add("keybind", ops.createString(((KeybindTextComponent) input).getKeybind()));
		} else {
			if (!(input instanceof NBTTextComponent)) {
				return DataResult.error("Don't know how to encode " + input + " as a Component");
			}

			NBTTextComponent nbtTextComponent = (NBTTextComponent) input;
			mapBuilder.add("nbt", ops.createString(nbtTextComponent.func_218676_i()));
			mapBuilder.add("interpret", ops.createBoolean(nbtTextComponent.func_218677_j()));
			if (nbtTextComponent instanceof NBTTextComponent.Block) {
				mapBuilder.add("block", ops.createString(((NBTTextComponent.Block) nbtTextComponent).func_218683_k()));
			} else if (nbtTextComponent instanceof NBTTextComponent.Entity) {
				mapBuilder.add("entity", ops.createString(((NBTTextComponent.Entity) nbtTextComponent).func_218687_k()));
			} else {
				if (!(nbtTextComponent instanceof NBTTextComponent.Storage)) {
					return DataResult.error("Don't know to encode " + nbtTextComponent + " as a Component");
				}
				mapBuilder.add("storage", ops.createString(((NBTTextComponent.Storage) nbtTextComponent).func_229726_k_().toString()));
			}
		}
		return mapBuilder.build(prefix);
	}

	static class MarkerTextComponent extends StringTextComponent {

		public MarkerTextComponent() {
			super("");
		}

	}
}
