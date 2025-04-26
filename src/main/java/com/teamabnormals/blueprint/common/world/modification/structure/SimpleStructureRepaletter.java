package com.teamabnormals.blueprint.common.world.modification.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link StructureRepaletter} that replaces {@link BlockState} instances containing a specific {@link Block} instance.
 * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletter
 */
public record SimpleStructureRepaletter(Block replacesBlock, Block replacesWith) implements StructureRepaletter, StructureRepaletter.Replacer {

	public static final MapCodec<SimpleStructureRepaletter> CODEC = RecordCodecBuilder.mapCodec(instance -> {
		return instance.group(
				BuiltInRegistries.BLOCK.byNameCodec().fieldOf("replaces_block").forGetter(repaletter -> repaletter.replacesBlock),
				BuiltInRegistries.BLOCK.byNameCodec().fieldOf("replaces_with").forGetter(repaletter -> repaletter.replacesWith)
		).apply(instance, SimpleStructureRepaletter::new);
	});

	@Override
	public Replacer createReplacer(StructureModificationContext context) {
		return this;
	}

	@Nullable
	@Override
	public BlockState getReplacement(ServerLevelAccessor level, BlockState state, RandomSource random) {
		return state.is(this.replacesBlock) ? this.replacesWith.withPropertiesOf(state) : null;
	}

	@Override
	public MapCodec<? extends StructureRepaletter> codec() {
		return CODEC;
	}

	@Override
	public MapCodec<? extends Replacer> savedTagCodec() {
		return CODEC;
	}
}
