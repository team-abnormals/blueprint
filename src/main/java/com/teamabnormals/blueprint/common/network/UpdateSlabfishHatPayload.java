package com.teamabnormals.blueprint.common.network;

import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Message for syncing Slabfish hat customization settings to the server.
 *
 * @author Jackson
 * @author SmellyModder (Luke Tonon)
 */
public record UpdateSlabfishHatPayload(byte setting) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<UpdateSlabfishHatPayload> TYPE = new CustomPacketPayload.Type<>(Blueprint.location("update_slabfish_hat"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSlabfishHatPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BYTE, UpdateSlabfishHatPayload::setting,
			UpdateSlabfishHatPayload::new
	);

	public static void handle(UpdateSlabfishHatPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Player player = context.player();
			if (player instanceof IDataManager)
				((IDataManager) player).setValue(Blueprint.SLABFISH_SETTINGS, payload.setting);
		}).exceptionally(e -> null);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}