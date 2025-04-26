package com.teamabnormals.blueprint.common.world.modification.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.NullableFieldCodec;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The record class for storing the data for an "unassigned" {@link StructureRepaletter} instance.
 * <p>A {@link HolderSet} of {@link Structure} is stored for selecting structures.</p>
 * <p>An optional {@link HolderSet} of {@link StructurePieceType} is stored for selecting specific piece types if desired.</p>
 * <p>An optional {@link StructureRepaletter.Condition} is used for having the repaletter only apply under specific conditions.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletterManager
 */
public record StructureRepaletterEntry(HolderSet<Structure> structures, Optional<HolderSet<StructurePieceType>> pieces, boolean shouldApplyToAfterPlace, int priority, Optional<StructureRepaletter.Condition> condition, List<StructureRepaletter> repaletters) {
	public static final Codec<StructureRepaletterEntry> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				RegistryCodecs.homogeneousList(Registries.STRUCTURE).fieldOf("structures").forGetter(entry -> entry.structures),
				RegistryCodecs.homogeneousList(Registries.STRUCTURE_PIECE).optionalFieldOf("pieces").forGetter(entry -> entry.pieces),
				Codec.BOOL.optionalFieldOf("should_apply_to_after_place", false).forGetter(entry -> entry.shouldApplyToAfterPlace),
				NullableFieldCodec.nullable("priority", Codec.INT, 100).forGetter(entry -> entry.priority),
				StructureRepaletter.Condition.CODEC.optionalFieldOf("condition").forGetter(entry -> entry.condition),
				Codec.either(StructureRepaletter.CODEC, ExtraCodecs.nonEmptyList(StructureRepaletter.CODEC.listOf()))
						.xmap(
								either -> either.map(List::of, repalleters -> repalleters),
								repaletters -> repaletters.size() == 1 ? Either.left(repaletters.getFirst()) : Either.right(repaletters)
						).fieldOf("repaletter").forGetter(entry -> entry.repaletters)
		).apply(instance, StructureRepaletterEntry::new);
	});

	/**
	 * Constructs a new {@link Builder} instance.
	 *
	 * @return A new {@link Builder} instance.
	 */
	public static Builder repalette() {
		return new Builder();
	}

	public static SimpleStructureRepaletter simple(Block replacesBlock, Block replacesWith) {
		return new SimpleStructureRepaletter(replacesBlock, replacesWith);
	}

	public static SimpleTagStructureRepaletter simple(TagKey<Block> replacesBlocks, Block replacesWith) {
		return new SimpleTagStructureRepaletter(replacesBlocks, replacesWith);
	}

	@SafeVarargs
	public static WeightedStructureRepaletter weighted(Block replacesBlock, WeightedEntry.Wrapper<Block>... weightedPairs) {
		return new WeightedStructureRepaletter(replacesBlock, WeightedRandomList.create(List.of(weightedPairs)));
	}

	@SafeVarargs
	public static WeightedTagStructureRepaletter weighted(TagKey<Block> replacesBlocks, WeightedEntry.Wrapper<Block>... weightedPairs) {
		return new WeightedTagStructureRepaletter(replacesBlocks, WeightedRandomList.create(List.of(weightedPairs)));
	}

	@SafeVarargs
	public static HolderSet<Structure> holder(HolderGetter<Structure> structures, ResourceKey<Structure>... keys) {
		return HolderSet.direct(Stream.of(keys).map(structures::getOrThrow).collect(Collectors.toList()));
	}

	public StructureRepaletterEntry(HolderSet<Structure> structures, Optional<HolderSet<StructurePieceType>> pieces, boolean shouldApplyToAfterPlace, int priority, Optional<StructureRepaletter.Condition> condition, StructureRepaletter repaletter) {
		this(structures, pieces, shouldApplyToAfterPlace, priority, condition, List.of(repaletter));
	}

	public StructureRepaletterEntry(HolderSet<Structure> structures, Optional<HolderSet<StructurePieceType>> pieces, boolean shouldApplyToAfterPlace, int priority, StructureRepaletter.Condition condition, StructureRepaletter repaletter) {
		this(structures, pieces, shouldApplyToAfterPlace, priority, Optional.of(condition), repaletter);
	}

	public StructureRepaletterEntry(HolderSet<Structure> structures, Optional<HolderSet<StructurePieceType>> pieces, boolean shouldApplyToAfterPlace, StructureRepaletter repaletter) {
		this(structures, pieces, shouldApplyToAfterPlace, 100, Optional.empty(), repaletter);
	}

	public StructureRepaletterEntry(HolderSet<Structure> structures, Optional<HolderSet<StructurePieceType>> pieces, boolean shouldApplyToAfterPlace, StructureRepaletter.Condition condition, StructureRepaletter repaletter) {
		this(structures, pieces, shouldApplyToAfterPlace, 100, Optional.of(condition), repaletter);
	}

	/**
	 * Builder class for {@link StructureRepaletterEntry}.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Builder {
		private Optional<StructureRepaletter.Condition> condition = Optional.empty();
		private Optional<HolderSet<StructurePieceType>> pieces = Optional.empty();
		private final List<StructureRepaletter> repaletters = new ArrayList<>();
		private boolean applyToAfterPlace;
		private int priority = 100;

		/**
		 * Sets the condition to use for determining when to repalette.
		 *
		 * @param condition The {@link StructureRepaletter.Condition} instance to use.
		 * @return This builder.
		 */
		public Builder condition(StructureRepaletter.Condition condition) {
			this.condition = Optional.of(condition);
			return this;
		}

		/**
		 * Sets the set of structure pieces to only repalette.
		 *
		 * @param pieces Holder set of {@link StructurePieceType} to only repalette.
		 * @return This builder.
		 */
		public Builder pieces(HolderSet<StructurePieceType> pieces) {
			this.pieces = Optional.of(pieces);
			return this;
		}

		/**
		 * Adds repaletters to use.
		 *
		 * @param repaletters An array of {@link StructureRepaletter} instances to use.
		 * @return This builder.
		 */
		public Builder repaletters(StructureRepaletter... repaletters) {
			var list = this.repaletters;
			for (StructureRepaletter repaletter : repaletters) {
				list.add(repaletter);
			}
			return this;
		}

		/**
		 * Makes the repaletters apply after piece placement.
		 *
		 * @return This builder.
		 */
		public Builder applyToAfterPlace() {
			this.applyToAfterPlace = true;
			return this;
		}

		/**
		 * Sets the priority for ordering this entry with others.
		 *
		 * @param priority Priority to use.
		 * @return This builder.
		 */
		public Builder priority(int priority) {
			this.priority = priority;
			return this;
		}

		/**
		 * Builds a {@link StructureRepaletterEntry} instance configured by this builder that targets a holder set of structures.
		 *
		 * @param structures A holder set of {@link Structure} to target.
		 * @return A new {@link StructureRepaletterEntry} instance.
		 */
		public StructureRepaletterEntry select(HolderSet<Structure> structures) {
			return new StructureRepaletterEntry(structures, this.pieces, this.applyToAfterPlace, this.priority, this.condition, this.repaletters);
		}
	}
}
