package com.teamabnormals.blueprint.core.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.world.modification.ModdednessSliceGetter;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * The class for Blueprint's surface rule types.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintSurfaceRules extends SurfaceRules {
	/**
	 * Registers Blueprint's surface rule types.
	 * <p><b>This is for internal use only!</b></p>
	 */
	public static void register() {
		BuiltinRegistries.register(Registry.CONDITION, new ResourceLocation(Blueprint.MOD_ID, "moddedness_slice"), ModdednessSliceConditionSource.CODEC);
	}

	/**
	 * A {@link SurfaceRules.ConditionSource} implementation that checks for a named moddedness slice.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record ModdednessSliceConditionSource(ResourceLocation sliceName) implements SurfaceRules.ConditionSource {
		public static final Codec<ModdednessSliceConditionSource> CODEC = RecordCodecBuilder.create(instance -> {
			return instance.group(
					ResourceLocation.CODEC.fieldOf("slice_name").forGetter(condition -> condition.sliceName)
			).apply(instance, ModdednessSliceConditionSource::new);
		});

		@Override
		public Codec<? extends ConditionSource> codec() {
			return CODEC;
		}

		@Override
		public SurfaceRules.Condition apply(Context context) {
			ModdednessSliceGetter moddednessSliceGetter = ModdednessSliceGetter.class.cast(context);
			if (moddednessSliceGetter.cannotGetSlices()) return () -> false;

			class ModdednessSliceCondition implements SurfaceRules.Condition {
				private long lastUpdateXZ;
				private boolean lastResult;

				@Override
				public boolean test() {
					long lastUpdateXZ = context.lastUpdateXZ;
					if (this.lastUpdateXZ == lastUpdateXZ) {
						return this.lastResult;
					}
					this.lastUpdateXZ = lastUpdateXZ;
					return this.lastResult = moddednessSliceGetter.getSliceName(QuartPos.fromBlock(context.blockX), QuartPos.fromBlock(context.blockZ)).equals(ModdednessSliceConditionSource.this.sliceName);
				}
			}

			return new ModdednessSliceCondition();
		}
	}
}
