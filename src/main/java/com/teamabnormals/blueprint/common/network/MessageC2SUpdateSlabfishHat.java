package com.teamabnormals.blueprint.common.network;

import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Message for syncing Slabfish hat customization settings to the server.
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
					((IDataManager) player).setValue(Blueprint.SLABFISH_SETTINGS, message.setting);
			});
			context.setPacketHandled(true);
		}
	}
}