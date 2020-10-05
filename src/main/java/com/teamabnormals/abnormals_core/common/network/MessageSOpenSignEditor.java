package com.teamabnormals.abnormals_core.common.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.teamabnormals.abnormals_core.client.ClientInfo;
import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import com.teamabnormals.abnormals_core.core.util.NetworkUtil;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Sends a message to the client to open the sign editor
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class MessageSOpenSignEditor {
	public UUID playerUUID;
	public BlockPos signPos;

	public MessageSOpenSignEditor(UUID playerUUID, BlockPos signPos) {
		this.playerUUID = playerUUID;
		this.signPos = signPos;
	}

	public void serialize(PacketBuffer buf) {
		buf.writeUniqueId(this.playerUUID);
		buf.writeBlockPos(this.signPos);
	}

	public static MessageSOpenSignEditor deserialize(PacketBuffer buf) {
		return new MessageSOpenSignEditor(buf.readUniqueId(), buf.readBlockPos());
	}

	public static void handle(MessageSOpenSignEditor message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			if (!ClientInfo.getClientPlayer().getUniqueID().equals(message.playerUUID)) return;
			context.enqueueWork(() -> {
				TileEntity tileentity = ClientInfo.getClientPlayerWorld().getTileEntity(message.signPos);
				if (!(tileentity instanceof AbnormalsSignTileEntity)) {
					tileentity = new AbnormalsSignTileEntity();
					tileentity.setWorldAndPos(ClientInfo.getClientPlayerWorld(), message.signPos);
				}

				NetworkUtil.openSignScreen((AbnormalsSignTileEntity) tileentity);
			});
		}
		context.setPacketHandled(true);
	}
}