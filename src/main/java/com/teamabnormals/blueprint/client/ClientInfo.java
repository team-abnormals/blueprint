package com.teamabnormals.blueprint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

/**
 * A class containing some useful methods for getting information about the client.
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class ClientInfo {
	/**
	 * Gets the partial ticks of the client.
	 *
	 * @return The partial ticks of the client.
	 */
	public static float getPartialTicks() {
		return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
	}

	/**
	 * Gets the {@link LocalPlayer} entity.
	 *
	 * @return The {@link LocalPlayer} entity.
	 */
	public static LocalPlayer getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	/**
	 * Gets the {@link Level} of the client player.
	 *
	 * @return The client player's world; equivalent to getting the client world.
	 */
	public static Level getClientPlayerLevel() {
		return ClientInfo.getClientPlayer().level();
	}
}