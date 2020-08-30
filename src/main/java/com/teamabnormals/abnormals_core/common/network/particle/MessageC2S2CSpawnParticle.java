package com.teamabnormals.abnormals_core.common.network.particle;

import java.util.function.Supplier;

import com.teamabnormals.abnormals_core.core.util.NetworkUtil;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Message for telling the server to spawn particles on clients from the client
 * @author - SmellyModder(Luke Tonon)
 */
public final class MessageC2S2CSpawnParticle {
	public String particleName;
	public double posX, posY, posZ;
	public double motionX, motionY, motionZ;
	
	public MessageC2S2CSpawnParticle(String particleName, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		this.particleName = particleName;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
	}
	
	public void serialize(PacketBuffer buf) {
		buf.writeString(this.particleName);
		buf.writeDouble(this.posX);
		buf.writeDouble(this.posY);
		buf.writeDouble(this.posZ);
		buf.writeDouble(this.motionX);
		buf.writeDouble(this.motionY);
		buf.writeDouble(this.motionZ);
	}
	
	public static MessageC2S2CSpawnParticle deserialize(PacketBuffer buf) {
		String particleName = buf.readString(32767);
		double posX = buf.readDouble();
		double posY = buf.readDouble();
		double posZ = buf.readDouble();
		double motionX = buf.readDouble();
		double motionY = buf.readDouble();
		double motionZ = buf.readDouble();
		return new MessageC2S2CSpawnParticle(particleName, posX, posY, posZ, motionX, motionY, motionZ);
	}
	
	public static void handle(MessageC2S2CSpawnParticle message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
			context.enqueueWork(() -> {
				NetworkUtil.spawnParticle(message.particleName, message.posX, message.posY, message.posZ, message.motionX, message.motionY, message.motionZ);
			});
		}
		context.setPacketHandled(true);
	}
}