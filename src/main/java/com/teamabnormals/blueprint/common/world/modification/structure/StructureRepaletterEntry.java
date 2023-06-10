package com.teamabnormals.blueprint.common.world.modification.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.NullableFieldCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * The record class for storing the data for an "unassigned" {@link StructureRepaletter} instance.
 * <p>A {@link HolderSet} instance is stored for selecting structures.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepalleterManager
 */
public record StructureRepaletterEntry(HolderSet<Structure> structures, int priority, StructureRepaletter repaletter) {
	public StructureRepaletterEntry(HolderSet<Structure> structures, StructureRepaletter repaletter) {
		this(structures, 100, repaletter);
	}

	public static final Codec<StructureRepaletterEntry> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				RegistryCodecs.homogeneousList(Registries.STRUCTURE).fieldOf("structures").forGetter(entry -> entry.structures),
				NullableFieldCodec.nullable("priority", Codec.INT, 100).forGetter(entry -> entry.priority),
				StructureRepaletter.CODEC.fieldOf("repaletter").forGetter(entry -> entry.repaletter)
		).apply(instance, StructureRepaletterEntry::new);
	});
}
