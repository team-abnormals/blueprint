package com.teamabnormals.blueprint.common.remolder.data;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamabnormals.blueprint.common.remolder.RemolderLoader;
import com.teamabnormals.blueprint.common.remolder.Remolding;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public final class MoldingTypes {
	private static final Gson GSON = new GsonBuilder().setLenient().create();
	private static final BasicRegistry<MoldingType<?>> REGISTRY = new BasicRegistry<>();
	public static final Codec<MoldingType<?>> TYPE_CODEC = REGISTRY;
	public static final MetadataSectionSerializer<JsonElement> JSON_METADATA_SERIALIZER = new MetadataSectionSerializer<>() {
		@Override
		public String getMetadataSectionName() {
			return "json";
		}

		@Override
		public JsonElement fromJson(JsonObject object) {
			return object;
		}
	};

	public static final MoldingType<JsonElement> JSON = register(
			"json",
			JsonMolding.ELEMENT_DATA_TYPE,
			JsonMolding::new,
			JsonOps.INSTANCE,
			resource -> {
				try (Reader reader = resource.openAsReader()) {
					return GSON.fromJson(reader, JsonElement.class);
				} catch (IllegalArgumentException | IOException | JsonParseException exception) {
					return null;
				}
			},
			MoldingTypes::serializeJsonElement,
			"json"
	);

	public static synchronized <T> MoldingType<T> register(String name, DataType<T> dataType, Molding.Factory factory, DynamicOps<T> ops, Function<Resource, T> deserializer, Function<T, byte[]> serializer, String... fileExtensions) {
		var moldingType = new MoldingType<>(dataType, factory, ops, deserializer, serializer, fileExtensions);
		REGISTRY.register(name, moldingType);
		return moldingType;
	}

	private static byte[] serializeJsonElement(JsonElement element) {
		return GSON.toJson(element).getBytes(StandardCharsets.UTF_8);
	}

	public record MoldingType<T>(DataType<T> dataType, Molding.Factory factory, DynamicOps<T> ops, Function<Resource, T> deserializer, Function<T, byte[]> serializer, String... fileExtensions) {
		@SuppressWarnings("unchecked")
		private static <T> T getMetadata(Resource resource, DynamicOps<T> ops) {
			try {
				var optional = resource.metadata().getSection(JSON_METADATA_SERIALIZER);
				if (optional.isPresent())
					return ops instanceof JsonOps ? (T) optional.get() : JsonOps.INSTANCE.convertTo(ops, optional.get());
			} catch (IOException ignored) {
			}
			return null;
		}

		private static void logFailedRemolder(Remolding<?> remolding, Exception exception, String location) {
			Blueprint.LOGGER.error("Error while applying remolder {}: {}", remolding, exception);
			Blueprint.LOGGER.warn("Restoring and stopping Remolder data changes at location: {}", location);
		}

		@SuppressWarnings("unchecked")
		public Resource remold(String location, Resource resource, Collection<RemolderLoader.Entry> entries) {
			T root = this.deserializer().apply(resource);
			if (root == null) return resource;
			String pack = resource.sourcePackId();
			DynamicOps<T> ops = this.ops;
			T metadata = getMetadata(resource, ops);
			Pair<T, T> result;
			for (RemolderLoader.Entry entry : entries) {
				if (!entry.packFilter().test(pack)) continue;
				try {
					result = ((Remolding<T>) entry.remolding()).apply(ops, root, metadata);
				} catch (Exception exception) {
					logFailedRemolder(entry.remolding(), exception, location);
					return resource;
				}
				root = result.getFirst();
				metadata = result.getSecond();
			}
			byte[] serializedRoot = this.serializer().apply(root);
			if (metadata == null) {
				return new RemoldedResource(resource.source(), () -> new ByteArrayInputStream(serializedRoot));
			} else {
				try {
					byte[] serializedMetadata = serializeJsonElement(metadata instanceof JsonElement element ? element : ops.convertTo(JsonOps.INSTANCE, metadata));
					return new RemoldedResource(resource.source(), () -> new ByteArrayInputStream(serializedRoot), () -> ResourceMetadata.fromJsonStream(new ByteArrayInputStream(serializedMetadata)));
				} catch (JsonIOException exception) {
					Blueprint.LOGGER.error("Failed to serialize metadata", exception);
					return resource;
				}
			}
		}
	}

	private static class RemoldedResource extends Resource {
		public RemoldedResource(PackResources source, IoSupplier<InputStream> streamSupplier) {
			super(source, streamSupplier);
		}

		public RemoldedResource(PackResources source, IoSupplier<InputStream> streamSupplier, IoSupplier<ResourceMetadata> metadataSupplier) {
			super(source, streamSupplier, metadataSupplier);
		}

		@Override
		public Optional<KnownPack> knownPackInfo() {
			return Optional.empty();
		}
	}
}
