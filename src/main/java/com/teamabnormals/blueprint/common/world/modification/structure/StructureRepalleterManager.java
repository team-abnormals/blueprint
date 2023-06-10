package com.teamabnormals.blueprint.common.world.modification.structure;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.registry.BlueprintDataPackRegistries;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
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
public final class StructureRepalleterManager {
	static final BasicRegistry<Codec<? extends StructureRepaletter>> REPALLETER_SERIALIZERS = new BasicRegistry<>();
	private static final HashMap<ResourceLocation, StructureRepaletter[]> ASSIGNED_REPALLETERS = new HashMap<>(1);
	private static final ThreadLocal<ActiveData> ACTIVE_DATA = ThreadLocal.withInitial(ActiveData::new);

	static {
		registerSerializer(new ResourceLocation(Blueprint.MOD_ID, "simple"), SimpleStructureRepaletter.CODEC);
		registerSerializer(new ResourceLocation(Blueprint.MOD_ID, "weighted"), WeightedStructureRepaletter.CODEC);
	}

	@SubscribeEvent
	public static void onServerStarted(ServerAboutToStartEvent event) {
		ASSIGNED_REPALLETERS.clear();
		RegistryAccess registryAccess = event.getServer().registryAccess();
		var entries = registryAccess.registryOrThrow(BlueprintDataPackRegistries.STRUCTURE_REPALETTERS).entrySet();
		HashMap<ResourceLocation, ArrayList<StructureRepaletterEntry>> assignedUnsortedEntries = new HashMap<>();
		if (!entries.isEmpty()) {
			for (var entry : entries) {
				StructureRepaletterEntry structureRepaletterEntry = entry.getValue();
				structureRepaletterEntry.structures().stream().forEach(structureHolder -> assignedUnsortedEntries.computeIfAbsent(structureHolder.unwrapKey().orElseThrow().location(), __ -> new ArrayList<>()).add(structureRepaletterEntry));
			}
		}
		assignedUnsortedEntries.forEach((location, structureRepaletterEntries) -> ASSIGNED_REPALLETERS.put(location, structureRepaletterEntries.stream().sorted((Comparator.comparing(StructureRepaletterEntry::priority))).map(StructureRepaletterEntry::repaletter).toArray(StructureRepaletter[]::new)));
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
