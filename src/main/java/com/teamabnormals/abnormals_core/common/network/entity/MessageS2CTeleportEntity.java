package com.teamabnormals.abnormals_core.common.network.entity;

import java.util.function.Supplier;

import com.teamabnormals.abnormals_core.client.ClientInfo;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Message for teleporting the entity from the server
 * @author SmellyModder(Luke Tonon)
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
	
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.entityId);
		buf.writeDouble(this.posX);
		buf.writeDouble(this.posY);
		buf.writeDouble(this.posZ);
	}
	
	public static MessageS2CTeleportEntity deserialize(PacketBuffer buf) {
		int entityId = buf.readInt();
		return new MessageS2CTeleportEntity(entityId, buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
	
	public static void handle(MessageS2CTeleportEntity message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		Entity entity = ClientInfo.getClientPlayerWorld().getEntityByID(message.entityId);
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(() -> {
				if (entity != null) {
					entity.setLocationAndAngles(message.posX, message.posY, message.posZ, entity.rotationYaw, entity.rotationPitch);
				}
			});
			context.setPacketHandled(true);
		}
	}
}