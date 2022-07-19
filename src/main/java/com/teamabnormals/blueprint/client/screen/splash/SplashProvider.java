package com.teamabnormals.blueprint.client.screen.splash;

import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;

/**
 * The {@link DataProvider} implementation for Blueprint's splash system.
 *
 * @author SmellyModder (Luke Tonon)
 * @see Splash
 */
public abstract class SplashProvider implements DataProvider {
	private final String modId;
	private final String path;
	private final DataGenerator dataGenerator;
	private final LinkedList<Splash> splashes = new LinkedList<>();

	protected SplashProvider(String modId, DataGenerator dataGenerator) {
		this.modId = modId;
		this.path = "assets/" + modId + "/" + BlueprintSplashManager.PATH;
		this.dataGenerator = dataGenerator;
	}

	/**
	 * Adds a {@link LiteralSplash} instance to get generated.
	 *
	 * @param splash The text to use for the {@link LiteralSplash} instance.
	 */
	protected void add(String splash) {
		this.add(new LiteralSplash(splash));
	}

	/**
	 * Adds a {@link Splash} instance to get generated.
	 *
	 * @param splash The {@link Splash} instance to get generated.
	 */
	protected void add(Splash splash) {
		this.splashes.add(splash);
	}

	/**
	 * Override this method to add your splashes at the appropriate time.
	 */
	protected abstract void registerSplashes();

	@Override
	public void run(CachedOutput cachedOutput) throws IOException {
		this.splashes.clear();
		this.registerSplashes();
		Path outputFolder = this.dataGenerator.getOutputFolder();
		Path resolvedPath = outputFolder.resolve(this.path);
		try {
			var dataResult = Splash.LIST_CODEC.encodeStart(JsonOps.INSTANCE, this.splashes);
			var error = dataResult.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			DataProvider.saveStable(cachedOutput, dataResult.result().get(), resolvedPath);
		} catch (JsonParseException | IOException e) {
			Blueprint.LOGGER.error("Couldn't save splashes {}", resolvedPath, e);
		}
	}

	@Override
	public String getName() {
		return "Blueprint Splashes: " + this.modId;
	}
}
