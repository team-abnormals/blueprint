package com.teamabnormals.blueprint.core.api;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class BlueprintRabbitTypes {
	private static final Set<BlueprintRabbitType> RABBIT_TYPES = new ObjectArraySet<>();

	public static synchronized BlueprintRabbitType register(int id, ResourceLocation name, ResourceLocation texturePath) {
		BlueprintRabbitType type = new BlueprintRabbitType(id, name, texturePath);
		RABBIT_TYPES.add(type);
		return type;
	}

	public static synchronized BlueprintRabbitType register(int id, ResourceLocation name) {
		return register(id, name, new ResourceLocation(name.getNamespace(), "textures/entity/rabbit/" + name.getPath() + ".png"));
	}

	public record BlueprintRabbitType(int id, ResourceLocation name, ResourceLocation textureLocation) {
	}

	public static ImmutableList<BlueprintRabbitType> values() {
		return ImmutableList.copyOf(RABBIT_TYPES);
	}
}