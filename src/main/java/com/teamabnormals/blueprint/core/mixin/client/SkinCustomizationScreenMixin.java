package com.teamabnormals.blueprint.core.mixin.client;

import com.teamabnormals.blueprint.client.screen.SlabfishHatScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(SkinCustomizationScreen.class)
public abstract class SkinCustomizationScreenMixin extends OptionsSubScreen {

	private SkinCustomizationScreenMixin(Screen previousScreen, Options gameSettingsObj, Component textComponent) {
		super(previousScreen, gameSettingsObj, textComponent);
	}

	@Inject(method = "addOptions", at = @At(value = "RETURN", shift = At.Shift.BY, by = -3), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void addSlabfishOptions(CallbackInfo info, List<AbstractWidget> list) {
		Minecraft minecraft = this.getMinecraft();
		Button slabfishScreenButton = Button.builder(Component.translatable(SlabfishHatScreen.SLABFISH_SCREEN_KEY), (button) -> minecraft.setScreen(new SlabfishHatScreen(this)))
				.bounds(0, 0, 150, 20)
				.tooltip(Tooltip.create(Component.translatable(SlabfishHatScreen.SLABFISH_SCREEN_KEY + ".tooltip", Component.literal("patreon.com/teamabnormals").withStyle(style -> style.withColor(TextColor.fromRgb(16728653))))))
				.build();
		list.add(slabfishScreenButton);
	}

}
