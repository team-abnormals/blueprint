package com.teamabnormals.abnormals_core.core.utils;

import com.teamabnormals.abnormals_core.client.ClientInfo;
import com.teamabnormals.abnormals_core.common.network.*;
import com.teamabnormals.abnormals_core.common.network.entity.*;
import com.teamabnormals.abnormals_core.common.network.particle.*;
import com.teamabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.library.endimator.Endimation;
import com.teamabnormals.abnormals_core.core.library.endimator.entity.IEndimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
	 * @param name - The registry name of the particle
	 * All other parameters work same as world#addParticle
	 * Used for adding particles to the world from the server side
	 */
	public static void spawnParticle(String name, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.ALL.with(() -> null), new MessageS2CSpawnParticle(name, posX, posY, posZ, motionX, motionY, motionZ));
	}
	
	/**
	 * @param name - The registry name of the particle
	 * Used for adding particles to all the clients from the client
	 */
	public static void spawnParticleC2S2C(String name, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		AbnormalsCore.CHANNEL.sendToServer(new MessageC2S2CSpawnParticle(name, posX, posY, posZ, motionX, motionY, motionZ));
	}
	
	/**
	 * Teleports the entity to a specified location
	 * @param entity - The Entity to teleport
	 * @param posX - The x position
	 * @param posY - The y position
	 * @param posZ - The z position
	 */
	public static void teleportEntity(Entity entity, double posX, double posY, double posZ) {
		entity.setLocationAndAngles(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
		AbnormalsCore.CHANNEL.send(PacketDistributor.ALL.with(() -> null), new MessageS2CTeleportEntity(entity.getEntityId(), posX, posY, posZ));
	}

	/**
	 * Sends an animation message to the clients to update an entity's animations
	 * @param entity - The Entity to send the packet for
	 * @param endimationToPlay - The endimation to play
	 */
	public static <E extends Entity & IEndimatedEntity> void setPlayingAnimationMessage(E entity, Endimation endimationToPlay) {
		if(!entity.world.isRemote) {
			AbnormalsCore.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MessageS2CEndimation(entity.getEntityId(), ArrayUtils.indexOf(entity.getEndimations(), endimationToPlay)));
			entity.setPlayingEndimation(endimationToPlay);
		}
	}

	/**
	 * Send a packet to the client to redirect them to another server
	 * @param address - The address to connect to
	 */
	public static void redirectToServer(ServerPlayerEntity player, String address) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageS2CServerRedirect(address));
	}

	/**
	 * Send a packet to all clients to redirect them to another server
	 * @param address - The address to connect to
	 */
	public static void redirectAllToServer(String address) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.ALL.noArg(), new MessageS2CServerRedirect(address));
	}

	@OnlyIn(Dist.CLIENT)
	public static void redirectToServer(String address) {
		Minecraft minecraft = ClientInfo.MINECRAFT;
		World world = minecraft.world;
		Screen currentScreen = minecraft.currentScreen;
		boolean integrated = minecraft.isIntegratedServerRunning();

		if (world != null) {
			world.sendQuittingDisconnectingPacket();
			minecraft.unloadWorld(new DirtMessageScreen(new TranslationTextComponent(integrated ? "menu.savingLevel" : "abnormals_core.message.redirect")));

			MainMenuScreen menuScreen = new MainMenuScreen();
			minecraft.displayGuiScreen(integrated ? menuScreen : new MultiplayerScreen(menuScreen));

			if (currentScreen != null)
				minecraft.displayGuiScreen(new ConnectingScreen(currentScreen, minecraft, new ServerData("Redirect", address, false)));
		}
	}

	public static void updateTrackedData(ServerPlayerEntity player, Entity target, Set<IDataManager.DataEntry<?>> entries) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageS2CUpdateEntityData(target.getEntityId(), entries));
	}

	public static void updateTrackedData(Entity entity, Set<IDataManager.DataEntry<?>> entries) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MessageS2CUpdateEntityData(entity.getEntityId(), entries));
	}

	@OnlyIn(Dist.CLIENT)
	public static void updateSlabfish(byte setting) {
		if (ClientInfo.getClientPlayer() != null) {
			AbnormalsCore.CHANNEL.sendToServer(new MessageC2SUpdateSlabfishHat(setting));
		}
	}
}