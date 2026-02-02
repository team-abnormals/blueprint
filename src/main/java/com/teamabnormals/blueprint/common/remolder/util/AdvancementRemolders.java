package com.teamabnormals.blueprint.common.remolder.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.teamabnormals.blueprint.common.advancement.modification.modifiers.CriteriaModifier;
import com.teamabnormals.blueprint.common.remolder.Remolder;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.teamabnormals.blueprint.common.remolder.RemolderTypes.*;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.*;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.parse;

/**
 * Utility class for creating {@link Remolder} instances for modifying advancements.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class AdvancementRemolders {
	private static final Codec<Map<String, Criterion<?>>> CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, Criterion.CODEC)
			.validate(map -> map.isEmpty() ? DataResult.error(() -> "Advancement criteria cannot be empty") : DataResult.success(map));
	private static final DisplayInfo DEFAULT_DISPLAY = new DisplayInfo(new ItemStack(Items.STONE), Component.empty(), Component.empty(), Optional.empty(), AdvancementType.TASK, true, true, false);

	/**
	 * Creates a {@link Remolder} instance that remolds advancement criteria and requirements.
	 *
	 * @param criteria                  The criteria to add.
	 * @param requirements              The requirements to add or replace.
	 * @param shouldReplaceRequirements If the old requirements should get fully replaced.
	 * @param indexedRequirements       The requirements to modify at a specific indices.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder remoldCriteria(Map<String, Criterion<?>> criteria, Optional<AdvancementRequirements> requirements, boolean shouldReplaceRequirements, Optional<List<CriteriaModifier.IndexedRequirementsEntry>> indexedRequirements) {
		var sequence = sequence();
		sequence.then(addAllToMap("criteria", "criteria", value(criteria, CRITERIA_CODEC)));
		if (requirements.isPresent()) {
			if (shouldReplaceRequirements) {
				sequence.then(replace(target("requirements"), value(requirements.get(), AdvancementRequirements.CODEC)));
			} else {
				sequence.then(replace(target("$requirements_to_add"), value(requirements.get(), AdvancementRequirements.CODEC)));
				sequence.then(ifElse(
						parse("#isList(requirements)"),
						sequence(
								replace(target("$safe_requirements"), parse("#list(requirements)")),
								replace(target("$generic_adding_iterator"), parse("#elements(#list($requirements_to_add))")),
								loopWhile(parse("#hasNext($generic_adding_iterator)"), add(target("$safe_requirements"), parse("(#) #next($generic_adding_iterator)")))
						),
						replace(target("requirements"), target("$requirements_to_add"))
				));
			}
		}
		if (indexedRequirements.isPresent()) {
			sequence.then(replace(target("$indexed_requirements"), value(indexedRequirements.get(), CriteriaModifier.IndexedRequirementsEntry.CODEC.listOf())));
			sequence.then(remoldIf(parse("#isList(requirements)"), sequence(
					replace(target("$requirements"), parse("#list(requirements)")),
					replace(target("$length"), parse("#size($requirements)")),
					replace(target("$index_requirements_iterator"), parse("#elements(#list($indexed_requirements))")),
					loopWhile(parse("#hasNext($index_requirements_iterator)"), sequence(
							replace(target("$entry"), parse("#map((#) #next($index_requirements_iterator))")),
							replace(target("$index"), parse("(int) $entry[\"index\"]")),
							remoldIf(parse("$index < $length"), sequence(
									ifElse(
											parse("$entry[\"replace\"]"),
											replace(target("$requirements[$index]"), parse("$entry[\"requirements\"]")),
											sequence(
													replace(target("$requirements_at_index"), parse("#list($requirements[$index])")),
													replace(target("$generic_adding_iterator"), parse("#elements(#list($entry[\"requirements\"]))")),
													loopWhile(parse("#hasNext($generic_adding_iterator)"), add(target("$requirements_at_index"), parse("(#) #next($generic_adding_iterator)")))
											)
									)
							))
					))
			)));
		}
		return sequence.complete();
	}

	// TODO: Migrate away from old builder when advancement modifiers die
	public static Remolder criteria(CriteriaModifier criteriaModifier) {
		return remoldCriteria(criteriaModifier.criteria(), criteriaModifier.requirements(), criteriaModifier.shouldReplaceRequirements(), criteriaModifier.indexedRequirements());
	}

	/**
	 * Creates a {@link Remolder} instance that fully replaces the display of an advancement.
	 *
	 * @param displayInfo The {@link DisplayInfoRemolderBuilder} for the new display.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder replaceDisplayInfo(DisplayInfo displayInfo) {
		return replace(target("display"), value(displayInfo, DisplayInfo.CODEC));
	}

	/**
	 * Creates a {@link DisplayInfoRemolderBuilder} instance to configure the modification of the display of an advancement.
	 *
	 * @return The {@link Remolder} instance.
	 */
	public static DisplayInfoRemolderBuilder remoldDisplayInfo() {
		return new DisplayInfoRemolderBuilder();
	}

	/**
	 * Creates a {@link Remolder} instance that fully replaces the effects of an effects changed criterion.
	 *
	 * @param key                 The key of the criterion.
	 * @param mobEffectsPredicate The new {@link MobEffectsPredicate} to replace the old.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder replaceEffectsChanged(String key, MobEffectsPredicate mobEffectsPredicate) {
		return replace(
				target("criteria." + key + ".conditions.effects"),
				value(mobEffectsPredicate, MobEffectsPredicate.CODEC)
		);
	}

	/**
	 * Creates a {@link Remolder} instance that adds to the effects of an effects changed criterion.
	 *
	 * @param key                 The key of the criterion.
	 * @param mobEffectsPredicate The new {@link MobEffectsPredicate} to combine with the existing one.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder addToEffectsChanged(String key, MobEffectsPredicate mobEffectsPredicate) {
		return sequence(addAllToMap(
				"criteria." + key + ".conditions.effects",
				"effects",
				value(mobEffectsPredicate, MobEffectsPredicate.CODEC)
		));
	}

	/**
	 * Creates a {@link Remolder} instance that removes effects of an effects changed criterion.
	 *
	 * @param key        The key of the criterion.
	 * @param mobEffects The effects to remove from the existing effect requirements.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder removeFromEffectsChanged(String key, List<Holder<MobEffect>> mobEffects) {
		return sequence(removeAllFromMap(
				"criteria." + key + ".conditions.effects",
				"effects",
				value(mobEffects, MobEffect.CODEC.listOf())
		));
	}

	/**
	 * Creates a {@link Remolder} instance that replaces the parent of an advancement.
	 *
	 * @param parent The {@link ResourceLocation} of the new parent.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder replaceParent(ResourceLocation parent) {
		return replace(target("parent"), value(parent, ResourceLocation.CODEC));
	}

	/**
	 * Creates a {@link Remolder} instance that fully replaces the rewards of an advancement.
	 *
	 * @param rewards The {@link AdvancementRewards} for the new rewards.
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder replaceRewards(AdvancementRewards rewards) {
		return replace(target("rewards"), value(rewards, AdvancementRewards.CODEC));
	}

	/**
	 * Creates a {@link Remolder} instance that modifies the rewards of an advancement.
	 *
	 * @param experience The new amount of experience (optional).
	 * @param loot       The new loot (optional).
	 * @param recipes    The new recipes (optional).
	 * @param function   The new function (optional).
	 * @return The {@link Remolder} instance.
	 */
	public static Remolder remoldRewards(@Nullable Integer experience, @Nullable List<ResourceKey<LootTable>> loot, @Nullable List<ResourceLocation> recipes, @Nullable ResourceLocation function) {
		var sequence = sequence();
		sequence.then(replace(target("$object"), parse("#map(this)")));
		sequence.then(remoldIf(
				parse("!#isMap($object[\"rewards\"])"),
				replace(target("$object[\"rewards\"]"), value(AdvancementRewards.EMPTY, AdvancementRewards.CODEC))
		));
		sequence.then(replace(target("$rewards"), parse("#map($object[\"rewards\"])")));
		if (experience != null)
			sequence.then(replace(target("$rewards[\"experience\"]"), value(experience, Codec.INT)));
		if (loot != null) {
			sequence.then(remoldIf(parse("!#isList($rewards[\"loot\"])"), replace(target("$rewards[\"loot\"]"), value(DynamicOps::emptyList))));
			sequence.then(addAllToList("$rewards[\"loot\"]", "loot", value(loot, ResourceKey.codec(Registries.LOOT_TABLE).listOf())));
		}
		if (recipes != null) {
			sequence.then(remoldIf(parse("!#isList($rewards[\"recipes\"])"), replace(target("$rewards[\"recipes\"]"), value(DynamicOps::emptyList))));
			sequence.then(addAllToList("$rewards[\"recipes\"]", "recipes", value(recipes, ResourceLocation.CODEC.listOf())));
		}
		if (function != null)
			sequence.then(replace(target("$rewards[\"function\"]"), value(function, ResourceLocation.CODEC)));
		return sequence.complete();
	}

	/**
	 * The builder class for simpler creation remolders that modify displays of advancements.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class DisplayInfoRemolderBuilder {
		private Optional<Component> title = Optional.empty();
		private Optional<Component> description = Optional.empty();
		private Optional<ItemStack> icon = Optional.empty();
		private Optional<Optional<ResourceLocation>> background = Optional.empty();
		private Optional<AdvancementType> type = Optional.empty();
		private Optional<Boolean> showToast = Optional.empty();
		private Optional<Boolean> announceToChat = Optional.empty();
		private Optional<Boolean> hidden = Optional.empty();

		private DisplayInfoRemolderBuilder() {
		}

		/**
		 * Updates the {@link #title}.
		 *
		 * @param title A {@link Component} instance to use as the title.
		 * @return This builder.
		 */
		public DisplayInfoRemolderBuilder title(Component title) {
			this.title = Optional.of(title);
			return this;
		}

		/**
		 * Updates the {@link #description}.
		 *
		 * @param description A {@link Component} instance to use as the description.
		 * @return This builder.
		 */
		public DisplayInfoRemolderBuilder description(Component description) {
			this.description = Optional.of(description);
			return this;
		}

		/**
		 * Updates the {@link #icon}.
		 *
		 * @param icon A {@link ItemStack} instance to use as the icon.
		 * @return This builder.
		 */
		public DisplayInfoRemolderBuilder icon(ItemStack icon) {
			this.icon = Optional.of(icon);
			return this;
		}

		/**
		 * Updates the {@link #background}.
		 *
		 * @param background A {@link ResourceLocation} instance to use as the background.
		 * @return This builder.
		 */
		public DisplayInfoRemolderBuilder background(@Nullable ResourceLocation background) {
			this.background = Optional.of(Optional.ofNullable(background));
			return this;
		}

		/**
		 * Updates the {@link #type}.
		 *
		 * @param type A {@link AdvancementType} value to use as the type.
		 * @return This builder.
		 */
		public DisplayInfoRemolderBuilder type(AdvancementType type) {
			this.type = Optional.of(type);
			return this;
		}

		/**
		 * Updates the {@link #showToast}.
		 *
		 * @param showToast If the advancement should show toast.
		 * @return This builder.
		 */
		public DisplayInfoRemolderBuilder showToast(boolean showToast) {
			this.showToast = Optional.of(showToast);
			return this;
		}

		/**
		 * Updates the {@link #announceToChat}.
		 *
		 * @param announceToChat If the advancement should announce to chat.
		 * @return This builder.
		 */
		public DisplayInfoRemolderBuilder announceToChat(boolean announceToChat) {
			this.announceToChat = Optional.of(announceToChat);
			return this;
		}

		/**
		 * Updates the {@link #hidden}.
		 *
		 * @param hidden If the advancement should be hidden.
		 * @return This builder.
		 */
		public DisplayInfoRemolderBuilder hidden(boolean hidden) {
			this.hidden = Optional.of(hidden);
			return this;
		}

		/**
		 * Builds a new {@link DisplayInfoRemolderBuilder} instance.
		 *
		 * @return A new {@link DisplayInfoRemolderBuilder} instance.
		 */
		public Remolder build() {
			var sequence = sequence();
			sequence.then(replace(target("$original_display"), parse("display")));
			sequence.then(remoldIf(parse("!#isMap($original_display)"),
					sequence(
							replace(target("$original_display"), value(DEFAULT_DISPLAY, DisplayInfo.CODEC)),
							replace(target("display"), parse("$original_display"))
					)
			));
			sequence.then(replace(target("$display"), parse("#map($original_display)")));
			this.icon.ifPresent(itemStack -> {
				sequence.then(replace(target("$display[\"icon\"]"), value(itemStack, ItemStack.STRICT_CODEC)));
			});
			this.title.ifPresent(component -> {
				sequence.then(replace(target("$display[\"title\"]"), value(component, ComponentSerialization.CODEC)));
			});
			this.description.ifPresent(component -> {
				sequence.then(replace(target("$display[\"description\"]"), value(component, ComponentSerialization.CODEC)));
			});
			this.background.ifPresent(background -> {
				sequence.then(replace(target("$display[\"background\"]"), background.isEmpty() ? value(DynamicOps::empty) : value(background.get(), ResourceLocation.CODEC)));
			});
			this.type.ifPresent(type -> {
				sequence.then(replace(target("$display[\"frame\"]"), value(type, AdvancementType.CODEC)));
			});
			this.showToast.ifPresent(showToast -> {
				sequence.then(replace(target("$display[\"show_toast\"]"), value(showToast, Codec.BOOL)));
			});
			this.announceToChat.ifPresent(announceToChat -> {
				sequence.then(replace(target("$display[\"announce_to_chat\"]"), value(announceToChat, Codec.BOOL)));
			});
			this.hidden.ifPresent(hidden -> {
				sequence.then(replace(target("$display[\"hidden\"]"), value(hidden, Codec.BOOL)));
			});
			return sequence.complete();
		}
	}
}
