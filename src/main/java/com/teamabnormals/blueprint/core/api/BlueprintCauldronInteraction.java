package com.teamabnormals.blueprint.core.api;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.Set;

public record BlueprintCauldronInteraction(ResourceLocation name, CauldronInteraction.InteractionMap map) {
	private static final Set<BlueprintCauldronInteraction> CAULDRON_INTERACTIONS = new ObjectArraySet<>();

	public static final BlueprintCauldronInteraction EMPTY = register(ResourceLocation.withDefaultNamespace("empty"), CauldronInteraction.EMPTY);
	public static final BlueprintCauldronInteraction WATER = register(ResourceLocation.withDefaultNamespace("water"), CauldronInteraction.WATER);
	public static final BlueprintCauldronInteraction LAVA = register(ResourceLocation.withDefaultNamespace("lava"), CauldronInteraction.LAVA);
	public static final BlueprintCauldronInteraction POWDER_SNOW = register(ResourceLocation.withDefaultNamespace("powder_snow"), CauldronInteraction.POWDER_SNOW);

	public static BlueprintCauldronInteraction create(ResourceLocation name, CauldronInteraction.InteractionMap map) {
		return new BlueprintCauldronInteraction(name, map);
	}

	public static synchronized BlueprintCauldronInteraction register(BlueprintCauldronInteraction map) {
		CAULDRON_INTERACTIONS.add(map);
		return map;
	}

	public static BlueprintCauldronInteraction register(ResourceLocation name, CauldronInteraction.InteractionMap map) {
		return register(create(name, map));
	}

	public static void addMoreDefaultInteractions(Item item, CauldronInteraction interaction) {
		values().forEach(blueprintInteraction -> blueprintInteraction.map.map().put(item, interaction));
	}

	public static ImmutableList<BlueprintCauldronInteraction> values() {
		return ImmutableList.copyOf(CAULDRON_INTERACTIONS);
	}

	@Nullable
	public static BlueprintCauldronInteraction getTypeFromLocation(ResourceLocation name) {
		for (BlueprintCauldronInteraction interaction : values()) {
			if (interaction.name().equals(name))
				return interaction;
		}
		return null;
	}
}