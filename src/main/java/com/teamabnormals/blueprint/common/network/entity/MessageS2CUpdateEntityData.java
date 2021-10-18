package com.teamabnormals.blueprint.common.network.entity;

import com.google.common.collect.Sets;
import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Set;
import java.util.function.Supplier;

/**
 * The message for updating data about a {@link IDataManager} on clients.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class MessageS2CUpdateEntityData {
	private final int entityId;
	private final Set<IDataManager.DataEntry<?>> entries;

	public MessageS2CUpdateEntityData(int entityId, Set<IDataManager.DataEntry<?>> entries) {
		this.entityId = entityId;
		this.entries = entries;
	}

	public void serialize(FriendlyByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeInt(this.entries.size());
		this.entries.forEach(entry -> entry.write(buf));
	}

	public static MessageS2CUpdateEntityData deserialize(FriendlyByteBuf buf) {
		int entityId = buf.readInt();
		int size = buf.readInt();
		Set<IDataManager.DataEntry<?>> entries = Sets.newHashSet();
		for (int i = 0; i < size; i++) {
			entries.add(IDataManager.DataEntry.read(buf));
		}
		return new MessageS2CUpdateEntityData(entityId, entries);
	}

	public static void handle(MessageS2CUpdateEntityData message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(() -> {
				Entity entity = ClientInfo.getClientPlayerLevel().getEntity(message.entityId);
				if (entity instanceof IDataManager) {
					message.entries.forEach(dataEntry -> setTrackedValue(entity, dataEntry));
				}
			});
			context.setPacketHandled(true);
		}
	}

	private static <T> void setTrackedValue(Entity entity, IDataManager.DataEntry<T> entry) {
		TrackedDataManager.INSTANCE.setValue(entity, entry.getTrackedData(), entry.getValue());
	}
}
