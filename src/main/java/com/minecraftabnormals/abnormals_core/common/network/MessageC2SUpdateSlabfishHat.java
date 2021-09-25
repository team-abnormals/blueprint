package com.minecraftabnormals.abnormals_core.common.network;

import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Message for syncing Slabfish hat customization settings.
 *
 * @author Jackson
 */
public final class MessageC2SUpdateSlabfishHat {
	private final byte setting;

	public MessageC2SUpdateSlabfishHat(byte setting) {
		this.setting = setting;
	}

	public void serialize(FriendlyByteBuf buf) {
		buf.writeByte(this.setting);
	}

	public static MessageC2SUpdateSlabfishHat deserialize(FriendlyByteBuf buf) {
		return new MessageC2SUpdateSlabfishHat(buf.readByte());
	}

	public static void handle(MessageC2SUpdateSlabfishHat message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
			context.enqueueWork(() -> {
				ServerPlayer player = context.getSender();
				if (player instanceof IDataManager)
					((IDataManager) player).setValue(AbnormalsCore.SLABFISH_SETTINGS, message.setting);
			});
			context.setPacketHandled(true);
		}
	}
}