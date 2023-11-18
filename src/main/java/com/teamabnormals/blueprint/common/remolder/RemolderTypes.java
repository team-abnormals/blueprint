package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.common.remolder.data.DynamicReference;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * The global registry for {@link Remolder} types/serializers.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class RemolderTypes {
	static final BasicRegistry<Codec<? extends Remolder>> REGISTRY = new BasicRegistry<>();

	static {
		REGISTRY.register("replace", ReplaceRemolder.CODEC);
		REGISTRY.register("remove", RemoveRemolder.CODEC);
		REGISTRY.register("add", AddRemolder.CODEC);
		REGISTRY.register("noop", NoopRemolder.CODEC);
		REGISTRY.register("sequence", SequenceRemolder.CODEC);
	}

	public static synchronized void register(ResourceLocation name, Codec<? extends Remolder> codec) {
		REGISTRY.register(name, codec);
	}

	public static ReplaceRemolder replace(DynamicReference.Expression target, DynamicReference value) {
		return new ReplaceRemolder(target, value);
	}

	public static RemoveRemolder remove(DynamicReference.Expression target) {
		return new RemoveRemolder(target);
	}

	public static AddRemolder add(DynamicReference.Expression target, DynamicReference value) {
		return new AddRemolder(target, value);
	}

	public static NoopRemolder noop() {
		return NoopRemolder.INSTANCE;
	}

	public static SequenceRemolder sequence(Remolder... remolders) {
		return new SequenceRemolder(List.of(remolders));
	}
}
