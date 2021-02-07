package com.minecraftabnormals.abnormals_core.core.config;

import com.minecraftabnormals.abnormals_core.core.annotations.ConfigKey;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Predicate;

/**
 * @author SmellyModder(Luke Tonon)
 */
public final class ACConfig {

	public static class Common {
		@ConfigKey("quark_sign_editing_enabled")
		public final ConfigValue<Boolean> enableQuarkSignEditing;
		@ConfigKey("sign_editing_requires_empty_hand")
		public final ConfigValue<Boolean> signEditingRequiresEmptyHand;
		@ConfigKey("quark_poison_potato_compat_enabled")
		public final ConfigValue<Boolean> poisonPotatoCompatEnabled;
		@ConfigKey("potato_poison_effect")
		public final ConfigValue<Boolean> poisonEffect;
		@ConfigKey("potato_poison_chance")
		public final ConfigValue<Double> poisonChance;
		
		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("Common only settings for Abnormals Core, this will affect all depending mods")
			.push("common");
			
			this.enableQuarkSignEditing = builder
				.comment("If Quark Sign Editing should be enabled; Default: True")
				.translation(makeTranslation("quark_sign_editing"))
				.define("quarkSignEditing", true);
			
			this.signEditingRequiresEmptyHand = builder
				.comment("If Quark Sign Editing requires an empty hand to edit; Default: False")
				.translation(makeTranslation("require_empty_hand"))
				.define("signEditingRequiresEmptyHand", false);

			builder.comment("Compatibility with Quark's poisonous potatoes feature")
			.push("poisonousPotatoCompat");
			this.poisonPotatoCompatEnabled = builder
					.comment("If baby mobs can be fed a poisonous potato to stunt their growth when Quark is installed; Default: True")
					.translation(makeTranslation("poison_potato_compat_enabled"))
					.define("poisonPotatoCompatEnabled",true);

			this.poisonEffect = builder
					.comment("If growth stunting should give baby mobs poison; Default: True")
					.translation(makeTranslation("poison_effect"))
					.define("poisonEffect",true);

			this.poisonChance = builder
					.comment("The chance to stunt baby mob growth when feeding a poisonous potato; Default: 0.1")
					.translation(makeTranslation("poison_chance"))
					.defineInRange("poisonChance",0.1,0,1);

			builder.pop();
			builder.pop();
		}
	}

	public static final class Client {
		public final SlabfishSettings slabfishSettings;

		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("Client only settings for Abnormals Core.")
			.push("client");

			this.slabfishSettings = new SlabfishSettings(builder);

			builder.pop();
		}
	}

	public static final class SlabfishSettings {
		@ConfigKey("slabfish_hat_enabled")
		public final ConfigValue<Boolean> enabled;
		@ConfigKey("slabfish_hat_backpack_enabled")
		public final ConfigValue<Boolean> backpackEnabled;
		@ConfigKey("slabfish_hat_sweater_enabled")
		public final ConfigValue<Boolean> sweaterEnabled;
		@ConfigKey("slabfish_hat_custom_type_enabled")
		public final ConfigValue<Boolean> typeEnabled;

		SlabfishSettings(ForgeConfigSpec.Builder builder) {
			builder.comment("Slabfish patron hat settings.")
			.push("slabfishSettings");

			this.enabled = builder
					.comment("If the slabfish hat should be enabled; Default: True")
					.translation(makeTranslation("slabfishHat"))
					.define("enabled", true);
			this.backpackEnabled = builder
					.comment("If the slabfish hat's backpack should be enabled; Default: True")
					.translation(makeTranslation("slabfishHat.backpack"))
					.define("backpackEnabled", true);
			this.sweaterEnabled = builder
					.comment("If the slabfish hat's sweater should be enabled; Default: True")
					.translation(makeTranslation("slabfishHat.sweater"))
					.define("sweaterEnabled", true);
			this.typeEnabled = builder
					.comment("If the slabfish hat's custom type should be enabled. If false, the default swamp slabfish appears; Default: True")
					.translation(makeTranslation("slabfishHat.type"))
					.define("typeEnabled", true);

			builder.pop();
		}
	}
	
	private static String makeTranslation(String name) {
		return "abnormals_core.config." + name;
	}
	
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;

	static {
		final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = commonSpecPair.getRight();
		COMMON = commonSpecPair.getLeft();

		final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = clientSpecPair.getRight();
		CLIENT = clientSpecPair.getLeft();
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