package com.teamabnormals.blueprint.common.advancement.modification;

import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A mutable version of the {@link Advancement.Builder} class, intended for advancement modifiers.
 *
 * @author SmellyModder (Luke Tonon)
 */
// TODO: Not great, but Advancement Modifiers will be replaced by Remolders soon
public final class BlueprintAdvancementBuilder {
	public Optional<ResourceLocation> parent = Optional.empty();
	public Optional<DisplayInfo> display = Optional.empty();
	public AdvancementRewards rewards = AdvancementRewards.EMPTY;
	public final HashMap<String, Criterion<?>> criteria = new HashMap<>();
	public Optional<AdvancementRequirements> requirements = Optional.empty();
	public AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;
	public boolean sendsTelemetryEvent;

	public BlueprintAdvancementBuilder parent(ResourceLocation location) {
		this.parent = Optional.of(location);
		return this;
	}

	public BlueprintAdvancementBuilder display(DisplayInfo info) {
		this.display = Optional.of(info);
		return this;
	}

	public BlueprintAdvancementBuilder rewards(AdvancementRewards.Builder builder) {
		return this.rewards(builder.build());
	}

	public BlueprintAdvancementBuilder rewards(AdvancementRewards rewards) {
		this.rewards = rewards;
		return this;
	}

	public BlueprintAdvancementBuilder addCriterion(String key, Criterion<?> criterion) {
		this.criteria.put(key, criterion);
		return this;
	}

	public BlueprintAdvancementBuilder requirements(AdvancementRequirements.Strategy strategy) {
		this.requirementsStrategy = strategy;
		return this;
	}

	public BlueprintAdvancementBuilder requirements(AdvancementRequirements requirements) {
		this.requirements = Optional.of(requirements);
		return this;
	}

	public BlueprintAdvancementBuilder sendsTelemetryEvent() {
		this.sendsTelemetryEvent = true;
		return this;
	}

	public AdvancementHolder build(ResourceLocation location) {
		Map<String, Criterion<?>> map = this.criteria;
		AdvancementRequirements advancementrequirements = this.requirements.orElseGet(() -> this.requirementsStrategy.create(map.keySet()));
		return new AdvancementHolder(
				location, new Advancement(this.parent, this.display, this.rewards, map, advancementrequirements, this.sendsTelemetryEvent)
		);
	}
}
