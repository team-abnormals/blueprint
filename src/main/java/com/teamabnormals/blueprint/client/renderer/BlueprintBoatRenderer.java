package com.teamabnormals.blueprint.client.renderer;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.registry.BlueprintBoatTypes;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

/**
 * The {@link EntityRenderer} responsible for the rendering of Blueprint's boat entities.
 */
@OnlyIn(Dist.CLIENT)
public class BlueprintBoatRenderer extends BoatRenderer {
	private final Map<BlueprintBoatTypes.BlueprintBoatType, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

	public BlueprintBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
		super(context, chest);
		this.boatResources = BlueprintBoatTypes.createBoatResources(context, chest);
	}

	@Override
	public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
		return boat instanceof HasBlueprintBoatType hasBlueprintBoatType ? this.boatResources.get(hasBlueprintBoatType.getBoatType()) : super.getModelWithLocation(boat);
	}
}
