package com.teamabnormals.blueprint.common.world.modification.structure;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.registry.BlueprintDataPackRegistries;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
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
	private static final IdentityHashMap<ResourceKey<Structure>, StructureRepaletterEntry[]> ASSIGNED_REPALLETERS = new IdentityHashMap<>();
	private static final ThreadLocal<ActiveData> ACTIVE_DATA = ThreadLocal.withInitial(ActiveData::new);

	static {
		registerSerializer(new ResourceLocation(Blueprint.MOD_ID, "simple"), SimpleStructureRepaletter.CODEC);
		registerSerializer(new ResourceLocation(Blueprint.MOD_ID, "weighted"), WeightedStructureRepaletter.CODEC);
	}

	@SubscribeEvent
	public static void onServerStarted(ServerAboutToStartEvent event) {
		ASSIGNED_REPALLETERS.clear();
		var entries = event.getServer().registryAccess().registryOrThrow(BlueprintDataPackRegistries.STRUCTURE_REPALETTERS).entrySet();
		IdentityHashMap<ResourceKey<Structure>, ArrayList<StructureRepaletterEntry>> assignedUnsortedEntries = new IdentityHashMap<>();
		for (var entry : entries) {
			StructureRepaletterEntry structureRepaletterEntry = entry.getValue();
			structureRepaletterEntry.structures().stream().forEach(structureHolder -> assignedUnsortedEntries.computeIfAbsent(structureHolder.unwrapKey().orElseThrow(), __ -> new ArrayList<>()).add(structureRepaletterEntry));
		}
		assignedUnsortedEntries.forEach((location, structureRepaletterEntries) -> ASSIGNED_REPALLETERS.put(location, structureRepaletterEntries.stream().sorted((Comparator.comparing(StructureRepaletterEntry::priority))).toArray(StructureRepaletterEntry[]::new)));
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
	 * Gets the array of {@link StructureRepaletterEntry} instances assigned to a given structure key.
	 *
	 * @param structure The key of the structure to get the entries assigned to the structure.
	 * @return The array of {@link StructureRepaletterEntry} instances assigned to the given structure key.
	 */
	@Nullable
	public static StructureRepaletterEntry[] getRepalettersForStructure(ResourceKey<Structure> structure) {
		return ASSIGNED_REPALLETERS.get(structure);
	}

	/**
	 * Updates the thread-local random for the repaletters.
	 *
	 * @param random A {@link RandomSource} instance to use.
	 */
	public static void updateRandomSource(RandomSource random) {
		ACTIVE_DATA.get().random = random;
	}

	/**
	 * Updates the thread-local active repaletters.
	 *
	 * @param repaletters An array of repaletters to use.
	 * @param pieceType   The structure piece currently being generated.
	 */
	public static void updateActiveRepaletters(StructureRepaletterEntry[] repaletters, @Nullable Holder<StructurePieceType> pieceType) {
		var activeRepaletters = ACTIVE_DATA.get().repaletters;
		activeRepaletters.clear();
		if (repaletters == null) return;
		if (pieceType != null) {
			for (StructureRepaletterEntry entry : repaletters) {
				var pieces = entry.pieces();
				if (pieces.isEmpty() || pieces.get().contains(pieceType)) {
					activeRepaletters.add(entry.repaletter());
				}
			}
		} else {
			for (StructureRepaletterEntry entry : repaletters) {
				if (entry.shouldApplyToAfterPlace()) activeRepaletters.add(entry.repaletter());
			}
		}
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
