package com.minecraftabnormals.abnormals_core.common.world.modification;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * An implementation class of {@link IBiomeModifier}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class BiomeModifier implements IBiomeModifier {
	private final BiPredicate<RegistryKey<Biome>, Biome> shouldModify;
	private final Consumer<BiomeModificationContext> modifier;

	public BiomeModifier(BiPredicate<RegistryKey<Biome>, Biome> shouldModify, Consumer<BiomeModificationContext> modifier) {
		this.shouldModify = shouldModify;
		this.modifier = modifier;
	}

	@Override
	public void accept(BiomeModificationContext context) {
		this.modifier.accept(context);
	}

	@Override
	public boolean test(BiomeModificationContext context) {
		return this.shouldModify.test(context.registryKey, context.biome);
	}
}
