package com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers;

import com.minecraftabnormals.abnormals_core.common.advancement.modification.AdvancementModifier;
import com.minecraftabnormals.abnormals_core.common.codec.text.ITextComponentCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An {@link AdvancementModifier} extension that modifies the {@link DisplayInfo} of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class DisplayInfoModifier extends AdvancementModifier<DisplayInfoModifier.Config> {
	@SuppressWarnings("deprecation")
	private static final Codec<ItemStack> ICON_CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				Registry.ITEM.fieldOf("item").forGetter(ItemStack::getItem),
				CompoundNBT.CODEC.optionalFieldOf("tag").forGetter((stack) -> Optional.ofNullable(stack.getTag()))
		).apply(instance, (item, tag) -> {
			ItemStack stack = new ItemStack(item, 1, null);
			tag.ifPresent(stack::setTag);
			return stack;
		});
	});
	private static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Mode.CODEC.fieldOf("mode").forGetter(config -> config.mode),
				ITextComponentCodec.INSTANCE.optionalFieldOf("title").forGetter(config -> config.title),
				ITextComponentCodec.INSTANCE.optionalFieldOf("description").forGetter(config -> config.description),
				ICON_CODEC.optionalFieldOf("icon").forGetter(config -> config.icon),
				ResourceLocation.CODEC.optionalFieldOf("background").forGetter(config -> config.background),
				Config.Frame.CODEC.optionalFieldOf("frame").forGetter(config -> config.frame),
				Codec.BOOL.optionalFieldOf("show_toast").forGetter(config -> config.showToast),
				Codec.BOOL.optionalFieldOf("announce_to_chat").forGetter(config -> config.announceToChat),
				Codec.BOOL.optionalFieldOf("hidden").forGetter(config -> config.hidden)
		).apply(instance, Config::new);
	});
	private static final Field DISPLAY_INFO_FIELD = ObfuscationReflectionHelper.findField(Advancement.Builder.class, "field_192063_c");
	private static final Field ICON_FIELD = ObfuscationReflectionHelper.findField(DisplayInfo.class, "field_192301_b");
	private static final Field BACKGROUND_FIELD = ObfuscationReflectionHelper.findField(DisplayInfo.class, "field_192302_c");
	private static final Field SHOW_TOAST_FIELD = ObfuscationReflectionHelper.findField(DisplayInfo.class, "field_193226_f");

	public DisplayInfoModifier() {
		super(CODEC);
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
				FrameType frameType = displayInfo.getFrame();
				if (config.frame.isPresent()) {
					frameType = config.frame.get().frameType;
				}
				boolean showToast = config.showToast.orElse(SHOW_TOAST_FIELD.getBoolean(displayInfo));
				boolean announceToChat = config.announceToChat.orElse(displayInfo.shouldAnnounceToChat());
				boolean hidden = config.hidden.orElse(displayInfo.isHidden());
				builder.withDisplay(new DisplayInfo(icon, title, description, background, frameType, showToast, announceToChat, hidden));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			builder.withDisplay(new DisplayInfo(config.icon.orElse(ItemStack.EMPTY), config.title.orElse(StringTextComponent.EMPTY), config.description.orElse(StringTextComponent.EMPTY), config.background.orElse(null), config.frame.orElse(Config.Frame.TASK).frameType, config.showToast.orElse(true), config.announceToChat.orElse(true), config.hidden.orElse(false)));
		}
	}

	static class Config {
		private final Mode mode;
		private final Optional<ITextComponent> title;
		private final Optional<ITextComponent> description;
		private final Optional<ItemStack> icon;
		private final Optional<ResourceLocation> background;
		private final Optional<Frame> frame;
		private final Optional<Boolean> showToast;
		private final Optional<Boolean> announceToChat;
		private final Optional<Boolean> hidden;

		Config(Mode mode, Optional<ITextComponent> title, Optional<ITextComponent> description, Optional<ItemStack> icon, Optional<ResourceLocation> background, Optional<Frame> frame, Optional<Boolean> showToast, Optional<Boolean> announceToChat, Optional<Boolean> hidden) {
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

		enum Frame implements IStringSerializable {
			TASK("task", FrameType.TASK),
			CHALLENGE("challenge", FrameType.CHALLENGE),
			GOAL("goal", FrameType.GOAL);

			private static final Map<String, Frame> VALUES_MAP = Arrays.stream(values()).collect(Collectors.toMap(Frame::getName, (frameType) -> {
				return frameType;
			}));
			private static final Codec<Frame> CODEC = IStringSerializable.createEnumCodec(Frame::values, Frame::getFrameTypeByName);
			private final String name;
			private final FrameType frameType;

			Frame(String name, FrameType frameType) {
				this.name = name;
				this.frameType = frameType;
			}

			public static Frame getFrameTypeByName(String name) {
				return VALUES_MAP.get(name);
			}

			public String getName() {
				return this.name;
			}

			public FrameType getFrameType() {
				return this.frameType;
			}

			@Override
			public String getString() {
				return this.name;
			}
		}
	}
}
