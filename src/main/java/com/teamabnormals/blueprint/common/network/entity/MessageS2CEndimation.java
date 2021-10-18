package com.teamabnormals.blueprint.common.network.entity;

import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.core.endimator.entity.IEndimatedEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * The message for telling clients to begin playing an animation on an {@link IEndimatedEntity}.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class MessageS2CEndimation {
	private int entityId;
	private int endimationIndex;

	public MessageS2CEndimation(int entityID, int endimationIndex) {
		this.entityId = entityID;
		this.endimationIndex = endimationIndex;
	}

	public void serialize(FriendlyByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeInt(this.endimationIndex);
	}

	public static MessageS2CEndimation deserialize(FriendlyByteBuf buf) {
		int entityId = buf.readInt();
		int endimationIndex = buf.readInt();
		return new MessageS2CEndimation(entityId, endimationIndex);
	}

	public static void handle(MessageS2CEndimation message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		IEndimatedEntity endimatedEntity = (IEndimatedEntity) ClientInfo.getClientPlayerLevel().getEntity(message.entityId);
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(() -> {
				if (endimatedEntity != null) {
					if (message.endimationIndex != -1) {
						endimatedEntity.setPlayingEndimation(endimatedEntity.getEndimations()[message.endimationIndex]);
					} else {
						endimatedEntity.setPlayingEndimation(IEndimatedEntity.BLANK_ANIMATION);
					}
				}
			});
			context.setPacketHandled(true);
		}
	}
}