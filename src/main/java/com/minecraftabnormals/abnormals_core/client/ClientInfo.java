package com.minecraftabnormals.abnormals_core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

public final class ClientInfo {
	public static final Minecraft MINECRAFT = Minecraft.getInstance();

	/**
	 * @return - The partial ticks of the minecraft client
	 */
	public static float getPartialTicks() {
		return MINECRAFT.isPaused() ? MINECRAFT.pausePartialTick : MINECRAFT.getFrameTime();
	}

	/**
	 * @return - The client player entity
	 */
	public static LocalPlayer getClientPlayer() {
		return MINECRAFT.player;
	}

	/**
	 * @return - The client player's world; equivalent to getting the client world
	 */
	public static Level getClientPlayerWorld() {
		return ClientInfo.getClientPlayer().level;
	}
}