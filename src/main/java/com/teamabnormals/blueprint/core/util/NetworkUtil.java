package com.teamabnormals.blueprint.core.util;

import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.common.network.UpdateSlabfishHatPayload;
import com.teamabnormals.blueprint.common.network.entity.TeleportEntityPayload;
import com.teamabnormals.blueprint.common.network.entity.UpdateEndimationPayload;
import com.teamabnormals.blueprint.common.network.entity.UpdateEntityDataPayload;
import com.teamabnormals.blueprint.common.network.particle.SpawnParticlesPayload;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimation;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimationManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Set;

/**
 * A utility class containing some useful Minecraft networking methods.
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class NetworkUtil {
	/**
	 * Used for adding particles to client levels from the server side.
	 * <p>Only sends the packet to players in the given level.</p>
	 *
	 * @param level     The level to spawn the particle in.
	 * @param options   The options for the particles to spawn.
	 * @param instances List of particle instances to spawn.
	 */
	public static void spawnParticle(ServerLevel level, ParticleOptions options, List<SpawnParticlesPayload.ParticleInstance> instances) {
		PacketDistributor.sendToPlayersInDimension(level, new SpawnParticlesPayload(options, instances));
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
		if (entity.level() instanceof ServerLevel serverLevel) {
			PacketDistributor.sendToPlayersInDimension(serverLevel, new TeleportEntityPayload(entity.getId(), posX, posY, posZ));
		}
	}

	/**
	 * Sends an animation message to the clients to update an entity's animations.
	 *
	 * @param entity           The Entity to send the packet for.
	 * @param endimationToPlay The endimation to play.
	 */
	public static <E extends Entity & Endimatable> void setPlayingAnimation(E entity, PlayableEndimation endimationToPlay) {
		if (!entity.level().isClientSide) {
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new UpdateEndimationPayload(entity.getId(), PlayableEndimationManager.INSTANCE.getID(endimationToPlay)));
			entity.setPlayingEndimation(endimationToPlay);
		}
	}

	/**
	 * Sends a {@link UpdateEntityDataPayload} instance to the player to update a tracked entity's {@link IDataManager} values.
	 *
	 * @param player   A {@link ServerPlayer} to send the message to.
	 * @param targetID The ID of the entity to update.
	 * @param entries  A list of new entries.
	 */
	public static void updateTrackedData(ServerPlayer player, int targetID, Set<IDataManager.DataEntry<?>> entries) {
		PacketDistributor.sendToPlayer(player, new UpdateEntityDataPayload(targetID, List.copyOf(entries)));
	}

	/**
	 * Sends a {@link UpdateEntityDataPayload} instance to an entity to update its {@link IDataManager} values.
	 *
	 * @param entity  An {@link Entity} to update.
	 * @param entries A list of new entries.
	 */
	public static void updateTrackedData(Entity entity, Set<IDataManager.DataEntry<?>> entries) {
		PacketDistributor.sendToPlayersTrackingEntity(entity, new UpdateEntityDataPayload(entity.getId(), List.copyOf(entries)));
	}

	/**
	 * Sends a {@link UpdateSlabfishHatPayload} to the server to update the sender's slabfish hat settings.
	 *
	 * @param setting The new slabfish hat setting(s).
	 */
	public static void updateSlabfish(byte setting) {
		if (ClientInfo.getClientPlayer() != null) PacketDistributor.sendToServer(new UpdateSlabfishHatPayload(setting));
	}
}