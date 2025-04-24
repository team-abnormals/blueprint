package com.teamabnormals.blueprint.common.network.entity;

import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * The message for updating data about a {@link IDataManager} on clients.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record UpdateEntityDataPayload(int entityId, List<IDataManager.DataEntry<?>> entries) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<UpdateEntityDataPayload> TYPE = new CustomPacketPayload.Type<>(Blueprint.location("update_entity_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateEntityDataPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, UpdateEntityDataPayload::entityId,
			IDataManager.DataEntry.LIST_STREAM_CODEC, UpdateEntityDataPayload::entries,
			UpdateEntityDataPayload::new
	);

	public static void handle(UpdateEntityDataPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Entity entity = ClientInfo.getClientPlayerLevel().getEntity(payload.entityId);
			if (entity instanceof IDataManager) {
				payload.entries.forEach(dataEntry -> setTrackedValue(entity, dataEntry));
			}
		}).exceptionally(e -> null);
	}

	private static <T> void setTrackedValue(Entity entity, IDataManager.DataEntry<T> entry) {
		TrackedDataManager.INSTANCE.setValue(entity, entry.getTrackedData(), entry.getValue());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
