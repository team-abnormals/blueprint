package com.teamabnormals.abnormals_core.common.network;

import com.teamabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Message for updating if the Slabfish hat is enabled.
 *
 * @author Jackson
 */
public final class MessageC2SUpdateSlabfishHat {
	private final byte setting;

	public MessageC2SUpdateSlabfishHat(byte setting) {
		this.setting = setting;
	}

	public void serialize(PacketBuffer buf) {
		buf.writeByte(this.setting);
	}

	public static MessageC2SUpdateSlabfishHat deserialize(PacketBuffer buf) {
		return new MessageC2SUpdateSlabfishHat(buf.readByte());
	}

	public static void handle(MessageC2SUpdateSlabfishHat message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
			context.enqueueWork(() -> {
				ServerPlayerEntity player = context.getSender();
				IDataManager data = (IDataManager) player;
				if(data != null) data.setValue(AbnormalsCore.SLABFISH_SETTINGS, message.getSetting());
			});
			context.setPacketHandled(true);
		}
	}

	public byte getSetting() {
		return setting;
	}
}