package com.minecraftabnormals.abnormals_core.core.util;

import com.minecraftabnormals.abnormals_core.client.ClientInfo;
import com.minecraftabnormals.abnormals_core.common.network.MessageC2SUpdateSlabfishHat;
import com.minecraftabnormals.abnormals_core.common.network.MessageS2CServerRedirect;
import com.minecraftabnormals.abnormals_core.common.network.entity.MessageS2CEndimation;
import com.minecraftabnormals.abnormals_core.common.network.entity.MessageS2CTeleportEntity;
import com.minecraftabnormals.abnormals_core.common.network.entity.MessageS2CUpdateEntityData;
import com.minecraftabnormals.abnormals_core.common.network.particle.MessageS2CSpawnParticle;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.endimator.Endimation;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.IEndimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Set;

/**
 * @author - SmellyModder(Luke Tonon)
 * This class holds a list of useful network functions
 */
public final class NetworkUtil {
	/**
	 * All other parameters work same as world#addParticle
	 * <p>Used for adding particles to client worlds from the server side</p>
	 * @param name The registry name of the particle
	 */
	public static void spawnParticle(String name, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.ALL.with(() -> null), new MessageS2CSpawnParticle(name, posX, posY, posZ, motionX, motionY, motionZ));
	}

	/**
	 * All other parameters work same as world#addParticle
	 * <p>Used for adding particles to client worlds from the server side</p>
	 * <p>Only sends the packet to players in {@code dimension}</p>
	 *
	 * @param name The registry name of the particle
	 * @param dimension The dimension to spawn the particle in. You can get this using {@link World#dimension()}
	 */
	public static void spawnParticle(String name, RegistryKey<World> dimension, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dimension), new MessageS2CSpawnParticle(name, posX, posY, posZ, motionX, motionY, motionZ));
	}

	/**
	 * Teleports the entity to a specified location
	 *
	 * @param entity The Entity to teleport
	 * @param posX   The x position
	 * @param posY   The y position
	 * @param posZ   The z position
	 */
	public static void teleportEntity(Entity entity, double posX, double posY, double posZ) {
		entity.moveTo(posX, posY, posZ, entity.yRot, entity.xRot);
		AbnormalsCore.CHANNEL.send(PacketDistributor.ALL.with(() -> null), new MessageS2CTeleportEntity(entity.getId(), posX, posY, posZ));
	}

	/**
	 * Sends an animation message to the clients to update an entity's animations
	 *
	 * @param entity           The Entity to send the packet for
	 * @param endimationToPlay The endimation to play
	 */
	public static <E extends Entity & IEndimatedEntity> void setPlayingAnimationMessage(E entity, Endimation endimationToPlay) {
		if (!entity.level.isClientSide) {
			AbnormalsCore.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MessageS2CEndimation(entity.getId(), ArrayUtils.indexOf(entity.getEndimations(), endimationToPlay)));
			entity.setPlayingEndimation(endimationToPlay);
		}
	}

	/**
	 * Send a packet to the client to redirect them to another server
	 *
	 * @param address The address to connect to
	 */
	public static void redirectToServer(ServerPlayerEntity player, String address) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageS2CServerRedirect(address));
	}

	/**
	 * Send a packet to all clients to redirect them to another server
	 *
	 * @param address The address to connect to
	 */
	public static void redirectAllToServer(String address) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.ALL.noArg(), new MessageS2CServerRedirect(address));
	}

	@OnlyIn(Dist.CLIENT)
	public static void redirectToServer(String address) {
		Minecraft minecraft = ClientInfo.MINECRAFT;
		World world = minecraft.level;
		Screen currentScreen = minecraft.screen;
		boolean integrated = minecraft.isLocalServer();

		if (world != null) {
			world.disconnect();
			minecraft.clearLevel(new DirtMessageScreen(new TranslationTextComponent(integrated ? "menu.savingLevel" : "abnormals_core.message.redirect")));

			MainMenuScreen menuScreen = new MainMenuScreen();
			minecraft.setScreen(integrated ? menuScreen : new MultiplayerScreen(menuScreen));

			if (currentScreen != null)
				minecraft.setScreen(new ConnectingScreen(currentScreen, minecraft, new ServerData("Redirect", address, false)));
		}
	}

	public static void updateTrackedData(ServerPlayerEntity player, Entity target, Set<IDataManager.DataEntry<?>> entries) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageS2CUpdateEntityData(target.getId(), entries));
	}

	public static void updateTrackedData(Entity entity, Set<IDataManager.DataEntry<?>> entries) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MessageS2CUpdateEntityData(entity.getId(), entries));
	}

	@OnlyIn(Dist.CLIENT)
	public static void updateSlabfish(byte setting) {
		if (ClientInfo.getClientPlayer() != null) {
			AbnormalsCore.CHANNEL.sendToServer(new MessageC2SUpdateSlabfishHat(setting));
		}
	}
}