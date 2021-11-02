package com.teamabnormals.blueprint.common.network.entity;

import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimation;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimationManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * The message for telling clients to begin playing a {@link PlayableEndimation} on an {@link Endimatable} entity.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class MessageS2CEndimation {
	private final int entityId;
	private final int endimationId;

	public MessageS2CEndimation(int entityID, int endimationId) {
		this.entityId = entityID;
		this.endimationId = endimationId;
	}

	public void serialize(FriendlyByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeInt(this.endimationId);
	}

	public static MessageS2CEndimation deserialize(FriendlyByteBuf buf) {
		return new MessageS2CEndimation(buf.readInt(), buf.readInt());
	}

	public static void handle(MessageS2CEndimation message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(() -> {
				Endimatable endimatedEntity = (Endimatable) ClientInfo.getClientPlayerLevel().getEntity(message.entityId);
				if (endimatedEntity != null) {
					int id = message.endimationId;
					PlayableEndimation endimation = PlayableEndimationManager.INSTANCE.getEndimation(id);
					if (endimation == null) {
						Blueprint.LOGGER.warn("Could not find Playable Endimation with ID " + id + " to play, defaulting to blank.");
						endimatedEntity.resetEndimation();
					} else {
						endimatedEntity.setPlayingEndimation(endimation);
					}
				}
			});
			context.setPacketHandled(true);
		}
	}
}