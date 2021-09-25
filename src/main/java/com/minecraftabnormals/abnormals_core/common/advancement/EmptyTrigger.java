package com.minecraftabnormals.abnormals_core.common.advancement;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

import net.minecraft.advancements.CriterionTrigger.Listener;

/**
 * @author - SmellyModder(Luke Tonon)
 */
public final class EmptyTrigger implements CriterionTrigger<EmptyTrigger.Instance> {
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
	public void addPlayerListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
		Listeners listeners = this.listeners.computeIfAbsent(playerAdvancements, Listeners::new);
		listeners.add(listener);
	}

	@Override
	public void removePlayerListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
		Listeners listeners = this.listeners.get(playerAdvancements);
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				this.listeners.remove(playerAdvancements);
			}
		}
	}

	@Override
	public void removePlayerListeners(PlayerAdvancements playerAdvancements) {
		this.listeners.remove(playerAdvancements);
	}

	@Override
	public Instance createInstance(JsonObject object, DeserializationContext conditions) {
		return new Instance(this.id);
	}

	public void trigger(ServerPlayer player) {
		Listeners listeners = this.listeners.get(player.getAdvancements());
		if (listeners != null) {
			listeners.trigger();
		}
	}

	public static class Instance implements CriterionTriggerInstance {
		private final ResourceLocation id;

		Instance(ResourceLocation id) {
			super();
			this.id = id;
		}

		@Override
		public ResourceLocation getCriterion() {
			return this.id;
		}

		@Override
		public JsonObject serializeToJson(SerializationContext conditions) {
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
			List<Listener<Instance>> listenerList = new ArrayList<>(this.listeners);
			for (Listener<Instance> instanceListener : listenerList) {
				instanceListener.run(this.advancements);
			}
		}
	}
}