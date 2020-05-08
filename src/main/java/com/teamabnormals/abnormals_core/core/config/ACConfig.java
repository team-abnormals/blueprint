package com.teamabnormals.abnormals_core.core.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;

/**
 * @author SmellyModder(Luke Tonon)
 */
public class ACConfig {

	public static class Common {
		public final ConfigValue<Boolean> enableQuarkSignEditing;
		public final ConfigValue<Boolean> signEditingRequiresEmptyHand;
		
		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("Common only settings for Abnormals Core, this will affect all depending mods")
			.push("common");
			
			enableQuarkSignEditing = builder
				.comment("If Quark Sign Editing should be enabled; Default: True")
				.translation(makeTranslation("quark_sign_editing"))
				.define("quarkSignEditing", true);
			
			signEditingRequiresEmptyHand = builder
				.comment("If Quark Sign Editing requires an empty hand to edit; Default: False")
				.translation(makeTranslation("require_empty_hand"))
				.define("signEditingRequiresEmptyHand", false);
			
			builder.pop();
		}
	}
	
	private static String makeTranslation(String name) {
		return "abnormals_core.config." + name;
	}
	
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;
	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}
	
	public static class ValuesHolder {
		private static boolean quarkSignEditing;
		private static boolean signEditingRequireEmptyHand;
		
		public static void updateCommonValuesFromConfig(ModConfig config) {
			quarkSignEditing = ACConfig.COMMON.enableQuarkSignEditing.get();
			signEditingRequireEmptyHand = ACConfig.COMMON.signEditingRequiresEmptyHand.get();
		}
		
		public static boolean isQuarkSignEditingEnabled() {
			return !ModList.get().isLoaded("quark") && quarkSignEditing;
		}
		
		public static boolean doesSignEditingRequireEmptyHand() {
			return signEditingRequireEmptyHand;
		}
	}
 
}