package com.teamabnormals.abnormals_core.client.renderer;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author bageldotjpg
 * This handles distribution of a special 
 * cape for developers on Team Abnormals.
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public class CapeHandler {
	
	private static final ImmutableSet<String> UUIDS = ImmutableSet.of(
			"8ed04941-c497-4caf-80b2-ccf2e821d94d",
			"b8b859a5-2dbc-4743-8f7a-4768f6692606",
			"4d568080-07a5-4961-96b2-3811f9721aa2", 
			"caaeff74-cbbc-415c-9c22-21e65ad6c33f",
			"4378df24-8433-4b5c-b865-bf635b003ebb",
			"9a10620c-ce87-4f6c-a4a7-42d6b8ed39d6",
			"ff2dd200-7a20-4cad-a42b-65a69da12f2c");
	
	private static final Set<String> RENDERED = Collections.newSetFromMap(new WeakHashMap<>());
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onRenderPlayer(RenderPlayerEvent.Post event) {
		PlayerEntity player = event.getPlayer();
		String uuid = PlayerEntity.getUUID(player.getGameProfile()).toString();
		if(player instanceof AbstractClientPlayerEntity && UUIDS.contains(uuid) && !RENDERED.contains(uuid)) {
			AbstractClientPlayerEntity clientPlayer = (AbstractClientPlayerEntity) player;
			if(clientPlayer.hasPlayerInfo()) {
				ResourceLocation cape = new ResourceLocation(AbnormalsCore.MODID, "textures/abnormals_cape.png");
				clientPlayer.playerInfo.playerTextures.put(MinecraftProfileTexture.Type.CAPE, cape);
				clientPlayer.playerInfo.playerTextures.put(MinecraftProfileTexture.Type.ELYTRA, cape);
				RENDERED.add(uuid);
			}
		}
	}
}
