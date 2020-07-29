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
		public final ConfigValue<Boolean> poisonPotatoCompatEnabled;
		public final ConfigValue<Boolean> poisonEffect;
		public final ConfigValue<Double> poisonChance;
		
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

			builder.comment("Compatibility with Quark's poisonous potatoes feature")
			.push("poisonousPotatoCompat");
			poisonPotatoCompatEnabled = builder
					.comment("If booflos can be fed a poisonous potato to stunt their growth when Quark is installed; Default: True")
					.translation(makeTranslation("poison_potato_compat_enabled"))
					.define("poisonPotatoCompatEnabled",true);

			poisonEffect = builder
					.comment("If growth stunting should give a booflo poison; Default: True")
					.translation(makeTranslation("poison_effect"))
					.define("poisonEffect",true);

			poisonChance = builder
					.comment("The chance to stunt booflo growth when feeding a poisonous potato; Default: 0.1")
					.translation(makeTranslation("poison_chance"))
					.defineInRange("poisonChance",0.1,0,1);

			builder.pop();
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
		private static boolean poisonPotatoCompatEnabled;
		private static boolean poisonEffect;
		private static double poisonChance;

		public static void updateCommonValuesFromConfig(ModConfig config) {
			quarkSignEditing = ACConfig.COMMON.enableQuarkSignEditing.get();
			signEditingRequireEmptyHand = ACConfig.COMMON.signEditingRequiresEmptyHand.get();
			poisonPotatoCompatEnabled = ACConfig.COMMON.poisonPotatoCompatEnabled.get();
			poisonEffect = ACConfig.COMMON.poisonEffect.get();
			poisonChance = ACConfig.COMMON.poisonChance.get();
		}

		public static boolean isQuarkSignEditingEnabled() {
			return ModList.get().isLoaded("quark") && quarkSignEditing;
		}

		public static boolean doesSignEditingRequireEmptyHand() {
			return signEditingRequireEmptyHand;
		}

		public static boolean isPoisonPotatoCompatEnabled() {
			return poisonPotatoCompatEnabled;
		}

		public static boolean shouldPoisonEntity() {
			return poisonEffect;
		}

		public static double poisonEffectChance() {
			return poisonChance;
		}
	}
 
}