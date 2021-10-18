package com.teamabnormals.blueprint.core.events;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event fired for when an {@link Advancement} is being deserialized and built.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class AdvancementBuildingEvent extends Event {
	private final Advancement.Builder builder;
	private final ResourceLocation location;

	private AdvancementBuildingEvent(Advancement.Builder builder, ResourceLocation location) {
		this.builder = builder;
		this.location = location;
	}

	/**
	 * Fires the {@link AdvancementBuildingEvent} for a given {@link Advancement.Builder} and {@link ResourceLocation} advancement name.
	 *
	 * @param builder  The {@link Advancement.Builder} being built.
	 * @param location The {@link ResourceLocation} of the {@link Advancement} being built.
	 */
	public static void onBuildingAdvancement(Advancement.Builder builder, ResourceLocation location) {
		MinecraftForge.EVENT_BUS.post(new AdvancementBuildingEvent(builder, location));
	}

	/**
	 * Gets the {@link Advancement.Builder} of this event.
	 *
	 * @return The {@link Advancement.Builder} of this event.
	 */
	public Advancement.Builder getBuilder() {
		return this.builder;
	}

	/**
	 * Gets the {@link ResourceLocation} name of the {@link Advancement} for this event.
	 *
	 * @return The {@link ResourceLocation} name of the {@link Advancement} for this event.
	 */
	public ResourceLocation getLocation() {
		return this.location;
	}
}
