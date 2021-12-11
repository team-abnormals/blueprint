package com.teamabnormals.blueprint.common.world.biome.modification;

import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.common.world.biome.modification.modifiers.*;
import com.teamabnormals.blueprint.core.util.modification.ModifierRegistry;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraftforge.event.world.BiomeLoadingEvent;

/**
 * The registry class for {@link com.teamabnormals.blueprint.common.world.biome.modification.modifiers.IBiomeModifier} implementations.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BiomeModifiers {
	public static final ModifierRegistry<BiomeLoadingEvent, RegistryWriteOps<JsonElement>, RegistryReadOps<JsonElement>> REGISTRY = new ModifierRegistry<>();

	public static final BiomeSpawnCostsModifier SPAWN_COSTS = REGISTRY.register("spawn_costs", new BiomeSpawnCostsModifier());
	public static final BiomeSpawnersModifier SPAWNERS = REGISTRY.register("spawners", new BiomeSpawnersModifier());
	public static final BiomeFeaturesModifier FEATURES = REGISTRY.register("features", new BiomeFeaturesModifier());
	public static final BiomeCarversModifier CARVERS = REGISTRY.register("carvers", new BiomeCarversModifier());
}
