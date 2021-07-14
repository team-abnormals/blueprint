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
		biomeModificationManager.addModifier(BiomeAmbienceModifier.createAmbienceReplacer(BiomeModificationPredicates.forBiome(biomeRegistryObject), () -> new BiomeAmbience.Builder().setWaterColor(415924).setWaterFogColor(329011).setFogColor(1268463).withSkyColor(1).setParticle(new ParticleEffectAmbience(ParticleTypes.ENCHANT, 0.00725F)).setAmbientSound(TestSounds.AMBIENCE_TEST.get()).setMoodSound(new MoodSoundAmbience(TestSounds.AMBIENCE_TEST.get(), 6000, 8, 2.0D)).setMusic(BackgroundMusicTracks.DRAGON_FIGHT_MUSIC).build()));
	});

	public static final BiomeSubRegistryHelper.KeyedBiome TEST_OCEAN = HELPER.createBiome("test_ocean", TestBiomes::createOceanBiome);

	public static final BiomeSubRegistryHelper.KeyedBiome TEST_NETHER = HELPER.createBiome("test_nether", TestBiomes::createNetherBiome);

	private static Biome createAmbienceBiome() {
		return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.PLAINS).depth(0.125F).scale(0.05F).temperature(0.8F).downfall(0.4F).setEffects(new BiomeAmbience.Builder().setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(1).build()).withMobSpawnSettings(new MobSpawnInfo.Builder().copy()).withGenerationSettings((new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j).build()).build();
	}

	private static Biome createOceanBiome() {
		return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.OCEAN).depth(-1.0F).scale(0.15F).temperature(1F).downfall(0.1F).setEffects(new BiomeAmbience.Builder().setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(1).build()).withMobSpawnSettings(new MobSpawnInfo.Builder().copy()).withGenerationSettings(new BiomeGenerationSettings.Builder().withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244185_q).build()).build();
	}

	private static Biome createNetherBiome() {
		MobSpawnInfo mobspawninfo = (new MobSpawnInfo.Builder()).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 1, 4, 4)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.STRIDER, 60, 1, 2)).withSpawnCost(EntityType.ENDERMAN, 1.0D, 0.12D).copy();
		BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244190_v).withStructure(StructureFeatures.FORTRESS).withStructure(StructureFeatures.BASTION_REMNANT).withStructure(StructureFeatures.RUINED_PORTAL_NETHER).withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.field_243772_f).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
		DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
		biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_OPEN).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.WARPED_FOREST_VEGETATION).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.NETHER_SPROUTS).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TWISTING_VINES);
		DefaultBiomeFeatures.withCommonNetherBlocks(biomegenerationsettings$builder);
		return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(1705242).withSkyColor(getSkyColorWithTemperatureModifier(2.0F)).setParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.01428F)).setAmbientSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP).setMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0D)).setAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111D)).setMusic(BackgroundMusicTracks.getDefaultBackgroundMusicSelector(SoundEvents.MUSIC_NETHER_WARPED_FOREST)).build()).withMobSpawnSettings(mobspawninfo).withGenerationSettings(biomegenerationsettings$builder.build()).build();
	}

	private static int getSkyColorWithTemperatureModifier(float temperature) {
		float lvt_1_1_ = temperature / 3.0F;
		lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
		return MathHelper.hsvToRGB(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
	}
}
