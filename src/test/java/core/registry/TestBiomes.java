package core.registry;

import com.minecraftabnormals.abnormals_core.common.world.modification.BiomeAmbienceModifier;
import com.minecraftabnormals.abnormals_core.common.world.modification.BiomeModificationPredicates;
import com.minecraftabnormals.abnormals_core.core.util.registry.BiomeSubRegistryHelper;
import core.ACTest;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestBiomes {
	public static final BiomeSubRegistryHelper HELPER = ACTest.REGISTRY_HELPER.getBiomeSubHelper();

	public static final BiomeSubRegistryHelper.KeyedBiome TEST_AMBIENCE = HELPER.createBiomeWithModifiers("test_ambience", TestBiomes::createAmbienceBiome, (biomeRegistryObject, biomeModificationManager) -> {
		biomeModificationManager.addModifier(BiomeAmbienceModifier.createAmbienceReplacer(BiomeModificationPredicates.forBiome(biomeRegistryObject), () -> new BiomeAmbience.Builder().setWaterColor(415924).setWaterFogColor(329011).setFogColor(1268463).withSkyColor(1).setParticle(new ParticleEffectAmbience(ParticleTypes.ENCHANT, 0.00725F)).setAmbientSound(TestSounds.AMBIENCE_TEST.get()).setMoodSound(new MoodSoundAmbience(TestSounds.AMBIENCE_TEST.get(), 6000, 8, 2.0D)).setMusic(BackgroundMusicTracks.DRAGON_FIGHT_MUSIC).build()));
	});

	private static Biome createAmbienceBiome() {
		return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.PLAINS).depth(0.125F).scale(0.05F).temperature(0.8F).downfall(0.4F).setEffects(new BiomeAmbience.Builder().setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(1).build()).withMobSpawnSettings(new MobSpawnInfo.Builder().copy()).withGenerationSettings((new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j).build()).build();
	}
}
