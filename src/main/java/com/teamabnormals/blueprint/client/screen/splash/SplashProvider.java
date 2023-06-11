package com.teamabnormals.blueprint.client.screen.splash;

import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link DataProvider} implementation for Blueprint's splash system.
 *
 * @author SmellyModder (Luke Tonon)
 * @see Splash
 */
public abstract class SplashProvider implements DataProvider {
	private final String modId;
	private final PackOutput packOutput;
	private final LinkedList<Splash> splashes = new LinkedList<>();

	protected SplashProvider(String modId, PackOutput packOutput) {
		this.modId = modId;
		this.packOutput = packOutput;
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
	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		this.splashes.clear();
		this.registerSplashes();
		var dataResult = Splash.LIST_CODEC.encodeStart(JsonOps.INSTANCE, this.splashes);
		Path resolvedPath = this.packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.modId).resolve(BlueprintSplashManager.PATH);
		try {
			var error = dataResult.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			return DataProvider.saveStable(cachedOutput, dataResult.result().get(), resolvedPath);
		} catch (JsonParseException e) {
			Blueprint.LOGGER.error("Couldn't save splashes {}", resolvedPath, e);
			return CompletableFuture.completedFuture(null);
		}
	}

	@Override
	public String getName() {
		return "Blueprint Splashes: " + this.modId;
	}
}
