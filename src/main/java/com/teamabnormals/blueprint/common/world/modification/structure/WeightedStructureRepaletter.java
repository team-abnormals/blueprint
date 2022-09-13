package com.teamabnormals.blueprint.common.world.modification.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link StructureRepaletter} that replaces tagged {@link BlockState} instances with weighted random blocks.
 * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see StructureRepaletter
 */
public record WeightedStructureRepaletter(TagKey<Block> replacesBlocks, WeightedRandomList<WeightedEntry.Wrapper<Block>> replacesWith) implements StructureRepaletter {
	@SuppressWarnings("deprecation")
	public static final Codec<WeightedStructureRepaletter> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				TagKey.codec(Registry.BLOCK_REGISTRY).fieldOf("replaces_blocks").forGetter(repaletter -> repaletter.replacesBlocks),
				WeightedRandomList.codec(WeightedEntry.Wrapper.codec(Registry.BLOCK.byNameCodec())).fieldOf("replaces_with").forGetter(repaletter -> repaletter.replacesWith)
		).apply(instance, WeightedStructureRepaletter::new);
	});

	@Nullable
	@Override
	public BlockState getReplacement(ServerLevelAccessor level, BlockState state, RandomSource random) {
		return state.is(this.replacesBlocks) ? this.replacesWith.getRandom(random).orElseThrow().getData().withPropertiesOf(state) : null;
	}

	@Override
	public Codec<? extends StructureRepaletter> codec() {
		return CODEC;
	}
}
