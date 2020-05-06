package com.teamabnormals.abnormals_core.core.utils;

import org.apache.commons.lang3.ArrayUtils;

import com.teamabnormals.abnormals_core.common.network.MessageS2CEndimation;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.library.endimator.Endimation;
import com.teamabnormals.abnormals_core.core.library.endimator.entity.IEndimatedEntity;

import net.minecraft.entity.Entity;
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
	
}