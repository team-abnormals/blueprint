package com.minecraftabnormals.abnormals_core.core.events;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.api.IAgeableEntity;
import com.minecraftabnormals.abnormals_core.core.config.ACConfig;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/**
 * Events for mod compatibility.
 *
 * @author abigailfails
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public final class CompatEvents {
	public static final String POISON_TAG = AbnormalsCore.MODID + ":poisoned_by_potato";

	@SubscribeEvent
	public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		Entity target = event.getTarget();
		ItemStack stack = event.getItemStack();
		if (target instanceof IAgeableEntity && ((IAgeableEntity) target).hasGrowthProgress() && stack.getItem() == Items.POISONOUS_POTATO && ACConfig.ValuesHolder.isPoisonPotatoCompatEnabled() && ModList.get().isLoaded("quark")) {
			PlayerEntity player = event.getPlayer();
			CompoundNBT persistentData = target.getPersistentData();
			if (((IAgeableEntity) target).canAge(true) && !persistentData.getBoolean(POISON_TAG)) {
				if (target.world.rand.nextDouble() < ACConfig.ValuesHolder.poisonEffectChance()) {
					target.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5f, 0.25f);
					persistentData.putBoolean(POISON_TAG, true);
					if (ACConfig.ValuesHolder.shouldPoisonEntity()) {
						((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.POISON, 200));
					}
				} else {
					target.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5f, 0.5f + target.world.rand.nextFloat() / 2);
				}
				if (!player.isCreative()) stack.shrink(1);
				event.setCancellationResult(ActionResultType.func_233537_a_(event.getWorld().isRemote()));
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
}
