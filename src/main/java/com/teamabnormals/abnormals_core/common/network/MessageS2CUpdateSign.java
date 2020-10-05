package com.teamabnormals.abnormals_core.common.network;

import java.util.function.Supplier;

import com.teamabnormals.abnormals_core.client.ClientInfo;
import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.DyeColor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Update message for the signs
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class MessageS2CUpdateSign {
	private BlockPos signPos;
	private String topLine, secondLine, thirdLine, bottomLine;
	private int color;

	public MessageS2CUpdateSign(BlockPos signPos, String topLine, String secondLine, String thirdLine, String bottomLine, int color) {
		this.signPos = signPos;
		this.topLine = topLine;
		this.secondLine = secondLine;
		this.thirdLine = thirdLine;
		this.bottomLine = bottomLine;
		this.color = color;
	}

	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(this.signPos);

		buf.writeString(this.topLine);
		buf.writeString(this.secondLine);
		buf.writeString(this.thirdLine);
		buf.writeString(this.bottomLine);

		buf.writeInt(this.color);
	}

	public static MessageS2CUpdateSign deserialize(PacketBuffer buf) {
		return new MessageS2CUpdateSign(buf.readBlockPos(), buf.readString(), buf.readString(), buf.readString(), buf.readString(), buf.readInt());
	}

	@SuppressWarnings("deprecation")
	public static void handle(MessageS2CUpdateSign message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(() -> {
				ClientWorld world = (ClientWorld) ClientInfo.getClientPlayerWorld();
				BlockPos blockpos = message.signPos;
				if (world.isBlockLoaded(blockpos)) {
					TileEntity tileentity = world.getTileEntity(blockpos);
					if (!(tileentity instanceof AbnormalsSignTileEntity)) return;

					AbnormalsSignTileEntity signtileentity = (AbnormalsSignTileEntity) tileentity;

					signtileentity.setText(0, new StringTextComponent(TextFormatting.getTextWithoutFormattingCodes(message.topLine)));
					signtileentity.setText(1, new StringTextComponent(TextFormatting.getTextWithoutFormattingCodes(message.secondLine)));
					signtileentity.setText(2, new StringTextComponent(TextFormatting.getTextWithoutFormattingCodes(message.thirdLine)));
					signtileentity.setText(3, new StringTextComponent(TextFormatting.getTextWithoutFormattingCodes(message.bottomLine)));

					signtileentity.setTextColor(DyeColor.byId(message.color));
				}
			});
			context.setPacketHandled(true);
		}
	}
}