package com.teamabnormals.blueprint.core.registry;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The class for Blueprint's surface rule types.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintSurfaceRules extends SurfaceRules {
	public static final DeferredRegister<Codec<? extends RuleSource>> RULE_SOURCES = DeferredRegister.create(Registries.MATERIAL_RULE, Blueprint.MOD_ID);

	public static final RegistryObject<Codec<? extends RuleSource>> TRANSIENT_MERGED = RULE_SOURCES.register("transient_merged", TransientMergedRuleSource.CODEC::codec);

	private record SequenceRule(List<SurfaceRules.SurfaceRule> rules) implements SurfaceRules.SurfaceRule {
		@Nullable
		@Override
		public BlockState tryApply(int x, int y, int z) {
			for (SurfaceRules.SurfaceRule surfaceRule : this.rules) {
				BlockState blockstate = surfaceRule.tryApply(x, y, z);
				if (blockstate != null) {
					return blockstate;
				}
			}
			return null;
		}
	}

	/**
	 * The {@link RuleSource} type responsible for merging new surface rules with original surface rules.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record TransientMergedRuleSource(List<RuleSource> sequence, RuleSource original) implements SurfaceRules.RuleSource {
		public static final KeyDispatchDataCodec<SurfaceRules.RuleSource> CODEC = KeyDispatchDataCodec.of(RuleSource.CODEC.xmap(source -> source, source -> source instanceof TransientMergedRuleSource transientMergedRuleSource ? transientMergedRuleSource.original : source).fieldOf("original_source"));

		@Override
		public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
			return CODEC;
		}

		@Override
		public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
			if (this.sequence.size() == 1) {
				return this.sequence.get(0).apply(context);
			} else {
				ImmutableList.Builder<SurfaceRules.SurfaceRule> builder = ImmutableList.builder();
				for (SurfaceRules.RuleSource ruleSource : this.sequence) {
					builder.add(ruleSource.apply(context));
				}
				return new SequenceRule(builder.build());
			}
		}
	}
}
