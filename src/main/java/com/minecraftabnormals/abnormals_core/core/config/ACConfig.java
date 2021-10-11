package com.minecraftabnormals.abnormals_core.core.config;

import com.minecraftabnormals.abnormals_core.core.annotations.ConfigKey;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Class for storing all the information about the Abnormals Core config.
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class ACConfig {

	/**
	 * Class that stores all the information about the Abnormals Core common config.
	 */
	public static class Common {
		@ConfigKey("quark_poison_potato_compat_enabled")
		public final ConfigValue<Boolean> poisonPotatoCompatEnabledValue;
		public boolean poisonPotatoCompatEnabled;
		@ConfigKey("potato_poison_effect")
		public final ConfigValue<Boolean> poisonEffectValue;
		public boolean poisonEffect;
		@ConfigKey("potato_poison_chance")
		public final ConfigValue<Double> poisonChanceValue;
		public double poisonChance;
		
		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("Common only settings for Abnormals Core, this will affect all depending mods")
			.push("common");

			builder.comment("Compatibility with Quark's poisonous potatoes feature")
			.push("poisonousPotatoCompat");
			this.poisonPotatoCompatEnabledValue = builder
					.comment("If baby mobs can be fed a poisonous potato to stunt their growth when Quark is installed; Default: True")
					.translation(makeTranslation("poison_potato_compat_enabled"))
					.define("poisonPotatoCompatEnabled", true);

			this.poisonEffectValue = builder
					.comment("If growth stunting should give baby mobs poison; Default: True")
					.translation(makeTranslation("poison_effect"))
					.define("poisonEffect", true);

			this.poisonChanceValue = builder
					.comment("The chance to stunt baby mob growth when feeding a poisonous potato; Default: 0.1")
					.translation(makeTranslation("poison_chance"))
					.defineInRange("poisonChance", 0.1, 0, 1);

			builder.pop();
			builder.pop();
		}

		/**
		 * Caches the common config values.
		 */
		public void load() {
			this.poisonPotatoCompatEnabled = this.poisonPotatoCompatEnabledValue.get();
			this.poisonEffect = this.poisonEffectValue.get();
			this.poisonChance = this.poisonChanceValue.get();
		}
	}

	/**
	 * Class that stores all the information about the Abnormals Core client config.
	 */
	public static final class Client {
		@ConfigKey("screen_shake_scale")
		public final ConfigValue<Double> screenShakeScaleValue;
		public double screenShakeScale;
		@ConfigKey("max_screen_shakers")
		public final ConfigValue<Integer> maxScreenShakersValue;
		public int maxScreenShakers;

		public final SlabfishSettings slabfishSettings;

		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("Client only settings for Abnormals Core.")
			.push("client");

			this.screenShakeScaleValue = builder
					.comment("Scale for screen shake effects; Default: 1.0")
					.translation(makeTranslation("screen_shake_scale"))
					.defineInRange("screenShakeScale", 1.0D, 0.0D, 1.0D);

			this.maxScreenShakersValue = builder
					.comment("Max amount of sources that can contribute to screen shaking, adjustable for performance and preference reasons. 0 will disable the addition of shake sources; Default: 256")
					.translation(makeTranslation("max_screen_shakers"))
					.defineInRange("maxScreenShakers", 256, 0, Integer.MAX_VALUE);

			this.slabfishSettings = new SlabfishSettings(builder);

			builder.pop();
		}

		/**
		 * Caches some client config values.
		 */
		public void load() {
			this.screenShakeScale = this.screenShakeScaleValue.get();
			this.maxScreenShakers = this.maxScreenShakersValue.get();
		}
	}

	/**
	 * Class that stores all the information about the Slabfish Patreon Hat settings.
	 */
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

}