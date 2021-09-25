package com.minecraftabnormals.abnormals_core.core.mixin.client;

import com.minecraftabnormals.abnormals_core.client.screen.SlabfishHatScreen;
import net.minecraft.client.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SkinCustomizationScreen.class)
public final class CustomizeSkinScreenMixin extends OptionsSubScreen {

	private CustomizeSkinScreenMixin(Screen previousScreen, Options gameSettingsObj, Component textComponent) {
		super(previousScreen, gameSettingsObj, textComponent);
	}

	@ModifyVariable(method = "init", ordinal = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/CustomizeSkinScreen;addButton(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 1, shift = At.Shift.AFTER))
	public int init(int i) {
		Minecraft minecraft = this.getMinecraft();
		++i;
		this.addButton(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent(SlabfishHatScreen.SLABFISH_SCREEN_KEY), (button) -> minecraft.setScreen(new SlabfishHatScreen(this)), (button, stack, mouseX, mouseY) -> this.renderTooltip(stack, minecraft.font.split(new TranslatableComponent(SlabfishHatScreen.SLABFISH_SCREEN_KEY + ".tooltip", new TextComponent("patreon.com/teamabnormals").withStyle(style -> style.withColor(TextColor.parseColor("#FF424D")))), 200), mouseX, mouseY)));
		return i;
	}

}
