package com.teamabnormals.abnormals_core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class ClientInfo {
	public static final Minecraft MINECRAFT = Minecraft.getInstance();

	/**
	 * @return - The partial ticks of the minecraft client
	 */
	public static float getPartialTicks() {
		return MINECRAFT.isGamePaused() ? MINECRAFT.renderPartialTicksPaused : MINECRAFT.getRenderPartialTicks();
	}
	
	/**
	 * @return - The client player entity
	 */
	public static ClientPlayerEntity getClientPlayer() {
		return MINECRAFT.player;
	}
		
	/**
	 * @return - The client player's world; equivalent to getting the client world
	 */
	public static ClientWorld getClientPlayerWorld() {
		return (ClientWorld) ClientInfo.getClientPlayer().world;
	}
}