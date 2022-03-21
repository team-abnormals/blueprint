package com.teamabnormals.blueprint.common.advancement.modification.modifiers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.teamabnormals.blueprint.common.advancement.modification.AdvancementModifierSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * An {@link AdvancementModifier} implementation that modifies the {@link DisplayInfo} of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record DisplayInfoModifier(Mode mode, Optional<Component> title, Optional<Component> description, Optional<ItemStack> icon, Optional<ResourceLocation> background, Optional<FrameType> frame, Optional<Boolean> showToast, Optional<Boolean> announceToChat, Optional<Boolean> hidden) implements AdvancementModifier<DisplayInfoModifier> {
	private static final Field DISPLAY_INFO_FIELD = ObfuscationReflectionHelper.findField(Advancement.Builder.class, "f_138334_");
	private static final Field ICON_FIELD = ObfuscationReflectionHelper.findField(DisplayInfo.class, "f_14960_");
	private static final Field BACKGROUND_FIELD = ObfuscationReflectionHelper.findField(DisplayInfo.class, "f_14961_");
	private static final Field SHOW_TOAST_FIELD = ObfuscationReflectionHelper.findField(DisplayInfo.class, "f_14963_");

	private static ItemStack deserializeIcon(JsonObject object) {
		if (!object.has("item")) {
			throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
		} else {
			Item item = GsonHelper.getAsItem(object, "item");
			if (object.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				ItemStack stack = new ItemStack(item);
				if (object.has("nbt")) {
					try {
						CompoundTag nbt = TagParser.parseTag(GsonHelper.convertToString(object.get("nbt"), "nbt"));
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
	public void modify(Advancement.Builder builder) {
		if (this.mode == Mode.MODIFY) {
			try {
				DisplayInfo displayInfo = (DisplayInfo) DISPLAY_INFO_FIELD.get(builder);
				Component title = this.title.orElse(displayInfo.getTitle());
				Component description = this.description.orElse(displayInfo.getDescription());
				ItemStack icon = this.icon.orElse((ItemStack) ICON_FIELD.get(displayInfo));
				ResourceLocation background = this.background.orElse((ResourceLocation) BACKGROUND_FIELD.get(displayInfo));
				FrameType frameType = this.frame.orElse(displayInfo.getFrame());
				boolean showToast = this.showToast.orElse(SHOW_TOAST_FIELD.getBoolean(displayInfo));
				boolean announceToChat = this.announceToChat.orElse(displayInfo.shouldAnnounceChat());
				boolean hidden = this.hidden.orElse(displayInfo.isHidden());
				builder.display(new DisplayInfo(icon, title, description, background, frameType, showToast, announceToChat, hidden));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			builder.display(new DisplayInfo(this.icon.orElse(ItemStack.EMPTY), this.title.orElse(TextComponent.EMPTY), this.description.orElse(TextComponent.EMPTY), this.background.orElse(null), this.frame.orElse(FrameType.TASK), this.showToast.orElse(true), this.announceToChat.orElse(true), this.hidden.orElse(false)));
		}
	}

	@Override
	public Serializer getSerializer() {
		return AdvancementModifierSerializers.DISPLAY_INFO;
	}

	public static final class Serializer implements AdvancementModifier.Serializer<DisplayInfoModifier> {
		@Override
		public JsonElement serialize(DisplayInfoModifier modifier, Void additional) throws JsonParseException {
			JsonObject jsonObject = new JsonObject();
			modifier.mode.serialize(jsonObject);
			modifier.title.ifPresent(title -> jsonObject.add("title", Component.Serializer.toJsonTree(title)));
			modifier.description.ifPresent(description -> jsonObject.add("description", Component.Serializer.toJsonTree(description)));
			modifier.icon.ifPresent(icon -> {
				JsonObject iconObject = new JsonObject();
				iconObject.addProperty("item", Registry.ITEM.getKey(icon.getItem()).toString());
				if (icon.hasTag()) {
					iconObject.addProperty("nbt", icon.getTag().toString());
				}
				jsonObject.add("icon", iconObject);
			});
			modifier.background.ifPresent(background -> jsonObject.addProperty("background", background.toString()));
			modifier.frame.ifPresent(frame -> jsonObject.addProperty("frame", frame.getName()));
			modifier.showToast.ifPresent(showToast -> jsonObject.addProperty("show_toast", showToast));
			modifier.announceToChat.ifPresent(announceToChat -> jsonObject.addProperty("announce_to_chat", announceToChat));
			modifier.hidden.ifPresent(hidden -> jsonObject.addProperty("hidden", hidden));
			return jsonObject;
		}

		@Override
		public DisplayInfoModifier deserialize(JsonElement element, DeserializationContext additional) throws JsonParseException {
			JsonObject object = element.getAsJsonObject();
			Mode mode = Mode.deserialize(object);
			Optional<Component> title = GsonHelper.isValidNode(object, "title") ? Optional.ofNullable(Component.Serializer.fromJson(object.get("title"))) : Optional.empty();
			Optional<Component> description = GsonHelper.isValidNode(object, "description") ? Optional.ofNullable(Component.Serializer.fromJson(object.get("description"))) : Optional.empty();
			Optional<ItemStack> icon = GsonHelper.isValidNode(object, "icon") ? Optional.of(deserializeIcon(GsonHelper.getAsJsonObject(object, "icon"))) : Optional.empty();
			Optional<ResourceLocation> background = GsonHelper.isValidNode(object, "background") ? Optional.of(new ResourceLocation(GsonHelper.getAsString(object, "background"))) : Optional.empty();
			Optional<FrameType> frameType = GsonHelper.isValidNode(object, "frame") ? Optional.of(FrameType.byName(GsonHelper.getAsString(object, "frame"))) : Optional.empty();
			Optional<Boolean> showToast = GsonHelper.isValidNode(object, "show_toast") ? Optional.of(GsonHelper.getAsBoolean(object, "show_toast")) : Optional.empty();
			Optional<Boolean> announceToChat = GsonHelper.isValidNode(object, "announce_to_chat") ? Optional.of(GsonHelper.getAsBoolean(object, "announce_to_chat")) : Optional.empty();
			Optional<Boolean> hidden = GsonHelper.isValidNode(object, "hidden") ? Optional.of(GsonHelper.getAsBoolean(object, "hidden")) : Optional.empty();
			return new DisplayInfoModifier(mode, title, description, icon, background, frameType, showToast, announceToChat, hidden);
		}
	}
}
