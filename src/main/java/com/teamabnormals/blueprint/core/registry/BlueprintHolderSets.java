package com.teamabnormals.blueprint.core.registry;

import com.google.gson.JsonArray;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.codec.BlueprintExtraCodecs;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.*;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.holdersets.CompositeHolderSet;
import net.minecraftforge.registries.holdersets.HolderSetType;
import net.minecraftforge.registries.holdersets.ICustomHolderSet;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registry class for Blueprint's {@link ICustomHolderSet} implementations.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintHolderSets {
	public static final DeferredRegister<HolderSetType> HOLDER_SET_TYPES = DeferredRegister.create(ForgeRegistries.Keys.HOLDER_SET_TYPES, Blueprint.MOD_ID);

	public static final RegistryObject<HolderSetType> CONDITIONAL = HOLDER_SET_TYPES.register("conditional", () -> ConditionalHolderSet::codec);

	/**
	 * Creates a new {@link ConditionalHolderSet} instance for simple serialization.
	 *
	 * @param value      A {@link HolderSet} instance to wrap.
	 * @param conditions An array of {@link ICondition} instances to use.
	 * @param <T>        The type of elements in the set.
	 * @return A new {@link ConditionalHolderSet} instance for simple serialization.
	 */
	public static <T> ConditionalHolderSet<T> conditional(HolderSet<T> value, ICondition... conditions) {
		Either<ICondition[], JsonArray> either = Either.left(conditions);
		return new ConditionalHolderSet<>(null, context -> either, value);
	}

	/**
	 * An extension of {@link ConditionalHolderSet} that uses another holder set if conditions are met.
	 * <p>If the conditions are not met, the set is empty.</p>
	 *
	 * @param <T> The type of elements in the set.
	 */
	public static class ConditionalHolderSet<T> extends CompositeHolderSet<T> {
		public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
			return RecordCodecBuilder.<ConditionalHolderSet<T>>create(builder -> builder.group(
					RegistryOps.retrieveRegistryLookup(registryKey).forGetter(set -> set.registryLookup),
					BlueprintExtraCodecs.CONDITIONS.fieldOf("conditions").forGetter(set -> set.conditions),
					HolderSetCodec.create(registryKey, holderCodec, forceList).fieldOf("value").forGetter(set -> set.getComponents().get(0))
			).apply(builder, ConditionalHolderSet::new));
		}

		private final HolderLookup.RegistryLookup<T> registryLookup;
		private final Function<ICondition.IContext, Either<ICondition[], JsonArray>> conditions;

		public ConditionalHolderSet(HolderLookup.RegistryLookup<T> registryLookup, Function<ICondition.IContext, Either<ICondition[], JsonArray>> conditions, HolderSet<T> value) {
			super(List.of(value));
			this.registryLookup = registryLookup;
			this.conditions = conditions;
		}

		@Override
		public HolderSetType type() {
			return CONDITIONAL.get();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Set<Holder<T>> createSet() {
			var registryLookup = this.registryLookup;
			// Lookup has to be nullable to make serialization possible. Forge bug temporary fix
			if (registryLookup == null) return Set.of();
			ICondition.IContext context = new ICondition.IContext() {
				@Override
				public <CT> Map<ResourceLocation, Collection<Holder<CT>>> getAllTags(ResourceKey<? extends Registry<CT>> registryKey) {
					return registryKey == registryLookup.key() ? registryLookup.listTags().collect(Collectors.toMap(key -> key.key().location(), key -> ((HolderSet.Named<CT>) key).stream().collect(Collectors.toSet()))) : Map.of();
				}
			};
			return this.conditions.apply(context).left().isPresent() ? this.getComponents().stream().flatMap(HolderSet::stream).collect(Collectors.toUnmodifiableSet()) : Set.of();
		}
	}
}
