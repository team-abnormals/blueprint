package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import com.teamabnormals.blueprint.common.advancement.modification.BlueprintAdvancementBuilder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * An {@link AdvancementModifier} implementation that modifies the {@link DisplayInfo} of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record DisplayInfoModifier(boolean replaces, Optional<ItemStack> icon, Optional<Component> title, Optional<Component> description, Optional<ResourceLocation> background, Optional<AdvancementType> type, Optional<Boolean> showToast, Optional<Boolean> announceToChat,
								  Optional<Boolean> hidden) implements AdvancementModifier<DisplayInfoModifier> {
	/**
	 * Creates a new {@link Builder} instance to simplify creation of {@link DisplayInfoModifier} instances.
	 *
	 * @return A new {@link Builder} instance.
	 */
	public static Builder builder() {
		return new Builder();
	}

	@Override
	public void modify(BlueprintAdvancementBuilder builder) {
		var display = builder.display;
		if (display.isPresent() && !this.replaces) {
			DisplayInfo displayInfo = display.get();
			Component title = this.title.orElse(displayInfo.getTitle());
			Component description = this.description.orElse(displayInfo.getDescription());
			ItemStack icon = this.icon.orElse(displayInfo.getIcon());
			var prevBackground = displayInfo.getBackground();
			Optional<ResourceLocation> background = prevBackground.isPresent() ? Optional.of(this.background.orElse(prevBackground.get())) : this.background;
			AdvancementType advancementType = this.type.orElse(displayInfo.getType());
			boolean showToast = this.showToast.orElse(displayInfo.shouldShowToast());
			boolean announceToChat = this.announceToChat.orElse(displayInfo.shouldAnnounceChat());
			boolean hidden = this.hidden.orElse(displayInfo.isHidden());
			builder.display(new DisplayInfo(icon, title, description, background, advancementType, showToast, announceToChat, hidden));
		} else {
			builder.display(new DisplayInfo(this.icon.orElse(ItemStack.EMPTY), this.title.orElse(Component.empty()), this.description.orElse(Component.empty()), this.background, this.type.orElse(AdvancementType.TASK), this.showToast.orElse(true), this.announceToChat.orElse(true), this.hidden.orElse(false)));
		}
	}

	@Override
	public Serializer getSerializer() {
		return AdvancementModifierSerializers.DISPLAY_INFO;
	}

	public static final class Serializer implements AdvancementModifier.Serializer<DisplayInfoModifier> {
		private static final Codec<DisplayInfoModifier> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
								Codec.BOOL.optionalFieldOf("replaces", false).forGetter(DisplayInfoModifier::replaces),
								ItemStack.STRICT_CODEC.optionalFieldOf("icon").forGetter(DisplayInfoModifier::icon),
								ComponentSerialization.CODEC.optionalFieldOf("title").forGetter(DisplayInfoModifier::title),
								ComponentSerialization.CODEC.optionalFieldOf("description").forGetter(DisplayInfoModifier::description),
								ResourceLocation.CODEC.optionalFieldOf("background").forGetter(DisplayInfoModifier::background),
								AdvancementType.CODEC.optionalFieldOf("frame").forGetter(DisplayInfoModifier::type),
								Codec.BOOL.optionalFieldOf("show_toast").forGetter(DisplayInfoModifier::showToast),
								Codec.BOOL.optionalFieldOf("announce_to_chat").forGetter(DisplayInfoModifier::announceToChat),
								Codec.BOOL.optionalFieldOf("hidden").forGetter(DisplayInfoModifier::hidden)
						)
						.apply(instance, DisplayInfoModifier::new)
		);

		@Override
		public JsonElement serialize(DisplayInfoModifier modifier, RegistryOps<JsonElement> ops) throws JsonParseException {
			var result = CODEC.encodeStart(ops, modifier);
			var error = result.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			return result.result().get();
		}

		@Override
		public DisplayInfoModifier deserialize(JsonElement element, RegistryOps<JsonElement> ops) throws JsonParseException {
			var result = CODEC.decode(ops, element);
			var error = result.error();
			if (error.isPresent()) throw new JsonParseException(error.get().message());
			return result.result().get().getFirst();
		}
	}

	/**
	 * The builder class for simpler creation of {@link DisplayInfoModifier} instances.
	 * <p>Use {@link DisplayInfoModifier#builder()} to create new instances of this class.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Builder {
		private boolean replace;
		private Optional<Component> title = Optional.empty();
		private Optional<Component> description = Optional.empty();
		private Optional<ItemStack> icon = Optional.empty();
		private Optional<ResourceLocation> background = Optional.empty();
		private Optional<AdvancementType> type = Optional.empty();
		private Optional<Boolean> showToast = Optional.empty();
		private Optional<Boolean> announceToChat = Optional.empty();
		private Optional<Boolean> hidden = Optional.empty();

		private Builder() {
		}

		/**
		 * Updates the {@link #replaces}.
		 *
		 * @param replace Whether to replace during the modification.
		 * @return This builder.
		 */
		public Builder replace(boolean replace) {
			this.replace = replace;
			return this;
		}

		/**
		 * Updates the {@link #title}.
		 *
		 * @param title A {@link Component} instance to use as the title.
		 * @return This builder.
		 */
		public Builder title(Component title) {
			this.title = Optional.of(title);
			return this;
		}

		/**
		 * Updates the {@link #description}.
		 *
		 * @param description A {@link Component} instance to use as the description.
		 * @return This builder.
		 */
		public Builder description(Component description) {
			this.description = Optional.of(description);
			return this;
		}

		/**
		 * Updates the {@link #icon}.
		 *
		 * @param icon A {@link ItemStack} instance to use as the icon.
		 * @return This builder.
		 */
		public Builder icon(ItemStack icon) {
			this.icon = Optional.of(icon);
			return this;
		}

		/**
		 * Updates the {@link #background}.
		 *
		 * @param background A {@link ResourceLocation} instance to use as the background.
		 * @return This builder.
		 */
		public Builder background(ResourceLocation background) {
			this.background = Optional.of(background);
			return this;
		}

		/**
		 * Updates the {@link #type}.
		 *
		 * @param type A {@link AdvancementType} value to use as the type.
		 * @return This builder.
		 */
		public Builder type(AdvancementType type) {
			this.type = Optional.of(type);
			return this;
		}

		/**
		 * Updates the {@link #showToast}.
		 *
		 * @param showToast If the advancement should show toast.
		 * @return This builder.
		 */
		public Builder showToast(boolean showToast) {
			this.showToast = Optional.of(showToast);
			return this;
		}

		/**
		 * Updates the {@link #announceToChat}.
		 *
		 * @param announceToChat If the advancement should announce to chat.
		 * @return This builder.
		 */
		public Builder announceToChat(boolean announceToChat) {
			this.announceToChat = Optional.of(announceToChat);
			return this;
		}

		/**
		 * Updates the {@link #hidden}.
		 *
		 * @param hidden If the advancement should be hidden.
		 * @return This builder.
		 */
		public Builder hidden(boolean hidden) {
			this.hidden = Optional.of(hidden);
			return this;
		}

		/**
		 * Builds a new {@link DisplayInfoModifier} instance.
		 *
		 * @return A new {@link DisplayInfoModifier} instance.
		 */
		public DisplayInfoModifier build() {
			return new DisplayInfoModifier(this.replace, this.icon, this.title, this.description, this.background, this.type, this.showToast, this.announceToChat, this.hidden);
		}
	}
}
