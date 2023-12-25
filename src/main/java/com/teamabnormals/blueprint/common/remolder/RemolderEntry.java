package com.teamabnormals.blueprint.common.remolder;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.teamabnormals.blueprint.common.remolder.data.MoldingTypes;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;
import com.teamabnormals.blueprint.core.util.modification.selection.ResourceSelector;

import javax.annotation.Nullable;

/**
 * Represents a compilable {@link Remolder} entry that can be applied to select resources.
 * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
// TODO: Clean up when cleaning up resource selectors!
public record RemolderEntry(ConditionedResourceSelector pathSelector, @Nullable ResourceSelector<?> packSelector, MoldingTypes.MoldingType<?> molding, Remolder remolder) {
	public static final RemolderEntry NOOP = new RemolderEntry(ConditionedResourceSelector.EMPTY, null, MoldingTypes.JSON, RemolderTypes.noop());
	public static final Codec<RemolderEntry> CODEC = new Codec<>() {
		private final Codec<ResourceSelector<?>> packSelectorCodec = ResourceSelector.CODEC.optionalFieldOf("pack_selector", null).codec();
		private final Codec<MoldingTypes.MoldingType<?>> moldingCodec = MoldingTypes.TYPE_CODEC.fieldOf("molding").codec();
		private final Codec<Remolder> remolderCodec = Remolder.CODEC.fieldOf("remolder").codec();

		@Override
		public <T> DataResult<Pair<RemolderEntry, T>> decode(DynamicOps<T> dynamicOps, T prefix) {
			return ConditionedResourceSelector.CODEC.decode(dynamicOps, prefix).flatMap(pair -> {
				ConditionedResourceSelector pathSelector = pair.getFirst();
				if (pathSelector == ConditionedResourceSelector.EMPTY) return DataResult.success(Pair.of(NOOP, prefix));
				var packSelectorDecodeDataResult = this.packSelectorCodec.decode(dynamicOps, prefix);
				var packSelectorDecodeError = packSelectorDecodeDataResult.error();
				if (packSelectorDecodeError.isPresent()) {
					String message = packSelectorDecodeError.get().message();
					return DataResult.error(() -> message);
				}
				var moldingDecodeDataResult = this.moldingCodec.decode(dynamicOps, prefix);
				var moldingDecodeError = moldingDecodeDataResult.error();
				if (moldingDecodeError.isPresent()) {
					String message = moldingDecodeError.get().message();
					return DataResult.error(() -> message);
				}
				var remolderDecodeDataResult = this.remolderCodec.decode(dynamicOps, prefix);
				var remolderDecodeError = remolderDecodeDataResult.error();
				if (remolderDecodeError.isPresent()) {
					String message = remolderDecodeError.get().message();
					return DataResult.error(() -> message);
				}
				return DataResult.success(Pair.of(new RemolderEntry(pathSelector, packSelectorDecodeDataResult.result().get().getFirst(), moldingDecodeDataResult.result().get().getFirst(), remolderDecodeDataResult.result().get().getFirst()), prefix));
			});
		}

		@Override
		public <T> DataResult<T> encode(RemolderEntry remolderEntry, DynamicOps<T> dynamicOps, T prefix) {
			return ConditionedResourceSelector.CODEC.encode(remolderEntry.pathSelector(), dynamicOps, prefix).flatMap(newPrefix -> {
				return this.packSelectorCodec.encode(remolderEntry.packSelector(), dynamicOps, newPrefix).flatMap(newerPrefix -> {
					return this.moldingCodec.encode(remolderEntry.molding(), dynamicOps, newerPrefix).flatMap(newestPrefix -> {
						return this.remolderCodec.encode(remolderEntry.remolder(), dynamicOps, newestPrefix);
					});
				});
			});
		}
	};
}
