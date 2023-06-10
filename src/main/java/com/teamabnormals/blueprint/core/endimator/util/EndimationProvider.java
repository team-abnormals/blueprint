package com.teamabnormals.blueprint.core.endimator.util;

import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.endimator.Endimation;
import com.teamabnormals.blueprint.core.endimator.EndimationKeyframe;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link DataProvider} implementation for {@link Endimation} instances.
 *
 * @author SmellyModder (Luke Tonon)
 * @see EndimationEntry
 */
public abstract class EndimationProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final String modId;
	private final PackOutput.PathProvider pathProvider;
	private final ArrayList<EndimationEntry> entries = new ArrayList<>();

	public EndimationProvider(String modId, PackOutput packOutput) {
		this.modId = modId;
		this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "endimations");
	}

	/**
	 * Creates a new {@link EndimationKeyframe} instance with {@link EndimationKeyframe#LINEAR} interpolation.
	 *
	 * @param time The time (in seconds) for the keyframe.
	 * @param x    The x value.
	 * @param y    The y value.
	 * @param z    The z value.
	 * @return A new {@link EndimationKeyframe} instance with {@link EndimationKeyframe#LINEAR} interpolation.
	 */
	public static EndimationKeyframe linear(float time, float x, float y, float z) {
		return new EndimationKeyframe(time, new EndimationKeyframe.Transform(() -> x, () -> y, () -> z), EndimationKeyframe.LINEAR);
	}

	/**
	 * Creates a new default {@link EndimationKeyframe} instance with {@link EndimationKeyframe#LINEAR} interpolation.
	 *
	 * @param time The time (in seconds) for the keyframe.
	 * @return A new default {@link EndimationKeyframe} instance with {@link EndimationKeyframe#LINEAR} interpolation.
	 */
	public static EndimationKeyframe linear(float time) {
		return linear(time, 0.0F, 0.0F, 0.0F);
	}

	/**
	 * Creates a new {@link EndimationKeyframe} instance with {@link EndimationKeyframe#CATMULL_ROM} interpolation.
	 *
	 * @param time The time (in seconds) for the keyframe.
	 * @param x    The x value.
	 * @param y    The y value.
	 * @param z    The z value.
	 * @return A new {@link EndimationKeyframe} instance with {@link EndimationKeyframe#CATMULL_ROM} interpolation.
	 */
	public static EndimationKeyframe catmullRom(float time, float x, float y, float z) {
		return new EndimationKeyframe(time, new EndimationKeyframe.Transform(() -> x, () -> y, () -> z), EndimationKeyframe.CATMULL_ROM);
	}

	/**
	 * Creates a new default {@link EndimationKeyframe} instance with {@link EndimationKeyframe#CATMULL_ROM} interpolation.
	 *
	 * @param time The time (in seconds) for the keyframe.
	 * @return A new default {@link EndimationKeyframe} instance with {@link EndimationKeyframe#CATMULL_ROM} interpolation.
	 */
	public static EndimationKeyframe catmullRom(float time) {
		return catmullRom(time, 0.0F, 0.0F, 0.0F);
	}

	/**
	 * Override this method to add your entries at the appropriate time.
	 */
	protected abstract void addEndimations();

	/**
	 * Creates and adds a new {@link EndimationEntry} instance.
	 *
	 * @param name The name for the entry.
	 * @return A new {@link EndimationEntry} instance.
	 */
	protected EndimationEntry endimation(String name) {
		EndimationEntry entry = new EndimationEntry(name);
		this.entries.add(entry);
		return entry;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		var entries = this.entries;
		entries.clear();
		this.addEndimations();
		return CompletableFuture.allOf(this.entries.stream().map(entry -> {
			Path resolvedPath = this.pathProvider.json(new ResourceLocation(this.modId, entry.name));
			try {
				var dataResult = Endimation.CODEC.encodeStart(JsonOps.INSTANCE, entry.build());
				var error = dataResult.error();
				if (error.isPresent()) throw new JsonParseException(error.get().message());
				return DataProvider.saveStable(cachedOutput, dataResult.result().get(), resolvedPath);
			} catch (JsonParseException e) {
				LOGGER.error("Couldn't save endimation {}", resolvedPath, e);
				return CompletableFuture.completedFuture(null);
			}
		}).toArray(CompletableFuture[]::new));
	}

	@Override
	public String getName() {
		return "Endimation generator for " + this.modId;
	}

	/**
	 * The class for representing a named {@link Endimation} to get generated.
	 * <p>This class is also an extension of {@link Endimation.Builder}.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 * @see Endimation.Builder
	 */
	protected static final class EndimationEntry extends Endimation.Builder {
		private final String name;

		private EndimationEntry(String name) {
			this.name = name;
		}
	}
}
