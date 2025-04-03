package core.registry;

import core.BlueprintTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

public final class TestNoises {
	public static final ResourceKey<NoiseParameters> HOE_DIAMONDS = createKey("hoe_diamonds");

	public static void bootstrap(BootstrapContext<NoiseParameters> context) {
		context.register(HOE_DIAMONDS, new NoiseParameters(-5, 1.0D));
	}

	public static ResourceKey<NoiseParameters> createKey(String name) {
		return ResourceKey.create(Registries.NOISE, BlueprintTest.location(name));
	}
}