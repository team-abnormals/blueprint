package com.teamabnormals.blueprint.core.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.world.modification.ModdednessSliceGetter;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * The class for Blueprint's surface rule types.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintSurfaceRules extends SurfaceRules {
	public static final DeferredRegister<Codec<? extends ConditionSource>> CONDITIONS = DeferredRegister.create(Registry.CONDITION_REGISTRY, Blueprint.MOD_ID);

	public static final RegistryObject<Codec<? extends ConditionSource>> MODDED_SLICE = CONDITIONS.register("modded_slice", ModdednessSliceConditionSource.CODEC::codec);

	/**
	 * A {@link SurfaceRules.ConditionSource} implementation that checks for a named moddedness slice.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record ModdednessSliceConditionSource(ResourceLocation sliceName) implements SurfaceRules.ConditionSource {
		public static final KeyDispatchDataCodec<ModdednessSliceConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.create(instance -> {
			return instance.group(
					ResourceLocation.CODEC.fieldOf("slice_name").forGetter(condition -> condition.sliceName)
			).apply(instance, ModdednessSliceConditionSource::new);
		}));

		@Override
		public KeyDispatchDataCodec<? extends ConditionSource> codec() {
			return CODEC;
		}

		@Override
		public SurfaceRules.Condition apply(Context surfaceRulesContext) {
			ModdednessSliceGetter moddednessSliceGetter = ModdednessSliceGetter.class.cast(surfaceRulesContext);
			if (moddednessSliceGetter.cannotGetSlices()) return () -> false;

			class ModdednessSliceCondition extends SurfaceRules.LazyYCondition {
				ModdednessSliceCondition() {
					super(surfaceRulesContext);
				}

				@Override
				protected boolean compute() {
					return moddednessSliceGetter.getSliceName().equals(ModdednessSliceConditionSource.this.sliceName);
				}
			}

			return new ModdednessSliceCondition();
		}
	}
}
