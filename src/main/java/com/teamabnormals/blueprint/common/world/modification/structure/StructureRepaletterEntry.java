package com.teamabnormals.blueprint.common.world.modification.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.NullableFieldCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.Optional;

/**
 * The record class for storing the data for an "unassigned" {@link StructureRepaletter} instance.
 * <p>A {@link HolderSet} of {@link Structure} is stored for selecting structures.</p>
 * <p>An optional {@link HolderSet} of {@link StructurePieceType} is stored for selecting specific piece types if desired.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepalleterManager
 */
public record StructureRepaletterEntry(HolderSet<Structure> structures, Optional<HolderSet<StructurePieceType>> pieces, boolean shouldApplyToAfterPlace, int priority, StructureRepaletter repaletter) {
	public static final Codec<StructureRepaletterEntry> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				RegistryCodecs.homogeneousList(Registries.STRUCTURE).fieldOf("structures").forGetter(entry -> entry.structures),
				RegistryCodecs.homogeneousList(Registries.STRUCTURE_PIECE).optionalFieldOf("pieces").forGetter(entry -> entry.pieces),
				Codec.BOOL.optionalFieldOf("should_apply_to_after_place", false).forGetter(entry -> entry.shouldApplyToAfterPlace),
				NullableFieldCodec.nullable("priority", Codec.INT, 100).forGetter(entry -> entry.priority),
				StructureRepaletter.CODEC.fieldOf("repaletter").forGetter(entry -> entry.repaletter)
		).apply(instance, StructureRepaletterEntry::new);
	});

	public StructureRepaletterEntry(HolderSet<Structure> structures, Optional<HolderSet<StructurePieceType>> pieces, boolean shouldApplyToAfterPlace, StructureRepaletter repaletter) {
		this(structures, pieces, shouldApplyToAfterPlace, 100, repaletter);
	}
}
