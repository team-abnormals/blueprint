package com.teamabnormals.blueprint.common.network.particle;

import com.teamabnormals.blueprint.client.ClientInfo;
import com.teamabnormals.blueprint.core.Blueprint;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * Message for telling the client to spawn particles with specific options.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record SpawnParticlesPayload(ParticleOptions particleOptions, List<ParticleInstance> instances) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SpawnParticlesPayload> TYPE = new CustomPacketPayload.Type<>(Blueprint.location("spawn_particles"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnParticlesPayload> STREAM_CODEC = StreamCodec.composite(
			ParticleTypes.STREAM_CODEC, SpawnParticlesPayload::particleOptions,
			ParticleInstance.LIST_STREAM_CODEC, SpawnParticlesPayload::instances,
			SpawnParticlesPayload::new
	);

	public static void handle(SpawnParticlesPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Level level = ClientInfo.getClientPlayerLevel();
			ParticleOptions options = payload.particleOptions();
			for (ParticleInstance instance : payload.instances()) {
				level.addParticle(options, instance.x, instance.y, instance.z, instance.velX, instance.velY, instance.velZ);
			}
		}).exceptionally(e -> null);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public record ParticleInstance(double x, double y, double z, double velX, double velY, double velZ) {
		public static final StreamCodec<ByteBuf, ParticleInstance> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.DOUBLE, ParticleInstance::x,
				ByteBufCodecs.DOUBLE, ParticleInstance::y,
				ByteBufCodecs.DOUBLE, ParticleInstance::z,
				ByteBufCodecs.DOUBLE, ParticleInstance::velX,
				ByteBufCodecs.DOUBLE, ParticleInstance::velY,
				ByteBufCodecs.DOUBLE, ParticleInstance::velZ,
				ParticleInstance::new
		);
		public static final StreamCodec<ByteBuf, List<ParticleInstance>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
	}
}