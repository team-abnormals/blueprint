package com.teamabnormals.abnormals_core.core.utils;

import org.apache.commons.lang3.ArrayUtils;

import com.teamabnormals.abnormals_core.client.ClientInfo;
import com.teamabnormals.abnormals_core.client.screen.AbnormalsSignEditorScreen;
import com.teamabnormals.abnormals_core.common.network.MessageC2SEditSign;
import com.teamabnormals.abnormals_core.common.network.MessageS2CEndimation;
import com.teamabnormals.abnormals_core.common.network.MessageS2CUpdateSign;
import com.teamabnormals.abnormals_core.common.network.MessageSOpenSignEditor;
import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.library.endimator.Endimation;
import com.teamabnormals.abnormals_core.core.library.endimator.entity.IEndimatedEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author - SmellyModder(Luke Tonon)
 * This class holds(will eventually) a big list of useful network functions. Most are used in the mod
 */
public class NetworkUtil {

	/**
	 * Sends an animation message to the clients to update an entity's animations
	 * @param entity - The Entity to send the packet for
	 * @param endimationToPlay - The endimation to play
	 */
	public static <E extends Entity & IEndimatedEntity> void setPlayingAnimationMessage(E entity, Endimation endimationToPlay) {
		if(!entity.world.isRemote) {
			AbnormalsCore.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MessageS2CEndimation(entity.getEntityId(), ArrayUtils.indexOf(entity.getEndimations(), endimationToPlay)));
			entity.setPlayingEndimation(endimationToPlay);
		}
	}
	
	public static void openSignEditor(PlayerEntity player, AbnormalsSignTileEntity sign) {
		if(player instanceof ServerPlayerEntity) {
			sign.setPlayer(player);
			AbnormalsCore.CHANNEL.send(PacketDistributor.ALL.noArg(), new MessageSOpenSignEditor(player.getUniqueID(), sign.getPos()));
		}
	}
	
	/**
	 * Send a packet to the server to set the sign's text
	 * @param signPos
	 * @param topLine
	 * @param secondLine
	 * @param thirdLine
	 * @param bottomLine
	 */
	public static void setNewSignText(BlockPos signPos, ITextComponent topLine, ITextComponent secondLine, ITextComponent thirdLine, ITextComponent bottomLine) {
		AbnormalsCore.CHANNEL.sendToServer(new MessageC2SEditSign(signPos, topLine.getString(), secondLine.getString(), thirdLine.getString(), bottomLine.getString()));
	}
	
	/**
	 * Send a packet to the server to update the sign's text
	 * @param signPos
	 * @param topLine
	 * @param secondLine
	 * @param thirdLine
	 * @param bottomLine
	 * @param color
	 */
	public static void updateSignText(BlockPos signPos, ITextComponent topLine, ITextComponent secondLine, ITextComponent thirdLine, ITextComponent bottomLine, DyeColor color) {
		AbnormalsCore.CHANNEL.send(PacketDistributor.ALL.noArg(), new MessageS2CUpdateSign(signPos, topLine.getString(), secondLine.getString(), thirdLine.getString(), bottomLine.getString(), color.getId()));
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void openSignScreen(AbnormalsSignTileEntity sign) {
		ClientInfo.MINECRAFT.displayGuiScreen(new AbnormalsSignEditorScreen(sign));
	}
}