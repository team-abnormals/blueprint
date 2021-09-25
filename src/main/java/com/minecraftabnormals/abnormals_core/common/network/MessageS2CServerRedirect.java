package com.minecraftabnormals.abnormals_core.common.network;

import com.minecraftabnormals.abnormals_core.core.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Allows servers to redirect clients to another server.
 *
 * @author Jackson
 */
public final class MessageS2CServerRedirect {
	private final String connectionAddress;

	public MessageS2CServerRedirect(String address) {
		this.connectionAddress = address;
	}

	public void serialize(FriendlyByteBuf buf) {
		buf.writeUtf(this.connectionAddress);
	}

	public static MessageS2CServerRedirect deserialize(FriendlyByteBuf buf) {
		return new MessageS2CServerRedirect(buf.readUtf());
	}

	public static void handle(MessageS2CServerRedirect message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(() -> {
				NetworkUtil.redirectToServer(message.getConnectionAddress());
			});
			context.setPacketHandled(true);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public String getConnectionAddress() {
		return connectionAddress;
	}
}
