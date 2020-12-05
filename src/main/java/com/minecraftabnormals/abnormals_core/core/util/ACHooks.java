package com.minecraftabnormals.abnormals_core.core.util;

import com.minecraftabnormals.abnormals_core.client.screen.SlabfishHatScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Used to call methods via ASM.
 */
public final class ACHooks {
	private static final Method ADD_BUTTON_METHOD = ObfuscationReflectionHelper.findMethod(Screen.class, "func_230480_a_", Widget.class);

	/**
	 * Called via ASM - customization_screen.js Transformer
	 */
	public static void addSlabfishButton(CustomizeSkinScreen screen, int i) {
		Minecraft minecraft = screen.getMinecraft();
		try {
			ADD_BUTTON_METHOD.invoke(screen, new Button(screen.width / 2 - 155 + i % 2 * 160, screen.height / 6 + 24 * (i >> 1), 150, 20, new TranslationTextComponent(SlabfishHatScreen.SLABFISH_SCREEN_KEY), (button) -> {
				minecraft.displayGuiScreen(new SlabfishHatScreen(screen));
			}, (button, stack, mouseX, mouseY) -> screen.renderTooltip(stack, minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent(SlabfishHatScreen.SLABFISH_SCREEN_KEY + ".tooltip", new StringTextComponent("patreon.com/teamabnormals").modifyStyle(style -> style.setColor(Color.fromHex("#FF424D")))), 200), mouseX, mouseY)));
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
