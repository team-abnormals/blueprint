package com.teamabnormals.abnormals_core.core.events;

import com.teamabnormals.abnormals_core.common.entity.IAgeableEntity;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.config.ACConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/**
 * @author Voliant
 * Events for mod compatibility.
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public class CompatEvents {

    public static final String poisonTag = AbnormalsCore.MODID + ":poisoned_by_potato";

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if(event.getTarget() instanceof IAgeableEntity && event.getItemStack().getItem() == Items.POISONOUS_POTATO && ACConfig.ValuesHolder.isPoisonPotatoCompatEnabled() && ModList.get().isLoaded("quark")) {
            if(((IAgeableEntity)event.getTarget()).getGrowingAge() < 0 && !event.getTarget().getPersistentData().getBoolean(poisonTag) && !event.getWorld().isRemote) {
                //Vec3d pos = creepie.getPositionVec();
                event.getPlayer().swingArm(event.getHand());
                if(event.getTarget().world.rand.nextDouble() < ACConfig.ValuesHolder.poisonEffectChance()) {
                    event.getTarget().playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5f, 0.25f);
                    //TODO reactivate this if fixed in Quark
                    //if(((LivingEntity)event.getTarget()).isServerWorld()) ((ServerWorld)event.getTarget().world).spawnParticle(ParticleTypes.ENTITY_EFFECT, pos.x, pos.y, pos.z, 5, 0, 1.0, 0, 0.8);
                    event.getTarget().getPersistentData().putBoolean(poisonTag, true);
                    if(ACConfig.ValuesHolder.shouldPoisonEntity()) {
                        ((LivingEntity) event.getTarget()).addPotionEffect(new EffectInstance(Effects.POISON, 200));
                    } else {
                        event.getTarget().playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5f, 0.5f + event.getTarget().world.rand.nextFloat() / 2);
                        //if(((LivingEntity)event.getTarget()).isServerWorld()) ((ServerWorld)event.getTarget().world).spawnParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 5, 0, 1.0, 0, 0.1);
                    }
                    if(!event.getPlayer().isCreative()) event.getItemStack().shrink(1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onUpdateEntity(LivingEvent.LivingUpdateEvent event) {
        if(event.getEntity() instanceof IAgeableEntity && ACConfig.ValuesHolder.isPoisonPotatoCompatEnabled() && ModList.get().isLoaded("quark")) {
            if (event.getEntity().getPersistentData().getBoolean(poisonTag)) ((IAgeableEntity)event.getEntity()).setGrowingAge(-24000);
        }
    }
}
