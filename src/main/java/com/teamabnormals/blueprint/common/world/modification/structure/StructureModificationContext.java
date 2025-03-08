package com.teamabnormals.blueprint.common.world.modification.structure;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.function.Supplier;

/**
 * Provides contextual data for modifying an individual generated structure.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class StructureModificationContext {
	private final Structure.GenerationContext generationContext;
	private final BlockPos position;
	private final Supplier<Holder<Biome>> biome;

	public StructureModificationContext(Structure.GenerationContext context, BlockPos position) {
		this.generationContext = context;
		this.position = position;
		this.biome = Suppliers.memoize(() -> {
			return context.biomeSource().getNoiseBiome(
					QuartPos.fromBlock(position.getX()),
					QuartPos.fromBlock(position.getY()),
					QuartPos.fromBlock(position.getZ()),
					context.randomState().sampler()
			);
		});
	}

	/**
	 * Gets the {@link #generationContext}.
	 *
	 * @return The {@link #generationContext}.
	 */
	public Structure.GenerationContext getGenerationContext() {
		return this.generationContext;
	}

	/**
	 * Gets the {@link #position} associated with the structure's generation stub.
	 *
	 * @return The {@link #position}.
	 */
	public BlockPos getPosition() {
		return this.position;
	}

	/**
	 * Gets access to the biome at the structure's generation stub.
	 *
	 * @return Access to the biome at the structure's generation stub.
	 */
	public Supplier<Holder<Biome>> biome() {
		return this.biome;
	}
}
