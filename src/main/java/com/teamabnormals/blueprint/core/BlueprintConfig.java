package com.teamabnormals.blueprint.core;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.teamabnormals.blueprint.core.annotations.ConfigKey;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Class for storing all the information about the Blueprint config.
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class BlueprintConfig {

	/**
	 * Class that stores all the information about the Blueprint common config.
	 */
	public static final class Common {
		public final ModConfigSpec.ConfigValue<CommentedConfig> moddedBiomeSliceSizes;

		public Common(ModConfigSpec.Builder builder) {
			builder.comment(
					"The modded biome slice sizes for dimensions",
					"Blueprint's Modded Biome Slice System allows for datapacks and mods to add new biome areas to any dimension",
					"Changing the size values will affect the size of all modded biome areas in their respected dimension",
					"If a slice size isn't a positive integer, it will get ignored and the default slice size will get used instead"
			);
			CommentedConfig config = TomlFormat.newConfig();
			config.setComment("default", "If the slice size for a dimension isn't defined, this value will get used for that dimension");
			config.set("default", 8);
			config.setComment("minecraft:overworld", "For example, the overworld's slice size would be formatted like this");
			config.set("minecraft:overworld", 8);
			this.moddedBiomeSliceSizes = builder.define("modded_biome_slice_sizes", config);
		}
	}

	/**
	 * Class that stores all the information about the Blueprint client config.
	 */
	public static final class Client {
		@ConfigKey("screen_shake_scale")
		public final ModConfigSpec.ConfigValue<Double> screenShakeScaleValue;
		public double screenShakeScale;
		@ConfigKey("max_screen_shakers")
		public final ModConfigSpec.ConfigValue<Integer> maxScreenShakersValue;
		public int maxScreenShakers;
		@ConfigKey("disable_experimental_settings_screen")
		public final ModConfigSpec.ConfigValue<Boolean> disableExperimentalSettingsScreenValue;
		public boolean disableExperimentalSettingsScreen;
		@ConfigKey("disable_slabfish_hats")
		public final ModConfigSpec.ConfigValue<Boolean> disableSlabfishHatsValue;
		public boolean disableSlabfishHats;

		public final SlabfishSettings slabfishSettings;

		Client(ModConfigSpec.Builder builder) {
			this.screenShakeScaleValue = builder
					.comment("Scale for screen shake effects")
					.translation(makeTranslation("screen_shake_scale"))
					.defineInRange("screenShakeScale", 1.0D, 0.0D, 1.0D);

			this.maxScreenShakersValue = builder
					.comment("Max amount of sources that can contribute to screen shaking, adjustable for performance and preference reasons. 0 will disable the addition of shake sources")
					.translation(makeTranslation("max_screen_shakers"))
					.defineInRange("maxScreenShakers", 256, 0, Integer.MAX_VALUE);

			this.disableExperimentalSettingsScreenValue = builder
					.comment("Determines if the experimental settings screen should be disabled")
					.translation(makeTranslation("disable_experimental_settings_screen"))
					.define("disableExperimentalSettingsScreen", true);

			this.disableSlabfishHatsValue = builder
					.comment("If no Slabfish hats should render at all, including on other players")
					.translation(makeTranslation("disable_slabfish_hats"))
					.define("disableSlabfishHats", false);

			this.slabfishSettings = new SlabfishSettings(builder);
		}

		/**
		 * Caches some client config values.
		 */
		public void load() {
			this.screenShakeScale = this.screenShakeScaleValue.get();
			this.maxScreenShakers = this.maxScreenShakersValue.get();
			this.disableExperimentalSettingsScreen = this.disableExperimentalSettingsScreenValue.get();
			this.disableSlabfishHats = this.disableSlabfishHatsValue.get();
		}
	}

	/**
	 * Class that stores all the information about the Slabfish Patreon Hat settings.
	 */
	public static final class SlabfishSettings {
		@ConfigKey("slabfish_hat_enabled")
		public final ModConfigSpec.ConfigValue<Boolean> enabled;
		@ConfigKey("slabfish_hat_backpack_enabled")
		public final ModConfigSpec.ConfigValue<Boolean> backpackEnabled;
		@ConfigKey("slabfish_hat_sweater_enabled")
		public final ModConfigSpec.ConfigValue<Boolean> sweaterEnabled;
		@ConfigKey("slabfish_hat_custom_type_enabled")
		public final ModConfigSpec.ConfigValue<Boolean> typeEnabled;

		SlabfishSettings(ModConfigSpec.Builder builder) {
			builder.comment("Slabfish patron hat settings.")
					.push("slabfishSettings");

			this.enabled = builder
					.comment("If the slabfish hat should be enabled")
					.translation(makeTranslation("slabfishHat"))
					.define("enabled", true);
			this.backpackEnabled = builder
					.comment("If the slabfish hat's backpack should be enabled")
					.translation(makeTranslation("slabfishHat.backpack"))
					.define("backpackEnabled", true);
			this.sweaterEnabled = builder
					.comment("If the slabfish hat's sweater should be enabled")
					.translation(makeTranslation("slabfishHat.sweater"))
					.define("sweaterEnabled", true);
			this.typeEnabled = builder
					.comment("If the slabfish hat's custom type should be enabled. If false, the default swamp slabfish appears")
					.translation(makeTranslation("slabfishHat.type"))
					.define("typeEnabled", true);

			builder.pop();
		}
	}

	private static String makeTranslation(String name) {
		return "blueprint.config." + name;
	}

	public static final ModConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;
	public static final ModConfigSpec COMMON_SPEC;
	public static final Common COMMON;

	static {
		Pair<Client, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = clientSpecPair.getRight();
		CLIENT = clientSpecPair.getLeft();

		Pair<Common, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = commonSpecPair.getRight();
		COMMON = commonSpecPair.getLeft();
	}

}