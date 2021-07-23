package com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.common.advancement.modification.AdvancementModifier;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Optional;

import com.minecraftabnormals.abnormals_core.common.advancement.modification.AdvancementModifier.Mode;

/**
 * An {@link AdvancementModifier} extension that modifies the {@link DisplayInfo} of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class DisplayInfoModifier extends AdvancementModifier<DisplayInfoModifier.Config> {
	private static final Field DISPLAY_INFO_FIELD = ObfuscationReflectionHelper.findField(Advancement.Builder.class, "field_192063_c");
	private static final Field ICON_FIELD = ObfuscationReflectionHelper.findField(DisplayInfo.class, "field_192301_b");
	private static final Field BACKGROUND_FIELD = ObfuscationReflectionHelper.findField(DisplayInfo.class, "field_192302_c");
	private static final Field SHOW_TOAST_FIELD = ObfuscationReflectionHelper.findField(DisplayInfo.class, "field_193226_f");

	public DisplayInfoModifier() {
		super(((element, conditionArrayParser) -> {
			JsonObject object = element.getAsJsonObject();
			Mode mode = Mode.deserialize(object);
			Optional<ITextComponent> title = JSONUtils.isValidNode(object, "title") ? Optional.ofNullable(ITextComponent.Serializer.fromJson(object.get("title"))) : Optional.empty();
			Optional<ITextComponent> description = JSONUtils.isValidNode(object, "description") ? Optional.ofNullable(ITextComponent.Serializer.fromJson(object.get("description"))) : Optional.empty();
			Optional<ItemStack> icon = JSONUtils.isValidNode(object, "icon") ? Optional.of(deserializeIcon(JSONUtils.getAsJsonObject(object, "icon"))) : Optional.empty();
			Optional<ResourceLocation> background = JSONUtils.isValidNode(object, "background") ? Optional.of(new ResourceLocation(JSONUtils.getAsString(object, "background"))) : Optional.empty();
			Optional<FrameType> frameType = JSONUtils.isValidNode(object, "frame") ? Optional.of(FrameType.byName(JSONUtils.getAsString(object, "frame"))) : Optional.empty();
			Optional<Boolean> showToast = JSONUtils.isValidNode(object, "show_toast") ? Optional.of(JSONUtils.getAsBoolean(object, "show_toast")) : Optional.empty();
			Optional<Boolean> announceToChat = JSONUtils.isValidNode(object, "announce_to_chat") ? Optional.of(JSONUtils.getAsBoolean(object, "announce_to_chat")) : Optional.empty();
			Optional<Boolean> hidden = JSONUtils.isValidNode(object, "hidden") ? Optional.of(JSONUtils.getAsBoolean(object, "hidden")) : Optional.empty();
			return new Config(mode, title, description, icon, background, frameType, showToast, announceToChat, hidden);
		}));
	}

	private static ItemStack deserializeIcon(JsonObject object) {
		if (!object.has("item")) {
			throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
		} else {
			Item item = JSONUtils.getAsItem(object, "item");
			if (object.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				ItemStack stack = new ItemStack(item);
				if (object.has("nbt")) {
					try {
						CompoundNBT nbt = JsonToNBT.parseTag(JSONUtils.convertToString(object.get("nbt"), "nbt"));
						stack.setTag(nbt);
					} catch (CommandSyntaxException syntax) {
						throw new JsonSyntaxException("Invalid nbt tag: " + syntax.getMessage());
					}
				}
				return stack;
			}
		}
	}

	@Override
	public void modify(Advancement.Builder builder, Config config) {
		if (config.mode == Mode.MODIFY) {
			try {
				DisplayInfo displayInfo = (DisplayInfo) DISPLAY_INFO_FIELD.get(builder);
				ITextComponent title = config.title.orElse(displayInfo.getTitle());
				ITextComponent description = config.description.orElse(displayInfo.getDescription());
				ItemStack icon = config.icon.orElse((ItemStack) ICON_FIELD.get(displayInfo));
				ResourceLocation background = config.background.orElse((ResourceLocation) BACKGROUND_FIELD.get(displayInfo));
				FrameType frameType = config.frame.orElse(displayInfo.getFrame());
				boolean showToast = config.showToast.orElse(SHOW_TOAST_FIELD.getBoolean(displayInfo));
				boolean announceToChat = config.announceToChat.orElse(displayInfo.shouldAnnounceChat());
				boolean hidden = config.hidden.orElse(displayInfo.isHidden());
				builder.display(new DisplayInfo(icon, title, description, background, frameType, showToast, announceToChat, hidden));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			builder.display(new DisplayInfo(config.icon.orElse(ItemStack.EMPTY), config.title.orElse(StringTextComponent.EMPTY), config.description.orElse(StringTextComponent.EMPTY), config.background.orElse(null), config.frame.orElse(FrameType.TASK), config.showToast.orElse(true), config.announceToChat.orElse(true), config.hidden.orElse(false)));
		}
	}

	static class Config {
		private final Mode mode;
		private final Optional<ITextComponent> title;
		private final Optional<ITextComponent> description;
		private final Optional<ItemStack> icon;
		private final Optional<ResourceLocation> background;
		private final Optional<FrameType> frame;
		private final Optional<Boolean> showToast;
		private final Optional<Boolean> announceToChat;
		private final Optional<Boolean> hidden;

		Config(Mode mode, Optional<ITextComponent> title, Optional<ITextComponent> description, Optional<ItemStack> icon, Optional<ResourceLocation> background, Optional<FrameType> frame, Optional<Boolean> showToast, Optional<Boolean> announceToChat, Optional<Boolean> hidden) {
			this.mode = mode;
			this.title = title;
			this.description = description;
			this.icon = icon;
			this.background = background;
			this.frame = frame;
			this.showToast = showToast;
			this.announceToChat = announceToChat;
			this.hidden = hidden;
		}
	}
}
