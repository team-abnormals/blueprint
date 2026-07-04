package com.teamabnormals.blueprint.common.remolder;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.teamabnormals.blueprint.common.remolder.data.MoldingTypes;
import com.teamabnormals.blueprint.core.util.modification.selection.ConditionedResourceSelector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a compilable {@link Remolder} entry that can be applied to select resources.
 * <p>Use {@link #CODEC} for serializing and deserializing instances of this class.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
// TODO: Clean up when cleaning up resource selectors!
public record RemolderEntry(ConditionedResourceSelector pathSelector, @Nullable Set<String> packs, MoldingTypes.MoldingType<?> molding, int priority, Remolder remolder) {
	public static final RemolderEntry NOOP = new RemolderEntry(ConditionedResourceSelector.EMPTY, null, MoldingTypes.JSON, RemolderTypes.noop());
	public static final Codec<RemolderEntry> CODEC = new Codec<>() {
		private final Codec<Set<String>> packsCodec = Codec.STRING.listOf().xmap(strings -> (Set<String>) new HashSet<>(strings), ArrayList::new).optionalFieldOf("packs", null).codec();
		private final Codec<MoldingTypes.MoldingType<?>> moldingCodec = MoldingTypes.TYPE_CODEC.fieldOf("molding").codec();
		private final Codec<Integer> priorityCodec = Codec.INT.optionalFieldOf("priority", 1000).codec();
		private final Codec<Remolder> remolderCodec = Remolder.CODEC.fieldOf("remolder").codec();

		@Override
		public <T> DataResult<Pair<RemolderEntry, T>> decode(DynamicOps<T> dynamicOps, T prefix) {
			return ConditionedResourceSelector.CODEC.decode(dynamicOps, prefix).flatMap(pair -> {
				ConditionedResourceSelector pathSelector = pair.getFirst();
				if (pathSelector == ConditionedResourceSelector.EMPTY) return DataResult.success(Pair.of(NOOP, prefix));
				var packsDecodeDataResult = this.packsCodec.decode(dynamicOps, prefix);
				var packsDecodeError = packsDecodeDataResult.error();
				if (packsDecodeError.isPresent()) {
					String message = packsDecodeError.get().message();
					return DataResult.error(() -> message);
				}
				var moldingDecodeDataResult = this.moldingCodec.decode(dynamicOps, prefix);
				var moldingDecodeError = moldingDecodeDataResult.error();
				if (moldingDecodeError.isPresent()) {
					String message = moldingDecodeError.get().message();
					return DataResult.error(() -> message);
				}
				var priorityDecodeDataResult = this.priorityCodec.decode(dynamicOps, prefix);
				var priorityDecodeError = priorityDecodeDataResult.error();
				if (priorityDecodeError.isPresent()) {
					String message = priorityDecodeError.get().message();
					return DataResult.error(() -> message);
				}
				var remolderDecodeDataResult = this.remolderCodec.decode(dynamicOps, prefix);
				var remolderDecodeError = remolderDecodeDataResult.error();
				if (remolderDecodeError.isPresent()) {
					String message = remolderDecodeError.get().message();
					return DataResult.error(() -> message);
				}
				return DataResult.success(Pair.of(new RemolderEntry(pathSelector, packsDecodeDataResult.result().get().getFirst(), moldingDecodeDataResult.result().get().getFirst(), priorityDecodeDataResult.result().get().getFirst(), remolderDecodeDataResult.result().get().getFirst()), prefix));
			});
		}

		@Override
		public <T> DataResult<T> encode(RemolderEntry remolderEntry, DynamicOps<T> dynamicOps, T prefix) {
			return ConditionedResourceSelector.CODEC.encode(remolderEntry.pathSelector(), dynamicOps, prefix).flatMap(prefix2 -> {
				return this.packsCodec.encode(remolderEntry.packs(), dynamicOps, prefix2).flatMap(prefix3 -> {
					return this.moldingCodec.encode(remolderEntry.molding(), dynamicOps, prefix3).flatMap(prefix4 -> {
						return this.priorityCodec.encode(remolderEntry.priority(), dynamicOps, prefix4).flatMap(prefix5 -> {
							return this.remolderCodec.encode(remolderEntry.remolder(), dynamicOps, prefix5);
						});
					});
				});
			});
		}
	};

	public RemolderEntry(ConditionedResourceSelector pathSelector, @Nullable Set<String> packs, MoldingTypes.MoldingType<?> molding, Remolder remolder) {
		this(pathSelector, packs, molding, 1000, remolder);
	}
}
