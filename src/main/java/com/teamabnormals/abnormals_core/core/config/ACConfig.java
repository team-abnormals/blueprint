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

	public static class Client {
		public final ConfigValue<Boolean> enableQuarkSignEditing;
		public final ConfigValue<Boolean> signEditingRequiresEmptyHand;
		
		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("Client only settings for Abnormals Core, this will affect all depending mods")
			.push("client");
			
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
	
	public static final ForgeConfigSpec CLIENTSPEC;
	public static final Client CLIENT;
	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENTSPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}
	
	public static class ValuesHolder {
		private static boolean quarkSignEditing;
		private static boolean signEditingRequireEmptyHand;
		
		public static void updateClientValuesFromConfig(ModConfig config) {
			quarkSignEditing = ACConfig.CLIENT.enableQuarkSignEditing.get();
			signEditingRequireEmptyHand = ACConfig.CLIENT.signEditingRequiresEmptyHand.get();
		}
		
		public static boolean isQuarkSignEditingEnabled() {
			return ModList.get().isLoaded("quark") && quarkSignEditing;
		}
		
		public static boolean doesSignEditingRequireEmptyHand() {
			return signEditingRequireEmptyHand;
		}
	}
 
}