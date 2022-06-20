package com.teamabnormals.blueprint.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamabnormals.blueprint.client.RewardHandler;
import com.teamabnormals.blueprint.client.RewardHandler.SlabfishSetting;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Locale;

/**
 * The {@link Screen} responsible for the slabfish patreon settings.
 *
 * <p>For more information, visit the <a href="https://www.patreon.com/teamabnormals">Patreon</a>></p>
 *
 * @author Jackson
 */
public class SlabfishHatScreen extends Screen {
	public static final String SLABFISH_SCREEN_KEY = "blueprint.screen.slabfish_settings";
	private final Screen parent;

	public SlabfishHatScreen(Screen parent) {
		super(Component.translatable(SLABFISH_SCREEN_KEY + ".title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int i = 0;

		for (SlabfishSetting setting : RewardHandler.SlabfishSetting.values()) {
			ForgeConfigSpec.ConfigValue<Boolean> configValue = setting.getConfigValue();

			this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, this.getOptionName(setting, configValue.get()), (button) -> {
				boolean enabled = !configValue.get();
				configValue.set(enabled);
				button.setMessage(this.getOptionName(setting, enabled));
				NetworkUtil.updateSlabfish(RewardHandler.SlabfishSetting.getConfig());
			}, (button, stack, mouseX, mouseY) -> this.renderTooltip(stack, this.font.split(Component.translatable("blueprint.config.slabfish_hat." + setting.name().toLowerCase(Locale.ROOT) + ".tooltip"), 200), mouseX, mouseY)));
			++i;
		}

		if (i % 2 == 1) {
			++i;
		}

		this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, CommonComponents.GUI_DONE, (button) -> this.getMinecraft().setScreen(this.parent)));
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(stack);
		drawCenteredString(stack, this.font, this.title, this.width / 2, 20, 16777215);
		super.render(stack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void onClose() {
		this.getMinecraft().setScreen(this.parent);
	}

	private Component getOptionName(RewardHandler.SlabfishSetting setting, boolean enabled) {
		return CommonComponents.optionStatus(Component.translatable("blueprint.config.slabfish_hat." + setting.name().toLowerCase(Locale.ROOT)), enabled);
	}
}
