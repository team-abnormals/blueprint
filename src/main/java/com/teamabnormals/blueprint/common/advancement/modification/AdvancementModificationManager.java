package com.teamabnormals.blueprint.common.advancement.modification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.teamabnormals.blueprint.common.advancement.modification.modifiers.AdvancementModifier;
import com.teamabnormals.blueprint.core.util.modification.ObjectModificationManager;
import net.minecraft.resources.RegistryOps;

import javax.annotation.Nullable;

/**
 * Data manager class for the {@link AdvancementModifier} system.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class AdvancementModificationManager extends ObjectModificationManager<BlueprintAdvancementBuilder, RegistryOps<JsonElement>, RegistryOps<JsonElement>> {
	public static final String TARGET_PATH = "advancements";
	private static final Gson GSON = (new GsonBuilder()).create();
	public static AdvancementModificationManager INSTANCE;

	public AdvancementModificationManager(RegistryOps<JsonElement> registryOps) {
		super(GSON, TARGET_PATH, "Advancement", AdvancementModifierSerializers.REGISTRY, (location) -> registryOps, true, true);
	}

	/**
	 * Gets the instance of the {@link AdvancementModificationManager}.
	 * <p>This is initialized once it has been added as a reload listener.</p>
	 *
	 * @return The instance of the {@link AdvancementModificationManager}.
	 */
	@Nullable
	public static AdvancementModificationManager getInstance() {
		return INSTANCE;
	}
}
