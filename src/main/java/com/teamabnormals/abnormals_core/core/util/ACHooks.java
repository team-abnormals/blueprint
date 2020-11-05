package com.teamabnormals.abnormals_core.core.util;

import com.teamabnormals.abnormals_core.core.config.ACConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ACHooks {

	public static void addSlabfishButton(CustomizeSkinScreen screen, int i) {
		Method addButton = ObfuscationReflectionHelper.findMethod(Screen.class, "func_230480_a_", Widget.class);
		try {
			addButton.invoke(screen, new Button(screen.width / 2 - 155 + i % 2 * 160, screen.height / 6 + 24 * (i >> 1), 150, 20, getSlabfishButtonName(), (button) -> {
				ACConfig.CLIENT.slabfishHat.set(!ACConfig.CLIENT.slabfishHat.get());
				button.setMessage(getSlabfishButtonName());
				NetworkUtil.updateSlabfish(ACConfig.CLIENT.slabfishHat.get());
			}, (button, stack, mouseX, mouseY) -> screen.renderTooltip(stack, Minecraft.getInstance().fontRenderer.trimStringToWidth(new TranslationTextComponent("abnormals_core.config.slabfish_hat.tooltip"), 200), mouseX, mouseY)));
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private static ITextComponent getSlabfishButtonName() {
		return DialogTexts.getComposedOptionMessage(new TranslationTextComponent("abnormals_core.config.slabfish_hat"), ACConfig.CLIENT.slabfishHat.get());
	}
}
