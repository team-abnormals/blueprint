package com.teamabnormals.blueprint.common.world.biome.modification;

import com.teamabnormals.blueprint.common.world.biome.modification.modifiers.BiomeSpawnCostsModifier;
import com.teamabnormals.blueprint.common.world.biome.modification.modifiers.BiomeSpawnersModifier;
import com.teamabnormals.blueprint.core.util.modification.ModifierRegistry;
import net.minecraftforge.event.world.BiomeLoadingEvent;

/**
 * The registry class for {@link com.teamabnormals.blueprint.common.world.biome.modification.modifiers.IBiomeModifier} implementations.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeModifiers {
	public static final ModifierRegistry<BiomeLoadingEvent, Void, Void> REGISTRY = new ModifierRegistry<>();

	public static final BiomeSpawnCostsModifier SPAWN_COSTS = REGISTRY.register("spawn_costs", new BiomeSpawnCostsModifier());
	public static final BiomeSpawnersModifier SPAWNERS = REGISTRY.register("spawners", new BiomeSpawnersModifier());
}
