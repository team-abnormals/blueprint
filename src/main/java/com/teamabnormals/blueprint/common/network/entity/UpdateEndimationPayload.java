package com.teamabnormals.blueprint.common.network.entity;

import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimation;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimationManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * The message for telling clients to begin playing a {@link PlayableEndimation} on an {@link Endimatable} entity.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record UpdateEndimationPayload(int entityId, int endimationId) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<UpdateEndimationPayload> TYPE = new CustomPacketPayload.Type<>(Blueprint.location("update_endimation"));
	public static final StreamCodec<ByteBuf, UpdateEndimationPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, UpdateEndimationPayload::entityId,
			ByteBufCodecs.VAR_INT, UpdateEndimationPayload::endimationId,
			UpdateEndimationPayload::new
	);

	public static void handle(UpdateEndimationPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Endimatable endimatedEntity = (Endimatable) ClientInfo.getClientPlayerLevel().getEntity(payload.entityId);
			if (endimatedEntity != null) {
				int id = payload.endimationId;
				PlayableEndimation endimation = PlayableEndimationManager.INSTANCE.getEndimation(id);
				if (endimation == null) {
					Blueprint.LOGGER.warn("Could not find Playable Endimation with ID " + id + " to play, defaulting to blank.");
					endimatedEntity.resetEndimation();
				} else {
					endimatedEntity.setPlayingEndimation(endimation);
				}
			}
		}).exceptionally(e -> null);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}