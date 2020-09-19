package com.teamabnormals.abnormals_core.client;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
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
 * This handles distribution of a special cape for developers on Team Abnormals.
 */
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID)
public class CapeHandler {
	private static final ResourceLocation CAPE_TEXTURE = new ResourceLocation(AbnormalsCore.MODID, "textures/abnormals_cape.png");

	private static final ImmutableSet<String> UUIDS = ImmutableSet.of(
			"8ed04941-c497-4caf-80b2-ccf2e821d94d", // bageldotjpg (bagel)
			"b8b859a5-2dbc-4743-8f7a-4768f6692606", // smellysox345 (Smelly)
			"68c08594-e7cd-43fb-bdf9-240147ee26cf", // JacksonPlayzYT (Jackson)
			"caaeff74-cbbc-415c-9c22-21e65ad6c33f", // camcamcamcamcam (Cameron)
			"4378df24-8433-4b5c-b865-bf635b003ebb", // Farcr
			"7d3a5f6e-ac22-43d8-8c9f-863c6f4ded1c", // hatsondogs
			"ff2dd200-7a20-4cad-a42b-65a69da12f2c", // Nitrometer (Snake Block)
			"c92ca019-c110-4856-a1ec-1b3c8d25546e"  // Echolite
	);
	
	private static final Set<String> RENDERED = Sets.newHashSet();
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onRenderPlayer(RenderPlayerEvent.Post event) {
		PlayerEntity player = event.getPlayer();
		String uuid = PlayerEntity.getUUID(player.getGameProfile()).toString();
		if (player instanceof AbstractClientPlayerEntity && UUIDS.contains(uuid) && !RENDERED.contains(uuid)) {
			AbstractClientPlayerEntity clientPlayer = (AbstractClientPlayerEntity) player;
			if (clientPlayer.hasPlayerInfo()) {
				Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = clientPlayer.playerInfo.playerTextures;
				playerTextures.put(MinecraftProfileTexture.Type.CAPE, CAPE_TEXTURE);
				playerTextures.put(MinecraftProfileTexture.Type.ELYTRA, CAPE_TEXTURE);
				RENDERED.add(uuid);
			}
		}
	}
}
