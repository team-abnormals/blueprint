package com.teamabnormals.blueprint.common.world.modification.structure;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * A {@link SimpleJsonResourceReloadListener} extension for loading {@link StructureRepaletterEntry} instances.
 * <p>This class also handles the assigning and applying of {@link StructureRepaletter} instances.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletterEntry
 * @see StructureRepaletter
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class StructureRepalleterManager extends SimpleJsonResourceReloadListener {
	static final BasicRegistry<Codec<? extends StructureRepaletter>> REPALLETER_SERIALIZERS = new BasicRegistry<>();
	private static final HashMap<ResourceLocation, StructureRepaletter[]> ASSIGNED_REPALLETERS = new HashMap<>(1);
	private static final ThreadLocal<ActiveData> ACTIVE_DATA = ThreadLocal.withInitial(ActiveData::new);

	static {
		registerSerializer(new ResourceLocation(Blueprint.MOD_ID, "simple"), SimpleStructureRepaletter.CODEC);
		registerSerializer(new ResourceLocation(Blueprint.MOD_ID, "weighted"), WeightedStructureRepaletter.CODEC);
	}

	private final RegistryAccess registryAccess;

	private StructureRepalleterManager(RegistryAccess registryAccess) {
		super(new Gson(), "structure_repalleters");
		this.registryAccess = registryAccess;
	}

	@SubscribeEvent
	public static void onReloadListener(AddReloadListenerEvent event) {
		try {
			event.addListener(new StructureRepalleterManager((RegistryAccess) DataUtil.REGISTRY_ACCESS.get(DataUtil.TAG_MANAGER.get(event.getServerResources()))));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Registers an identifiable {@link Codec} instance to use for serializing and deserializing {@link StructureRepaletter} instances.
	 *
	 * @param name  A {@link ResourceLocation} instance to use for identifying the {@link Codec} instance.
	 * @param codec The {@link Codec} instance to register.
	 */
	public static synchronized void registerSerializer(ResourceLocation name, Codec<? extends StructureRepaletter> codec) {
		REPALLETER_SERIALIZERS.register(name, codec);
	}

	/**
	 * Updates {@link #ACTIVE_DATA} to be prepared for the structure generating on the current thread.
	 * <p><b>You should only ever call this method for a very good reason!</b></p>
	 *
	 * @param location The {@link ResourceLocation} instance belonging to the generating structure.
	 * @param random   A {@link RandomSource} instance to use for the repaletters.
	 */
	public static void update(ResourceLocation location, RandomSource random) {
		var repalettersForKey = ASSIGNED_REPALLETERS.get(location);
		if (repalettersForKey == null) return;
		ActiveData activeData = ACTIVE_DATA.get();
		Collections.addAll(activeData.repaletters, repalettersForKey);
		activeData.random = random;
	}

	/**
	 * Gets the "repalleted" {@link BlockState} instance for a given {@link BlockState} instance.
	 *
	 * @param level A {@link ServerLevelAccessor} instance to use for the {@link StructureRepaletter} instances.
	 * @param state A {@link BlockState} instance to potentially replace.
	 * @return The "repalleted" {@link BlockState} instance for a given {@link BlockState} instance.
	 */
	public static BlockState getBlockState(ServerLevelAccessor level, BlockState state) {
		ActiveData activeData = ACTIVE_DATA.get();
		var activeRepaletters = activeData.repaletters;
		if (activeRepaletters.isEmpty()) return state;
		RandomSource random = activeData.random;
		for (StructureRepaletter repaletter : activeRepaletters) {
			BlockState repaletterState = repaletter.getReplacement(level, state, random);
			if (repaletterState != null) return repaletterState;
		}
		return state;
	}

	/**
	 * Clears the repaletters list.
	 */
	public static void reset() {
		ACTIVE_DATA.get().repaletters.clear();
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profilerFiller) {
		ASSIGNED_REPALLETERS.clear();
		var registryAccess = this.registryAccess;
		var registryOps = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
		var structureLocations = registryAccess.registry(Registry.STRUCTURE_REGISTRY).orElseThrow().keySet();
		HashMap<ResourceLocation, ArrayList<StructureRepaletterEntry>> unorderedEntries = new HashMap<>();
		int count = 0;
		for (var mapEntry : map.entrySet()) {
			ResourceLocation name = mapEntry.getKey();
			try {
				StructureRepaletterEntry entry = StructureRepaletterEntry.deserialize(name, mapEntry.getValue(), registryOps);
				if (entry == null) continue;
				entry.selector().select(structureLocations::forEach).forEach(location -> {
					unorderedEntries.computeIfAbsent(location, __ -> new ArrayList<>()).add(entry);
				});
				count++;
			} catch (JsonParseException exception) {
				Blueprint.LOGGER.error("Parsing error loading Structure Repaletter: {}", name, exception);
			}
		}
		unorderedEntries.forEach((location, repaletters) -> {
			ASSIGNED_REPALLETERS.put(location, repaletters.stream().sorted((Comparator.comparing(StructureRepaletterEntry::priority))).map(StructureRepaletterEntry::repaletter).toArray(StructureRepaletter[]::new));
		});
		Blueprint.LOGGER.info("Structure Repaletter Manager has loaded {} repaletters", count);
	}

	/**
	 * The class for storing the data needed for applying correctly assigned {@link StructureRepaletter} instances.
	 *
	 * @author SmellyModder (Luke Tonon)
	 * @see StructureRepalleterManager
	 * @see StructureRepaletter
	 */
	private static final class ActiveData {
		private final ArrayList<StructureRepaletter> repaletters = new ArrayList<>(0);
		private RandomSource random;
	}
}
