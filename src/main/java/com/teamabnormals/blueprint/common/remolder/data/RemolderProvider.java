package com.teamabnormals.blueprint.common.remolder.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.common.remolder.Remolder;
import com.teamabnormals.blueprint.common.remolder.RemolderEntry;
import com.teamabnormals.blueprint.common.remolder.RemolderTypes;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.selectors.NamesResourceSelector;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link DataProvider} implementation for remolders.
 *
 * @author SmellyModder (Luke Tonon)
 */
public abstract class RemolderProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final String modId;
	private final PackOutput.Target packOutputTarget;
	private final PackOutput.PathProvider pathProvider;
	private final CompletableFuture<HolderLookup.Provider> lookupProvider;
	private final ArrayList<Entry> entries = new ArrayList<>();

	public RemolderProvider(String modId, PackOutput.Target packOutputTarget, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		this.modId = modId;
		this.packOutputTarget = packOutputTarget;
		this.pathProvider = packOutput.createPathProvider(packOutputTarget, "remolders");
		this.lookupProvider = lookupProvider;
	}

	/**
	 * Override this to register your remolders.
	 */
	protected abstract void registerEntries(HolderLookup.Provider provider);

	/**
	 * Adds and returns a new remolder {@link Entry} instance.
	 *
	 * @param name The {@link ResourceLocation} name for the remolder.
	 * @return A new remolder {@link Entry} instance.
	 */
	protected Entry entry(ResourceLocation name) {
		Entry entry = new Entry(name);
		this.entries.add(entry);
		return entry;
	}

	/**
	 * Adds and returns a new remolder {@link Entry} instance.
	 *
	 * @param name The name for the remolder.
	 * @return A new remolder {@link Entry} instance.
	 */
	protected Entry entry(String name) {
		return this.entry(new ResourceLocation(this.modId, name));
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return this.lookupProvider.thenCompose(provider -> {
			var entries = this.entries;
			entries.clear();
			this.registerEntries(provider);
			PackOutput.PathProvider pathProvider = this.pathProvider;
			RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, provider);
			return CompletableFuture.allOf(entries.stream().map(entry -> {
				Path resolvedPath = pathProvider.json(entry.name);
				try {
					var dataResult = RemolderEntry.CODEC.encodeStart(registryOps, new RemolderEntry(entry.pathSelector, entry.packSelector, entry.molding, entry.remolder));
					var error = dataResult.error();
					if (error.isPresent()) throw new JsonParseException(error.get().message());
					return DataProvider.saveStable(output, dataResult.result().get(), resolvedPath);
				} catch (JsonParseException e) {
					LOGGER.error("Couldn't save remolder {}", resolvedPath, e);
					return CompletableFuture.completedFuture(null);
				}
			}).toArray(CompletableFuture[]::new));
		});
	}

	@Override
	public String getName() {
		return "Remolders (" + this.packOutputTarget + "): " + this.modId;
	}

	/**
	 * Builder to hold data used to create {@link RemolderEntry} instances.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Entry {
		private final ResourceLocation name;
		private ConditionedResourceSelector pathSelector = ConditionedResourceSelector.EMPTY;
		@Nullable
		private ResourceSelector<?> packSelector = null;
		private MoldingTypes.MoldingType<?> molding = MoldingTypes.JSON;
		private Remolder remolder = RemolderTypes.noop();

		public Entry(ResourceLocation name) {
			this.name = name;
		}

		/**
		 * Changes the {@link ConditionedResourceSelector} instance used for selecting which resources to target.
		 *
		 * @param selector A new {@link ConditionedResourceSelector} instance to use for selecting resources.
		 * @return This entry.
		 */
		public Entry path(ConditionedResourceSelector selector) {
			this.pathSelector = selector;
			return this;
		}

		/**
		 * Changes the {@link ConditionedResourceSelector} instance used for selecting which resources to target.
		 *
		 * @param selector A new {@link ResourceSelector} instance to use for selecting resources.
		 * @return This entry.
		 */
		public Entry path(ResourceSelector<?> selector) {
			return this.path(new ConditionedResourceSelector(selector));
		}

		/**
		 * Changes the {@link ConditionedResourceSelector} instance used for selecting which resources to target.
		 *
		 * @param names A new array of names to target.
		 * @return This entry.
		 */
		public Entry path(String... names) {
			return this.path(new NamesResourceSelector(names));
		}

		/**
		 * Changes the {@link ResourceSelector} instance used for selecting which packs to target.
		 *
		 * @param selector A new {@link ResourceSelector} instance to use for selecting valid packs.
		 * @return This entry.
		 */
		public Entry pack(ResourceSelector<?> selector) {
			this.packSelector = selector;
			return this;
		}

		/**
		 * Changes the {@link MoldingTypes.MoldingType} instance used for compiling and applying the remolder.
		 *
		 * @param molding A new {@link MoldingTypes.MoldingType} instance to use for compiling and applying the remolder.
		 * @return This entry.
		 */
		public Entry molding(MoldingTypes.MoldingType<?> molding) {
			this.molding = molding;
			return this;
		}

		/**
		 * Changes the {@link Remolder} instance used for modifying the targeted resources.
		 *
		 * @param remolder A new {@link Remolder} instance to use for modifying the targeted resources.
		 * @return This entry.
		 */
		public Entry remolder(Remolder remolder) {
			this.remolder = remolder;
			return this;
		}
	}
}
