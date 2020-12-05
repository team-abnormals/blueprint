package com.minecraftabnormals.abnormals_core.common.network;

import java.util.function.Supplier;

import com.minecraftabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import com.minecraftabnormals.abnormals_core.core.util.NetworkUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Message for updating edited sign text from the client to the server
 *
 * @author - SmellyModder(Luke Tonon)
 */
public final class MessageC2SEditSign {
	private BlockPos signPos;
	private String topLine, secondLine, thirdLine, bottomLine;

	public MessageC2SEditSign(BlockPos signPos, String topLine, String secondLine, String thirdLine, String bottomLine) {
		this.signPos = signPos;
		this.topLine = topLine;
		this.secondLine = secondLine;
		this.thirdLine = thirdLine;
		this.bottomLine = bottomLine;
	}

	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(this.signPos);

		buf.writeString(this.topLine);
		buf.writeString(this.secondLine);
		buf.writeString(this.thirdLine);
		buf.writeString(this.bottomLine);
	}

	public static MessageC2SEditSign deserialize(PacketBuffer buf) {
		return new MessageC2SEditSign(buf.readBlockPos(), buf.readString(32767), buf.readString(32767), buf.readString(32767), buf.readString(32767));
	}

	@SuppressWarnings("deprecation")
	public static void handle(MessageC2SEditSign message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
			context.enqueueWork(() -> {
				ServerPlayerEntity player = context.getSender();
				player.markPlayerActive();

				ServerWorld world = player.getServerWorld();
				BlockPos blockpos = message.signPos;
				if (world.isBlockLoaded(blockpos)) {
					BlockState blockstate = world.getBlockState(blockpos);
					TileEntity tileentity = world.getTileEntity(blockpos);
					if (!(tileentity instanceof AbnormalsSignTileEntity)) return;

					AbnormalsSignTileEntity signtileentity = (AbnormalsSignTileEntity) tileentity;
					if (!signtileentity.getIsEditable() || signtileentity.getPlayer() != player) {
						return;
					}

					signtileentity.setText(0, new StringTextComponent(TextFormatting.getTextWithoutFormattingCodes(message.topLine)));
					signtileentity.setText(1, new StringTextComponent(TextFormatting.getTextWithoutFormattingCodes(message.secondLine)));
					signtileentity.setText(2, new StringTextComponent(TextFormatting.getTextWithoutFormattingCodes(message.thirdLine)));
					signtileentity.setText(3, new StringTextComponent(TextFormatting.getTextWithoutFormattingCodes(message.bottomLine)));

					signtileentity.markDirty();
					world.notifyBlockUpdate(blockpos, blockstate, blockstate, 3);

					NetworkUtil.updateSignText(signtileentity.getPos(), signtileentity.signText[0], signtileentity.signText[1], signtileentity.signText[2], signtileentity.signText[3], signtileentity.getTextColor());
				}
			});
			context.setPacketHandled(true);
		}
	}
}