package com.teamabnormals.blueprint.core.util;

import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.common.network.MessageC2SUpdateSlabfishHat;
import com.teamabnormals.blueprint.common.network.entity.MessageS2CEndimation;
import com.teamabnormals.blueprint.common.network.entity.MessageS2CTeleportEntity;
import com.teamabnormals.blueprint.common.network.entity.MessageS2CUpdateEntityData;
import com.teamabnormals.blueprint.common.network.particle.MessageS2CSpawnParticle;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimation;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimationManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.Set;

/**
 * A utility class containing some useful Minecraft networking methods.
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class NetworkUtil {
	/**
	 * All other parameters work the same in {@link Level#addParticle(ParticleOptions, double, double, double, double, double, double)}.
	 * <p>Used for adding particles to client levels from the server side.</p>
	 *
	 * @param name    The registry name of the particle.
	 * @param posX    The x pos of the particle.
	 * @param posY    The y pos of the particle.
	 * @param posZ    The z pos of the particle.
	 * @param motionX The x motion of the particle.
	 * @param motionY The y motion of the particle.
	 * @param motionZ The y motion of the particle.
	 */
	public static void spawnParticle(String name, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		Blueprint.CHANNEL.send(PacketDistributor.ALL.with(() -> null), new MessageS2CSpawnParticle(name, posX, posY, posZ, motionX, motionY, motionZ));
	}

	/**
	 * All other parameters work the same in {@link Level#addParticle(ParticleOptions, double, double, double, double, double, double)}.
	 * <p>Used for adding particles to client levels from the server side.</p>
	 * <p>Only sends the packet to players in {@code dimension}.</p>
	 *
	 * @param name      The registry name of the particle.
	 * @param dimension The dimension to spawn the particle in. You can get this using {@link Level#dimension()}.
	 * @param posX      The x pos of the particle.
	 * @param posY      The y pos of the particle.
	 * @param posZ      The z pos of the particle.
	 * @param motionX   The x motion of the particle.
	 * @param motionY   The y motion of the particle.
	 * @param motionZ   The y motion of the particle.
	 */
	public static void spawnParticle(String name, ResourceKey<Level> dimension, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		Blueprint.CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dimension), new MessageS2CSpawnParticle(name, posX, posY, posZ, motionX, motionY, motionZ));
	}

	/**
	 * Teleports the entity to a specified location.
	 *
	 * @param entity The Entity to teleport.
	 * @param posX   The x position.
	 * @param posY   The y position.
	 * @param posZ   The z position.
	 */
	public static void teleportEntity(Entity entity, double posX, double posY, double posZ) {
		entity.moveTo(posX, posY, posZ, entity.getYRot(), entity.getXRot());
		Blueprint.CHANNEL.send(PacketDistributor.ALL.with(() -> null), new MessageS2CTeleportEntity(entity.getId(), posX, posY, posZ));
	}

	/**
	 * Sends an animation message to the clients to update an entity's animations.
	 *
	 * @param entity           The Entity to send the packet for.
	 * @param endimationToPlay The endimation to play.
	 */
	public static <E extends Entity & Endimatable> void setPlayingAnimation(E entity, PlayableEndimation endimationToPlay) {
		if (!entity.level.isClientSide) {
			Blueprint.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new MessageS2CEndimation(entity.getId(), PlayableEndimationManager.INSTANCE.getID(endimationToPlay)));
			entity.setPlayingEndimation(endimationToPlay);
		}
	}

	/**
	 * Sends a {@link MessageS2CUpdateEntityData} instance to the player to update a tracked entity's {@link IDataManager} values.
	 *
	 * @param player   A {@link ServerPlayer} to send the message to.
	 * @param targetID The ID of the entity to update.
	 * @param entries  A set of new entries.
	 */
	public static void updateTrackedData(ServerPlayer player, int targetID, Set<IDataManager.DataEntry<?>> entries) {
		Blueprint.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageS2CUpdateEntityData(targetID, entries));
	}

	/**
	 * Sends a {@link MessageS2CUpdateEntityData} instance to an entity to update its {@link IDataManager} values.
	 *
	 * @param entity  An {@link Entity} to update.
	 * @param entries A set of new entries.
	 */
	public static void updateTrackedData(Entity entity, Set<IDataManager.DataEntry<?>> entries) {
		Blueprint.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MessageS2CUpdateEntityData(entity.getId(), entries));
	}

	/**
	 * Sends a {@link MessageC2SUpdateSlabfishHat} to the server to update the sender's slabfish hat settings.
	 *
	 * @param setting The new slabfish hat setting(s).
	 */
	@OnlyIn(Dist.CLIENT)
	public static void updateSlabfish(byte setting) {
		if (ClientInfo.getClientPlayer() != null) {
			Blueprint.CHANNEL.sendToServer(new MessageC2SUpdateSlabfishHat(setting));
		}
	}
}