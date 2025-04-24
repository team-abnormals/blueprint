package com.teamabnormals.blueprint.common.network.entity;

import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.core.Blueprint;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * The message for teleporting the entity from the server.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record TeleportEntityPayload(int entityId, double x, double y, double z) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<TeleportEntityPayload> TYPE = new CustomPacketPayload.Type<>(Blueprint.location("teleport_entity"));
	public static final StreamCodec<ByteBuf, TeleportEntityPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, TeleportEntityPayload::entityId,
			ByteBufCodecs.DOUBLE, TeleportEntityPayload::x,
			ByteBufCodecs.DOUBLE, TeleportEntityPayload::y,
			ByteBufCodecs.DOUBLE, TeleportEntityPayload::z,
			TeleportEntityPayload::new
	);

	public static void handle(TeleportEntityPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Entity entity = ClientInfo.getClientPlayerLevel().getEntity(payload.entityId);
			if (entity != null) {
				entity.moveTo(payload.x, payload.y, payload.z, entity.getYRot(), entity.getXRot());
			}
		}).exceptionally(e -> null);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}