package com.minecraftabnormals.abnormals_core.core.util;

import com.minecraftabnormals.abnormals_core.client.ClientInfo;
import com.minecraftabnormals.abnormals_core.common.network.MessageC2SUpdateSlabfishHat;
import com.minecraftabnormals.abnormals_core.common.network.entity.MessageS2CEndimation;
import com.minecraftabnormals.abnormals_core.common.network.entity.MessageS2CTeleportEntity;
import com.minecraftabnormals.abnormals_core.common.network.entity.MessageS2CUpdateEntityData;
import com.minecraftabnormals.abnormals_core.common.network.particle.MessageS2CSpawnParticle;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.minecraftabnormals.abnormals_core.core.endimator.Endimation;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.IEndimatedEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Set;

/**
 * @author - SmellyModder(Luke Tonon)
 * This class holds a list of useful network functions
 */
public final class NetworkUtil {
	/**
	 * All other parameters work the same in {@link Level#addParticle(ParticleOptions, double, double, double, double, double, double)}.
	 * <p>Used for adding particles to client levels from the server side</p>
	 * @param name The registry name of the particle
	 */
	public static void spawnParticle(String name, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.ALL.with(() -> null), new MessageS2CSpawnParticle(name, posX, posY, posZ, motionX, motionY, motionZ));
	}

	/**
	 * All other parameters work the same in {@link Level#addParticle(ParticleOptions, double, double, double, double, double, double)}.
	 * <p>Used for adding particles to client levels from the server side</p>
	 * <p>Only sends the packet to players in {@code dimension}</p>
	 *
	 * @param name The registry name of the particle
	 * @param dimension The dimension to spawn the particle in. You can get this using {@link Level#dimension()}
	 */
	public static void spawnParticle(String name, ResourceKey<Level> dimension, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
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
		entity.moveTo(posX, posY, posZ, entity.getYRot(), entity.getXRot());
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

	public static void updateTrackedData(ServerPlayer player, Entity target, Set<IDataManager.DataEntry<?>> entries) {
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