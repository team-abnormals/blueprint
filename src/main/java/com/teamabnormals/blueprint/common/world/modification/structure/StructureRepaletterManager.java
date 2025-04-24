package com.teamabnormals.blueprint.common.world.modification.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.world.modification.structure.condition.*;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.registry.BlueprintDataPackRegistries;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Optional;

/**
 * A {@link SimpleJsonResourceReloadListener} extension for loading {@link StructureRepaletterEntry} instances.
 * <p>This class also handles the assigning and applying of {@link StructureRepaletter} instances.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletterEntry
 * @see StructureRepaletter
 */
@EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class StructureRepaletterManager {
	static final BasicRegistry<MapCodec<? extends StructureRepaletter>> REPALLETER_SERIALIZERS = new BasicRegistry<>();
	static final BasicRegistry<MapCodec<? extends StructureRepaletter.Replacer>> REPLACER_SERIALIZERS = new BasicRegistry<>();
	static final BasicRegistry<MapCodec<? extends StructureRepaletter.Condition>> CONDITION_SERIALIZERS = new BasicRegistry<>();
	private static final IdentityHashMap<ResourceKey<Structure>, StructureRepaletterEntry[]> ASSIGNED_REPALLETERS = new IdentityHashMap<>();
	private static final ThreadLocal<ActiveData> ACTIVE_DATA = ThreadLocal.withInitial(ActiveData::new);

	static {
		registerRepalleter(Blueprint.location("simple"), SimpleStructureRepaletter.CODEC, SimpleStructureRepaletter.CODEC);
		registerRepalleter(Blueprint.location("weighted"), WeightedStructureRepaletter.CODEC, WeightedStructureRepaletter.CODEC);
		registerCondition(Blueprint.location("not"), NotStructureCondition.CODEC);
		registerCondition(Blueprint.location("and"), AndStructureCondition.CODEC);
		registerCondition(Blueprint.location("or"), OrStructureCondition.CODEC);
		registerCondition(Blueprint.location("biome"), BiomeStructureCondition.CODEC);
		registerCondition(Blueprint.location("chance"), ChanceStructureCondition.CODEC);
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
	 * Registers an identifiable {@link MapCodec} instance to use for serializing and deserializing {@link StructureRepaletter} instances.
	 *
	 * @param name  A {@link ResourceLocation} instance to use for identifying the {@link MapCodec} instance.
	 * @param codec The {@link MapCodec} instance to register.
	 */
	public static synchronized void registerRepalleter(ResourceLocation name, MapCodec<? extends StructureRepaletter> codec) {
		REPALLETER_SERIALIZERS.register(name, codec);
	}

	/**
	 * Registers an identifiable {@link MapCodec} instance to use for serializing and deserializing {@link StructureRepaletter.Replacer} instances.
	 *
	 * @param name  A {@link ResourceLocation} instance to use for identifying the {@link MapCodec} instance.
	 * @param codec The {@link MapCodec} instance to register.
	 */
	public static synchronized void registerReplacer(ResourceLocation name, MapCodec<? extends StructureRepaletter.Replacer> codec) {
		REPLACER_SERIALIZERS.register(name, codec);
	}

	/**
	 * Combines the functionality of {@link #registerRepalleter(ResourceLocation, MapCodec)} and {@link #registerReplacer(ResourceLocation, MapCodec)}.
	 *
	 * @param name          A {@link ResourceLocation} instance to use for identifying the {@link MapCodec} instances.
	 * @param codec         The repalleter {@link MapCodec} instance to register.
	 * @param replacerCodec The replacer {@link MapCodec} instance to register.
	 */
	public static void registerRepalleter(ResourceLocation name, MapCodec<? extends StructureRepaletter> codec, MapCodec<? extends StructureRepaletter.Replacer> replacerCodec) {
		registerRepalleter(name, codec);
		registerReplacer(name, replacerCodec);
	}

	/**
	 * Registers an identifiable {@link MapCodec} instance to use for serializing and deserializing {@link StructureRepaletter.Condition} instances.
	 *
	 * @param name  A {@link ResourceLocation} instance to use for identifying the {@link MapCodec} instance.
	 * @param codec The {@link MapCodec} instance to register.
	 */
	public static synchronized void registerCondition(ResourceLocation name, MapCodec<? extends StructureRepaletter.Condition> codec) {
		CONDITION_SERIALIZERS.register(name, codec);
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
	 * @param entries   A list of entries to use.
	 * @param pieceType The structure piece currently being generated.
	 */
	public static void updateActiveRepaletters(@Nullable ArrayList<Entry> entries, @Nullable Holder<StructurePieceType> pieceType) {
		ActiveData activeData = ACTIVE_DATA.get();
		if (entries == null) {
			activeData.reset();
			return;
		}
		ArrayList<StructureRepaletter.Replacer> activeReplacers = new ArrayList<>();
		if (pieceType != null) {
			for (var entry : entries) {
				var pieces = entry.pieces();
				if (pieces.isEmpty() || pieces.get().contains(pieceType)) {
					activeReplacers.add(entry.replacer());
				}
			}
		} else {
			for (var entry : entries) {
				if (entry.shouldApplyToAfterPlace()) activeReplacers.add(entry.replacer());
			}
		}
		activeData.replacers = activeReplacers.toArray(StructureRepaletter.Replacer[]::new);
	}

	/**
	 * Gets the "repalleted" {@link BlockState} instance for a given {@link BlockState} instance.
	 *
	 * @param level A {@link ServerLevelAccessor} instance to use for the {@link StructureRepaletter.Replacer} instances.
	 * @param state A {@link BlockState} instance to potentially replace.
	 * @return The "repalleted" {@link BlockState} instance for a given {@link BlockState} instance.
	 */
	public static BlockState getBlockState(ServerLevelAccessor level, BlockState state) {
		ActiveData activeData = ACTIVE_DATA.get();
		var replacers = activeData.replacers;
		int length = replacers.length;
		if (length == 0) return state;
		RandomSource random = activeData.random;
		for (int i = 0; i < length; i++) {
			BlockState replacement = replacers[i].getReplacement(level, state, random);
			if (replacement != null) return replacement;
		}
		return state;
	}

	/**
	 * Clears the repaletters list.
	 */
	public static void reset() {
		ACTIVE_DATA.get().reset();
	}

	/**
	 * Record class for storing the data needed for correctly assigning {@link StructureRepaletter.Replacer} instances.
	 *
	 * @author SmellyModder (Luke Tonon)
	 * @see StructureRepaletterEntry
	 * @see StructureRepaletter.Replacer
	 */
	public record Entry(Optional<HolderSet<StructurePieceType>> pieces, boolean shouldApplyToAfterPlace, StructureRepaletter.Replacer replacer) {
		public static final String KEY = Blueprint.MOD_ID + ":repaletters";
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> {
			return instance.group(
					RegistryCodecs.homogeneousList(Registries.STRUCTURE_PIECE).optionalFieldOf("pieces").forGetter(Entry::pieces),
					Codec.BOOL.optionalFieldOf("should_apply_to_after_place", false).forGetter(Entry::shouldApplyToAfterPlace),
					StructureRepaletter.Replacer.CODEC.fieldOf("replacer").forGetter(Entry::replacer)
			).apply(instance, Entry::new);
		});
	}

	/**
	 * The class for storing the data needed for applying correctly assigned {@link StructureRepaletter.Replacer} instances.
	 *
	 * @author SmellyModder (Luke Tonon)
	 * @see StructureRepaletterManager
	 * @see StructureRepaletter.Replacer
	 */
	private static final class ActiveData {
		private static final StructureRepaletter.Replacer[] EMPTY_REPLACER = new StructureRepaletter.Replacer[0];
		private StructureRepaletter.Replacer[] replacers = EMPTY_REPLACER;
		private RandomSource random;

		private void reset() {
			this.replacers = EMPTY_REPLACER;
		}
	}
}
