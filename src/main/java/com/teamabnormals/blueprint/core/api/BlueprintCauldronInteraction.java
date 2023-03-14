package com.teamabnormals.blueprint.core.api;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public record BlueprintCauldronInteraction(ResourceLocation name, Map<Item, CauldronInteraction> map) {
	private static final Set<BlueprintCauldronInteraction> CAULDRON_INTERACTIONS = new ObjectArraySet<>();

	public static final BlueprintCauldronInteraction EMPTY = register(new ResourceLocation("empty"), CauldronInteraction.EMPTY);
	public static final BlueprintCauldronInteraction WATER = register(new ResourceLocation("water"), CauldronInteraction.WATER);
	public static final BlueprintCauldronInteraction LAVA = register(new ResourceLocation("lava"), CauldronInteraction.LAVA);
	public static final BlueprintCauldronInteraction POWDER_SNOW = register(new ResourceLocation("powder_snow"), CauldronInteraction.POWDER_SNOW);

	public static BlueprintCauldronInteraction create(ResourceLocation name, Map<Item, CauldronInteraction> map) {
		return new BlueprintCauldronInteraction(name, map);
	}

	public static synchronized BlueprintCauldronInteraction register(BlueprintCauldronInteraction map) {
		CAULDRON_INTERACTIONS.add(map);
		return map;
	}

	public static BlueprintCauldronInteraction register(ResourceLocation name, Map<Item, CauldronInteraction> map) {
		return register(create(name, map));
	}

	public static void addMoreDefaultInteractions(Item item, CauldronInteraction interaction) {
		values().forEach(blueprintInteraction -> blueprintInteraction.map.put(item, interaction));
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