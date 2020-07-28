package com.minecraftabnormals.abnormals_core.core.library.api;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.google.common.collect.Lists;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.examples.ExampleSoundRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * @author SmellyModder(Luke Tonon)
 */
@SuppressWarnings("unused")
@EventBusSubscriber(modid = AbnormalsCore.MODID, value = Dist.CLIENT)
public class AmbienceMusicPlayer {
	private static final List<IAmbientSoundHandler> SOUND_HANDLERS = Lists.newArrayList();
		
	static {
		//registerBiomeAmbientSoundPlayer(Lists.newArrayList(() -> Biomes.END_HIGHLANDS, () -> Biomes.END_MIDLANDS, () -> Biomes.END_BARRENS), () -> ExampleSoundRegistry.AMBIENCE_EXAMPLE.get(), () -> SoundEvents.ENTITY_SHULKER_TELEPORT, () -> SoundEvents.ENTITY_ENDERMAN_AMBIENT);
	}
	
	/**
	 * Registers an BiomeAmbientSoundPlayer for a specific biome
	 * For more info on the parameters visit {@link https://minecraft.gamepedia.com/Ambience}
	 * @param biome - Biome to play the ambiance in
	 * @param loopSound - The looped sound for the biome
	 * @param additionSound - The common ambient sound(s) for the biome
	 * @param moodSound - The rare/long(plays every 6000-17999 ticks) ambient sound(s) for the biome
	 */
	public static synchronized void registerBiomeAmbientSoundPlayer(List<Supplier<Biome>> biome, Supplier<SoundEvent> loopSound, Supplier<SoundEvent> additionSound, Supplier<SoundEvent> moodSound) {
		SOUND_HANDLERS.add(new BiomeAmbientSoundPlayer(biome, loopSound, additionSound, moodSound));
	}
	
	@SubscribeEvent
	public static void onClientPlayerTick(ClientTickEvent event) {
		if(event.phase == Phase.START) {
			Minecraft mc = Minecraft.getInstance();
			ClientPlayerEntity player = mc.player;
			SoundHandler soundHandler = mc.getSoundHandler();
			SOUND_HANDLERS.forEach((ambientSoundHandler) -> {
				if(player != null && player.canUpdate()) {
					ambientSoundHandler.tickMainAmbience(player, soundHandler);
					ambientSoundHandler.tickAdditions(player, soundHandler);
				}
			});
		}
	}
	
	public static class BiomeAmbientSoundPlayer implements IBiomeAmbientSoundHandler {
		private final List<Supplier<Biome>> biomesToPlayIn;
		private final List<Supplier<SoundEvent>> soundsToPlay = Lists.newArrayList();
		private int delay = 0;
		private int ticksInBiome;
		private int ticksTillNextMood = this.generateTicksTillNextMood();
		private boolean isInBiome;
		
		public BiomeAmbientSoundPlayer(List<Supplier<Biome>> biomesToPlayIn, Supplier<SoundEvent> loopSound, Supplier<SoundEvent> additionSound, Supplier<SoundEvent> moodSound) {
			this.biomesToPlayIn = biomesToPlayIn;
			this.soundsToPlay.add(loopSound);
			this.soundsToPlay.add(additionSound);
			this.soundsToPlay.add(moodSound);
		}
		
		@Override
		public void tickMainAmbience(ClientPlayerEntity player, SoundHandler soundHandler) {
			boolean wasInBiome = this.isInBiome;
			boolean isInBiome = this.updateIsInBiome(player);
			
			if(!wasInBiome && isInBiome) {
				soundHandler.play(new BiomeAmbienceSound(player, this.soundsToPlay.get(0).get(), this.getBiomes()));
			}
		}
		
		@Override
		public void tickAdditions(ClientPlayerEntity player, SoundHandler soundHandler) {
			this.delay--;
			if(this.isInProperBiome(player)) {
				this.ticksInBiome++;
				
				if(!Minecraft.getInstance().isGamePaused()) {
					if(this.delay <= 0 && player.getRNG().nextFloat() < 0.0175F) {
						soundHandler.play(new BiomeAmbienceAdditionSound(player, this.soundsToPlay.get(1).get()));
						this.delay = 60;
					}
					
					if(this.ticksTillNextMood <= this.ticksInBiome) {
						soundHandler.play(new BiomeAmbienceAdditionSound(player, this.soundsToPlay.get(2).get()));
						this.ticksTillNextMood = this.generateTicksTillNextMood();
						this.ticksInBiome = 0;
						this.delay = 20;
					}
				}
			} else {
				this.ticksInBiome = 0;
			}
		}
		
		@Override
		public List<Biome> getBiomes() {
			List<Biome> biomes = Lists.newArrayList();
			for(Supplier<Biome> biome : this.biomesToPlayIn) {
				biomes.add(biome.get());
			}
			return biomes;
		}
		
		private boolean updateIsInBiome(ClientPlayerEntity player) {
			this.isInBiome = this.isInProperBiome(player);
			return this.isInBiome;
		}
		
		private int generateTicksTillNextMood() {
			return (new Random()).nextInt(100) + 6000;
		}
		
		class BiomeAmbienceSound extends TickableSound {
			private final ClientPlayerEntity player;
			private final List<Biome> biomes;
			private int ticksInBiome;

			public BiomeAmbienceSound(ClientPlayerEntity player, SoundEvent sound, List<Biome> biomes) {
				super(sound, SoundCategory.AMBIENT);
				this.player = player;
				this.biomes = biomes;
				this.repeat = true;
				this.repeatDelay = 0;
				this.volume = 1.0F;
				this.priority = true;
				this.global = true;
			}

			public void tick() {
				if(this.player.isAlive() && this.ticksInBiome >= 0) {
					BlockPos pos = new BlockPos(this.player.getPositionVec());
					if(this.player.world.isAreaLoaded(pos, 1) && this.biomes.contains(this.player.world.getBiome(pos))) {
						this.ticksInBiome++;
					} else {
						this.ticksInBiome--;
					}
					
					this.ticksInBiome = Math.min(this.ticksInBiome, 40);
					this.volume = MathHelper.clamp((float) this.ticksInBiome / 40.0F, 0.0F, 1.0F);
				} else {
					this.func_239509_o_();
				}
			}
		}
		
		class BiomeAmbienceAdditionSound extends TickableSound {
			private final ClientPlayerEntity player;

			BiomeAmbienceAdditionSound(ClientPlayerEntity player, SoundEvent sound) {
				super(sound, SoundCategory.AMBIENT);
				this.player = player;
				this.repeat = false;
				this.volume = 0.5F;
				this.pitch = (new Random()).nextFloat() * 0.2F + 0.9F;
				this.priority = true;
				this.global = true;
			}
			
			@Override
			public void tick() {
				if(!this.player.isAlive()) this.func_239509_o_();
			}
		}
	}
	
	public interface IBiomeAmbientSoundHandler extends IAmbientSoundHandler {
		List<Biome> getBiomes();
		
		default boolean isInProperBiome(ClientPlayerEntity player) {
			return this.getBiomes().contains(player.world.getBiome(new BlockPos(player.getPositionVec())));
		}
	}
	
	public interface IAmbientSoundHandler {
		public void tickMainAmbience(ClientPlayerEntity player, SoundHandler soundHandler);
		
		public void tickAdditions(ClientPlayerEntity player, SoundHandler soundHandler);
		
		public default boolean canBeOverridenBy(IAmbientSoundHandler handler) {
			return false;
		}
	}
}