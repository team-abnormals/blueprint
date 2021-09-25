package com.minecraftabnormals.abnormals_core.core.events;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.api.IAgeableEntity;
import com.minecraftabnormals.abnormals_core.core.config.ACConfig;
import com.minecraftabnormals.abnormals_core.core.util.DataUtil;
import com.minecraftabnormals.abnormals_core.core.util.NetworkUtil;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;

/**
 * Events for mod compatibility.
 *
 * @author abigailfails
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public final class CompatEvents {
	public static final String POISON_TAG = AbnormalsCore.MODID + ":poisoned_by_potato";
	public static final String NOTE_KEY = "minecraft:note";
	public static List<DataUtil.CustomNoteBlockInstrument> SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS = null;

	@SubscribeEvent
	public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		Entity target = event.getTarget();
		ItemStack stack = event.getItemStack();
		if (target instanceof IAgeableEntity && ((IAgeableEntity) target).hasGrowthProgress() && stack.getItem() == Items.POISONOUS_POTATO && ACConfig.ValuesHolder.isPoisonPotatoCompatEnabled() && ModList.get().isLoaded("quark")) {
			Player player = event.getPlayer();
			CompoundTag persistentData = target.getPersistentData();
			if (((IAgeableEntity) target).canAge(true) && !persistentData.getBoolean(POISON_TAG)) {
				if (target.level.random.nextDouble() < ACConfig.ValuesHolder.poisonEffectChance()) {
					target.playSound(SoundEvents.GENERIC_EAT, 0.5f, 0.25f);
					persistentData.putBoolean(POISON_TAG, true);
					if (ACConfig.ValuesHolder.shouldPoisonEntity()) {
						((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.POISON, 200));
					}
				} else {
					target.playSound(SoundEvents.GENERIC_EAT, 0.5f, 0.5f + target.level.random.nextFloat() / 2);
				}
				if (!player.isCreative()) stack.shrink(1);
				event.setCancellationResult(InteractionResult.sidedSuccess(event.getWorld().isClientSide()));
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onUpdateEntity(LivingEvent.LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof IAgeableEntity && ACConfig.ValuesHolder.isPoisonPotatoCompatEnabled() && ModList.get().isLoaded("quark")) {
			if (entity.getPersistentData().getBoolean(POISON_TAG)) ((IAgeableEntity) entity).resetGrowthProgress();
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onNoteBlockPlay(NoteBlockEvent.Play event) {
		if (SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS  != null) {
			Level world = (Level) event.getWorld();
			if (!world.isClientSide()) {
				BlockPos pos = event.getPos();
				BlockSource source = new BlockSourceImpl((ServerLevel) world, pos.relative(Direction.DOWN));
				for (DataUtil.CustomNoteBlockInstrument instrument : SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS) {
					if (instrument.test(source)) {
						SoundEvent sound = instrument.getSound();
						double note = event.getVanillaNoteId();
						world.playSound(null, pos, sound, SoundSource.RECORDS, 3.0F, (float) Math.pow(2.0D, (note - 12) / 12.0D));
						NetworkUtil.spawnParticle(NOTE_KEY, world.dimension(), pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, note / 24.0D, 0.0D, 0.0D);
						event.setCanceled(true);
						break;
					}
				}
			}
		}

	}
}
