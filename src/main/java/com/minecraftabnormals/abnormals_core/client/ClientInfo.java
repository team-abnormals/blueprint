package com.minecraftabnormals.abnormals_core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.world.World;

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
	public static World getClientPlayerWorld() {
		return ClientInfo.getClientPlayer().world;
	}
}