package core.registry;

import com.minecraftabnormals.abnormals_core.common.world.modification.BiomeAmbienceModifier;
import com.minecraftabnormals.abnormals_core.common.world.modification.BiomeModificationPredicates;
import com.minecraftabnormals.abnormals_core.core.util.registry.BiomeSubRegistryHelper;
import core.ACTest;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestBiomes {
	public static final BiomeSubRegistryHelper HELPER = ACTest.REGISTRY_HELPER.getBiomeSubHelper();

	public static final BiomeSubRegistryHelper.KeyedBiome TEST_AMBIENCE = HELPER.createBiomeWithModifiers("test_ambience", TestBiomes::createAmbienceBiome, (biomeRegistryObject, biomeModificationManager) -> {
		biomeModificationManager.addModifier(BiomeAmbienceModifier.createAmbienceReplacer(BiomeModificationPredicates.forBiome(biomeRegistryObject), () -> new BiomeAmbience.Builder().waterColor(415924).waterFogColor(329011).fogColor(1268463).skyColor(1).ambientParticle(new ParticleEffectAmbience(ParticleTypes.ENCHANT, 0.00725F)).ambientLoopSound(TestSounds.AMBIENCE_TEST.get()).ambientMoodSound(new MoodSoundAmbience(TestSounds.AMBIENCE_TEST.get(), 6000, 8, 2.0D)).backgroundMusic(BackgroundMusicTracks.END_BOSS).build()));
	});

	public static final BiomeSubRegistryHelper.KeyedBiome TEST_OCEAN = HELPER.createBiome("test_ocean", TestBiomes::createOceanBiome);

	public static final BiomeSubRegistryHelper.KeyedBiome TEST_NETHER = HELPER.createBiome("test_nether", TestBiomes::createNetherBiome);

	private static Biome createAmbienceBiome() {
		return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.PLAINS).depth(0.125F).scale(0.05F).temperature(0.8F).downfall(0.4F).specialEffects(new BiomeAmbience.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(1).build()).mobSpawnSettings(new MobSpawnInfo.Builder().build()).generationSettings((new BiomeGenerationSettings.Builder()).surfaceBuilder(ConfiguredSurfaceBuilders.GRASS).build()).build();
	}

	private static Biome createOceanBiome() {
		return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).biomeCategory(Biome.Category.OCEAN).depth(-1.0F).scale(0.15F).temperature(1F).downfall(0.1F).specialEffects(new BiomeAmbience.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(1).build()).mobSpawnSettings(new MobSpawnInfo.Builder().build()).generationSettings(new BiomeGenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.OCEAN_SAND).build()).build();
	}

	private static Biome createNetherBiome() {
		MobSpawnInfo mobspawninfo = (new MobSpawnInfo.Builder()).addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 1, 4, 4)).addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.STRIDER, 60, 1, 2)).addMobCharge(EntityType.ENDERMAN, 1.0D, 0.12D).build();
		BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).surfaceBuilder(ConfiguredSurfaceBuilders.WARPED_FOREST).addStructureStart(StructureFeatures.NETHER_BRIDGE).addStructureStart(StructureFeatures.BASTION_REMNANT).addStructureStart(StructureFeatures.RUINED_PORTAL_NETHER).addCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.NETHER_CAVE).addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
		DefaultBiomeFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
		biomegenerationsettings$builder.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_OPEN).addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE).addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED).addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.WARPED_FOREST_VEGETATION).addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.NETHER_SPROUTS).addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TWISTING_VINES);
		DefaultBiomeFeatures.addNetherDefaultOres(biomegenerationsettings$builder);
		return (new Biome.Builder()).precipitation(Biome.RainType.NONE).biomeCategory(Biome.Category.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeAmbience.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(1705242).skyColor(getSkyColorWithTemperatureModifier(2.0F)).ambientParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.01428F)).ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP).ambientMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111D)).backgroundMusic(BackgroundMusicTracks.createGameMusic(SoundEvents.MUSIC_BIOME_WARPED_FOREST)).build()).mobSpawnSettings(mobspawninfo).generationSettings(biomegenerationsettings$builder.build()).build();
	}

	private static int getSkyColorWithTemperatureModifier(float temperature) {
		float lvt_1_1_ = temperature / 3.0F;
		lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
	}
}
