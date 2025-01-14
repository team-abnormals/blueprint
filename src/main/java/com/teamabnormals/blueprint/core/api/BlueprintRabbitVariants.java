package com.teamabnormals.blueprint.core.api;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;
import java.util.function.Predicate;

public class BlueprintRabbitVariants {
	private static final Set<BlueprintRabbitVariant> RABBIT_VARIANTS = new ObjectArraySet<>();

	public static synchronized BlueprintRabbitVariant register(int id, ResourceLocation name, ResourceLocation texturePath, Predicate<Pair<ServerLevelAccessor, BlockPos>> predicate) {
		BlueprintRabbitVariant type = new BlueprintRabbitVariant(id, name, texturePath, predicate);
		RABBIT_VARIANTS.add(type);
		return type;
	}

	public static synchronized BlueprintRabbitVariant register(int id, ResourceLocation name, Predicate<Pair<ServerLevelAccessor, BlockPos>> predicate) {
		return register(id, name, ResourceLocation.fromNamespaceAndPath(name.getNamespace(), "textures/entity/rabbit/" + name.getPath() + ".png"), predicate);
	}

	public static Holder<Biome> getBiome(Pair<ServerLevelAccessor, BlockPos> pair) {
		return pair.getFirst().getBiome(pair.getSecond());
	}

	public record BlueprintRabbitVariant(int id, ResourceLocation name, ResourceLocation textureLocation, Predicate<Pair<ServerLevelAccessor, BlockPos>> predicate) {
		public boolean test(ServerLevelAccessor level, BlockPos pos) {
			return this.predicate.test(Pair.of(level, pos));
		}
	}

	public static ImmutableList<BlueprintRabbitVariant> values() {
		return ImmutableList.copyOf(RABBIT_VARIANTS);
	}

	public static class BlueprintRabbitGroupData extends AgeableMob.AgeableMobGroupData {
		public final int variant;

		public BlueprintRabbitGroupData(int id) {
			super(1.0F);
			this.variant = id;
		}
	}
}