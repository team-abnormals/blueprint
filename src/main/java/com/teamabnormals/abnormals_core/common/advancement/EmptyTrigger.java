package com.teamabnormals.abnormals_core.common.advancement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

/**
 * @author - SmellyModder(Luke Tonon)
 */
public class EmptyTrigger implements ICriterionTrigger<EmptyTrigger.Instance> {
	private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();
	private final ResourceLocation id;
	
	public EmptyTrigger(ResourceLocation id) {
		this.id = id;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}
	
	@Override
	public void addListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
		Listeners listeners = this.listeners.computeIfAbsent(playerAdvancements, Listeners::new);
		listeners.add(listener);
	}
	
	@Override
	public void removeListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
		Listeners listeners = this.listeners.get(playerAdvancements);
		if(listeners != null) {
			listeners.remove(listener);
			if(listeners.isEmpty()) {
				this.listeners.remove(playerAdvancements);
			}
		}
	}
	
	@Override
	public void removeAllListeners(PlayerAdvancements playerAdvancements) {
		this.listeners.remove(playerAdvancements);
	}
	
	@Override
	public Instance deserialize(JsonObject json, ConditionArrayParser context) {
		return new Instance(this.id);
	}
	
	public void trigger(ServerPlayerEntity player) {
		Listeners listeners = this.listeners.get(player.getAdvancements());
		if(listeners != null) {
			listeners.trigger();
		}
	}
	
	public static class Instance implements ICriterionInstance {
		private final ResourceLocation id;
		
		Instance(ResourceLocation id) {
			super();
			this.id = id;
		}
		
		@Override
		public ResourceLocation getId() {
			return this.id;
		}

		@Override
		public JsonObject serialize(ConditionArraySerializer p_230240_1_) {
			return new JsonObject();
		}
	}
	
	static class Listeners {
		private final Set<Listener<Instance>> listeners = new HashSet<>();
		private final PlayerAdvancements advancements;

		public Listeners(PlayerAdvancements advancements) {
			this.advancements = advancements;
		}

		public void add(Listener<Instance> listener) {
			this.listeners.add(listener);
		}

		public void remove(Listener<Instance> listener) {
			this.listeners.remove(listener);
		}
		
		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void trigger() {
			this.listeners.forEach(listener -> listener.grantCriterion(this.advancements));
		}
	}
}