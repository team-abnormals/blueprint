package com.teamabnormals.abnormals_core.core.events;

import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.config.ACConfig;
import com.teamabnormals.abnormals_core.core.library.api.IAgeableEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/**
 * @author tessdotcpp
 * Events for mod compatibility.
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public class CompatEvents {
    public static final String poisonTag = AbnormalsCore.MODID + ":poisoned_by_potato";

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        ItemStack stack = event.getItemStack();
        if (target instanceof IAgeableEntity && stack.getItem() == Items.POISONOUS_POTATO && ACConfig.ValuesHolder.isPoisonPotatoCompatEnabled() && ModList.get().isLoaded("quark")) {
            PlayerEntity player = event.getPlayer();
            CompoundNBT persistantData = target.getPersistentData();
            if (((IAgeableEntity) target).getGrowingAge() < 0 && !persistantData.getBoolean(poisonTag)) {
                if (!event.getWorld().isRemote) {
                    if (target.world.rand.nextDouble() < ACConfig.ValuesHolder.poisonEffectChance()) {
                        target.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5f, 0.25f);
                        persistantData.putBoolean(poisonTag, true);
                        if (ACConfig.ValuesHolder.shouldPoisonEntity()) {
                            ((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.POISON, 200));
                        }
                    } else {
                        target.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5f, 0.5f + target.world.rand.nextFloat() / 2);
                    }
                } else {
                    player.swingArm(event.getHand());
                }
                if (!player.isCreative()) stack.shrink(1);
            }
        }
    }

    @SubscribeEvent
    public static void onUpdateEntity(LivingEvent.LivingUpdateEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof IAgeableEntity && ACConfig.ValuesHolder.isPoisonPotatoCompatEnabled() && ModList.get().isLoaded("quark")) {
            if (entity.getPersistentData().getBoolean(poisonTag)) ((IAgeableEntity) entity).setGrowingAge(-24000);
        }
    }
}