package com.teamabnormals.blueprint.core.sonar;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>Loads and caches images from the internet. The cache can be given an expiration time which allows for images to be redownloaded when required.</p>
 * <p>Textures will also be deleted when not looked at for the specified texture cache time which can be disabled by passing <code>-1</code> as the <code>textureCacheTime</code> in the constructors.</p>
 *
 * @author Ocelot
 * @since 3.1.0
 */
@OnlyIn(Dist.CLIENT)
public class OnlineImageCache {
	private static final Logger LOGGER = LogManager.getLogger();

	private final Path cacheFolder;
	private final Path cacheFile;
	private final Map<String, ResourceLocation> locationCache;
	private final Set<String> errored;
	private final Map<String, CompletableFuture<ResourceLocation>> requested;
	private final Map<String, Long> textureCache;
	private final long textureCacheTime;
	private JsonObject cacheFileData;

	public OnlineImageCache(String domain, long textureCacheTime, TimeUnit unit) {
		this.cacheFolder = Minecraft.getInstance().gameDirectory.toPath().resolve(domain + "-online-image-cache");
		this.cacheFile = this.cacheFolder.resolve("cache.json");
		this.locationCache = new HashMap<>();
		this.errored = new HashSet<>();
		this.requested = new HashMap<>();
		this.textureCache = new HashMap<>();
		this.textureCacheTime = unit.toMillis(textureCacheTime);

		if (Files.exists(this.cacheFile)) {
			try (InputStreamReader is = new InputStreamReader(new FileInputStream(this.cacheFile.toFile()))) {
				this.cacheFileData = new JsonParser().parse(is).getAsJsonObject();
			} catch (Exception e) {
				LOGGER.error("Failed to load cache from '" + this.cacheFile + "'", e);
				this.cacheFileData = new JsonObject();
			}
		} else {
			this.cacheFileData = new JsonObject();
		}

		MinecraftForge.EVENT_BUS.register(this);
	}

	private boolean hasTextureExpired(String hash) {
		return this.textureCacheTime > 0 && (!this.textureCache.containsKey(hash) || System.currentTimeMillis() - this.textureCache.get(hash) > 0);
	}

	private boolean hasExpired(String hash) {
		return !this.cacheFileData.has(hash) || (this.cacheFileData.get(hash).getAsLong() != -1 && System.currentTimeMillis() - this.cacheFileData.get(hash).getAsLong() > 0);
	}

	@Nullable
	private synchronized CompletableFuture<ResourceLocation> loadCache(String hash, ResourceLocation location) {
		if (!Files.exists(this.cacheFolder))
			return null;

		Path imageFile = this.cacheFolder.resolve(hash);
		if (!Files.exists(imageFile))
			return null;

		if (this.hasExpired(hash))
			return null;

		return CompletableFuture.supplyAsync(() ->
		{
			try (FileInputStream is = new FileInputStream(imageFile.toFile())) {
				return NativeImage.read(is);
			} catch (IOException e) {
				LOGGER.error("Failed to load image with hash '" + hash + "' from cache. Deleting", e);
				return null;
			}
		}, Util.ioPool()).thenApplyAsync(image ->
		{
			if (image == null) {
				try {
					this.cacheFileData.remove(hash);
					Files.delete(imageFile);
				} catch (IOException e) {
					LOGGER.error("Failed to delete image with hash '" + hash + "' from cache.", e);
				}
				this.textureCache.put(hash, System.currentTimeMillis() + 30000);
				this.errored.add(hash);
				return MissingTextureAtlasSprite.getLocation();
			}
			Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(image));
			this.textureCache.put(hash, System.currentTimeMillis() + 30000);
			return location;
		}, command -> RenderSystem.recordRenderCall(command::run));
	}

	private synchronized void writeCache(String hash, NativeImage image, long expirationDate) throws IOException {
		if (!Files.exists(this.cacheFolder))
			Files.createDirectories(this.cacheFolder);
		if (!Files.exists(this.cacheFile))
			Files.createFile(this.cacheFile);

		this.cacheFileData.addProperty(hash, expirationDate);
		try (FileOutputStream os = new FileOutputStream(this.cacheFile.toFile())) {
			IOUtils.write(this.cacheFileData.toString(), os, Charsets.UTF_8);
		} catch (Exception e) {
			LOGGER.error("Failed to write cache to file.", e);
		}

		image.writeToFile(this.cacheFolder.resolve(hash));
	}

	public CompletableFuture<ResourceLocation> requestTexture(String url) {
		String hash = DigestUtils.md5Hex(url);
		if (this.errored.contains(hash)) {
			this.textureCache.put(hash, System.currentTimeMillis() + 30000);
			return CompletableFuture.completedFuture(MissingTextureAtlasSprite.getLocation());
		}

		ResourceLocation location = this.locationCache.computeIfAbsent(hash, key -> new ResourceLocation("blueprint", key));
		if (Minecraft.getInstance().getTextureManager().getTexture(location, null) != null) {
			this.textureCache.put(hash, System.currentTimeMillis() + 30000);
			return CompletableFuture.completedFuture(location);
		}

		if (this.requested.containsKey(hash))
			return this.requested.get(hash);

		CompletableFuture<ResourceLocation> cachedFuture = this.loadCache(hash, location);
		if (cachedFuture != null) {
			this.requested.put(hash, cachedFuture);
			return cachedFuture;
		}

		LOGGER.info("Requesting image from '" + hash + "'");
		CompletableFuture<ResourceLocation> future = OnlineRequest.request(url).thenApplyAsync(result ->
		{
			if (result == null)
				return null;
			try {
				NativeImage image = NativeImage.read(result);
				this.writeCache(hash, image, System.currentTimeMillis() + this.textureCacheTime);
				return image;
			} catch (IOException e) {
				LOGGER.error("Failed to load online texture from '" + url + "'. Using missing texture sprite.", e);
				return null;
			}
		}).thenApplyAsync(image ->
		{
			this.textureCache.put(hash, System.currentTimeMillis() + 30000);
			if (image == null) {
				this.errored.add(hash);
				return MissingTextureAtlasSprite.getLocation();
			}
			Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(image));
			return location;
		}, command -> RenderSystem.recordRenderCall(command::run));
		this.requested.put(hash, future);
		return future;
	}

	@SubscribeEvent
	public void onEvent(TickEvent.ClientTickEvent event) {
		this.locationCache.entrySet().removeIf(entry -> Minecraft.getInstance().getTextureManager().getTexture(entry.getValue(), null) == null);
		this.locationCache.forEach((hash, location) ->
		{
			if (this.hasTextureExpired(hash)) {
				Minecraft.getInstance().execute(() -> Minecraft.getInstance().getTextureManager().release(location));
			}
		});
		this.errored.removeIf(this::hasTextureExpired);
		this.requested.values().removeIf(CompletableFuture::isDone);
	}
}
