package com.teamabnormals.blueprint.client.screen.splash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * The reload listener class for {@link Splash} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintSplashManager extends SimplePreparableReloadListener<Pair<LinkedList<Splash>, IdentityHashMap<String, Splash>>> {
	private static final Gson GSON = (new GsonBuilder()).create();
	public static final String PATH = "texts/blueprint/splashes.json";
	private static BlueprintSplashManager instance;
	private LinkedList<Splash> eventSplashes = new LinkedList<>();
	private IdentityHashMap<String, Splash> identifierToRandomSplash = new IdentityHashMap<>();

	/**
	 * Called in {@link Blueprint#Blueprint()} to add a listener for adding instances of {@link BlueprintSplashManager}.
	 * <p><b>This is for internal use only!</b></p>
	 *
	 * @param event A {@link RegisterClientReloadListenersEvent} instance.
	 */
	public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(instance = new BlueprintSplashManager());
	}

	/**
	 * Gets a random event splash if possible.
	 *
	 * @param user   A {@link User} instance to use for calls to {@link Splash#getText(User, RandomSource)}.
	 * @param random A {@link RandomSource} instance to use for calls to {@link Splash#getText(User, RandomSource)}.
	 * @return A random event splash or null if no valid event splashes are found.
	 */
	@Nullable
	public static String getRandomEventSplash(User user, RandomSource random) {
		if (instance == null) return null;
		ArrayList<Splash> eventSplashes = new ArrayList<>(instance.eventSplashes);
		while (eventSplashes.size() > 0) {
			int index = random.nextInt(eventSplashes.size());
			String splash = eventSplashes.get(index).getText(user, random);
			if (splash != null) return splash;
			eventSplashes.remove(index);
		}
		return null;
	}

	/**
	 * Gets the random {@link Splash} instance belonging to an identifier splash.
	 *
	 * @param identifier An identifier splash to look up its corresponding {@link Splash} instance.
	 * @return The random {@link Splash} instance belonging to an identifier splash, or null if no corresponding {@link Splash} instance could be found.
	 */
	@Nullable
	public static Splash getSplashForIdentifier(String identifier) {
		return instance != null ? instance.identifierToRandomSplash.get(identifier) : null;
	}

	@Override
	protected Pair<LinkedList<Splash>, IdentityHashMap<String, Splash>> prepare(ResourceManager manager, ProfilerFiller profilerFiller) {
		LinkedList<Splash> eventSplashes = new LinkedList<>();
		IdentityHashMap<String, Splash> identifierToRandomSplash = new IdentityHashMap<>();
		for (String namespace : manager.getNamespaces()) {
			ResourceLocation location = new ResourceLocation(namespace, PATH);
			for (Resource resource : manager.getResourceStack(location)) {
				try {
					InputStream inputstream = resource.open();
					Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
					JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
					if (element != null) {
						var dataResult = Splash.LIST_CODEC.decode(JsonOps.INSTANCE, element);
						var error = dataResult.error();
						if (error.isPresent()) throw new JsonParseException(error.get().message());
						List<Splash> splashList = dataResult.result().get().getFirst();
						for (Splash splash : splashList) {
							if (splash.isRandom()) {
								// A little cursed.
								// This is the only way to add new random splashes and keep them uniformly distributed even if another mod adds custom splashes.
								identifierToRandomSplash.put(String.valueOf(splash.hashCode()), splash);
								continue;
							}
							eventSplashes.add(splash);
						}
						continue;
					}
					Blueprint.LOGGER.error("Couldn't load splashes file {} from {} as it is null or empty", location, resource.sourcePackId());
				} catch (RuntimeException | IOException exception) {
					Blueprint.LOGGER.error("Couldn't read splashes file {} in data pack {}", location, resource.sourcePackId(), exception);
				}
			}
		}
		return Pair.of(eventSplashes, identifierToRandomSplash);
	}

	@Override
	protected void apply(Pair<LinkedList<Splash>, IdentityHashMap<String, Splash>> pair, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		this.eventSplashes = pair.getFirst();
		LinkedList<Splash> eventSplashes = this.eventSplashes;
		((SplashManagerAccessor) Minecraft.getInstance().getSplashManager()).getSplashes().addAll((this.identifierToRandomSplash = pair.getSecond()).keySet());
		Blueprint.LOGGER.info("Blueprint Splash Manager has loaded {} splashes", eventSplashes.size() + this.identifierToRandomSplash.size());
	}
}
