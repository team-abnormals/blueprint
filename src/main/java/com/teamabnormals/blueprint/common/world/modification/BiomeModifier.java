package com.teamabnormals.blueprint.common.world.modification;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * An implementation class of {@link IBiomeModifier}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class BiomeModifier implements IBiomeModifier {
	private final BiPredicate<ResourceKey<Biome>, Biome> shouldModify;
	private final Consumer<BiomeModificationContext> modifier;

	public BiomeModifier(BiPredicate<ResourceKey<Biome>, Biome> shouldModify, Consumer<BiomeModificationContext> modifier) {
		this.shouldModify = shouldModify;
		this.modifier = modifier;
	}

	@Override
	public void accept(BiomeModificationContext context) {
		this.modifier.accept(context);
	}

	@Override
	public boolean test(BiomeModificationContext context) {
		return this.shouldModify.test(context.resourceKey, context.biome);
	}
}
