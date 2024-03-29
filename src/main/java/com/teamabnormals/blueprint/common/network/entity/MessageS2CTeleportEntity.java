package com.teamabnormals.blueprint.common.network.entity;

import com.teamabnormals.blueprint.client.ClientInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * The message for teleporting the entity from the server.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class MessageS2CTeleportEntity {
	private int entityId;
	private double posX, posY, posZ;

	public MessageS2CTeleportEntity(int entityID, double posX, double posY, double posZ) {
		this.entityId = entityID;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	public void serialize(FriendlyByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeDouble(this.posX);
		buf.writeDouble(this.posY);
		buf.writeDouble(this.posZ);
	}

	public static MessageS2CTeleportEntity deserialize(FriendlyByteBuf buf) {
		int entityId = buf.readInt();
		return new MessageS2CTeleportEntity(entityId, buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	public static void handle(MessageS2CTeleportEntity message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		Entity entity = ClientInfo.getClientPlayerLevel().getEntity(message.entityId);
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(() -> {
				if (entity != null) {
					entity.moveTo(message.posX, message.posY, message.posZ, entity.getYRot(), entity.getXRot());
				}
			});
			context.setPacketHandled(true);
		}
	}
}