package com.teamabnormals.blueprint.common.world.modification.structure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A {@link DataProvider} implementation for generating {@link StructureRepaletterEntry} instances.
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletterEntry
 * @see StructureRepalleterManager
 * @see StructureRepaletter
 */
public abstract class StructureRepaletterProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private final List<Pair<ResourceLocation, StructureRepaletterEntry>> entries = new LinkedList<>();
	private final DataGenerator dataGenerator;
	private final String modid;
	private final Supplier<DynamicOps<JsonElement>> ops;

	protected StructureRepaletterProvider(DataGenerator dataGenerator, String modid, Supplier<DynamicOps<JsonElement>> ops) {
		this.dataGenerator = dataGenerator;
		this.modid = modid;
		this.ops = ops;
	}

	protected StructureRepaletterProvider(DataGenerator dataGenerator, String modid) {
		this(dataGenerator, modid, () -> RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.BUILTIN.get()));
	}

	@Override
	public void run(HashCache cache) {
		HashSet<ResourceLocation> names = new HashSet<>();
		Path outputFolder = this.dataGenerator.getOutputFolder();
		String basePath = "data/" + this.modid + "/structure_repalleters/";
		DynamicOps<JsonElement> ops = this.ops.get();
		var entries = this.entries;
		entries.clear();
		this.registerRepaletters();
		entries.forEach(pair -> {
			ResourceLocation name = pair.getFirst();
			if (!names.add(name)) {
				throw new IllegalStateException("Duplicate structure repaletter: " + name);
			} else {
				Path path = outputFolder.resolve(basePath + name.getPath() + ".json");
				try {
					DataProvider.save(GSON, cache, pair.getSecond().serialize(ops), path);
				} catch (JsonParseException | IOException exception) {
					LOGGER.error("Couldn't save structure repaletter {}", path, exception);
				}
			}
		});
	}

	/**
	 * Override this method to register your repaletters.
	 */
	protected abstract void registerRepaletters();

	/**
	 * Creates and registers a fully customized {@link StructureRepaletterEntry} instance to get generated.
	 *
	 * @param name       The name to use.
	 * @param selector   A {@link ConditionedResourceSelector} instance to use as the selector.
	 * @param priority   The {@link EventPriority} instance to use.
	 * @param repaletter A {@link StructureRepaletter} instance to use.
	 */
	protected void registerRepaletter(String name, ConditionedResourceSelector selector, EventPriority priority, StructureRepaletter repaletter) {
		this.entries.add(Pair.of(new ResourceLocation(this.modid, name), new StructureRepaletterEntry(selector, priority, repaletter)));
	}

	/**
	 * Creates and registers an unconditioned {@link StructureRepaletterEntry} instance to get generated.
	 *
	 * @param name       The name to use.
	 * @param selector   A {@link ResourceSelector} instance to use as the selector.
	 * @param priority   The {@link EventPriority} instance to use.
	 * @param repaletter A {@link StructureRepaletter} instance to use.
	 */
	protected void registerRepaletter(String name, ResourceSelector<?> selector, EventPriority priority, StructureRepaletter repaletter) {
		this.registerRepaletter(name, new ConditionedResourceSelector(selector), priority, repaletter);
	}

	/**
	 * Creates and registers an unconditioned {@link StructureRepaletterEntry} instance that uses a {@link NamesResourceSelector} instance as its selector.
	 *
	 * @param name       The name to use.
	 * @param priority   The {@link EventPriority} instance to use.
	 * @param repaletter A {@link StructureRepaletter} instance to use.
	 * @param selects    An array of {@link ResourceLocation} instances to select.
	 */
	protected void registerRepaletter(String name, EventPriority priority, StructureRepaletter repaletter, ResourceLocation... selects) {
		this.registerRepaletter(name, new NamesResourceSelector(selects), priority, repaletter);
	}

	/**
	 * Creates and registers an unconditioned normally prioritized {@link StructureRepaletterEntry} instance that uses a {@link NamesResourceSelector} instance as its selector.
	 *
	 * @param name       The name to use.
	 * @param repaletter A {@link StructureRepaletter} instance to use.
	 * @param selects    An array of {@link ResourceLocation} instances to select.
	 */
	protected void registerRepaletter(String name, StructureRepaletter repaletter, ResourceLocation... selects) {
		this.registerRepaletter(name, EventPriority.NORMAL, repaletter, selects);
	}

	@Override
	public String getName() {
		return "Structure Repaletter: " + this.modid;
	}
}
