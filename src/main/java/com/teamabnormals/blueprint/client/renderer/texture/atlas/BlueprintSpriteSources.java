package com.teamabnormals.blueprint.client.renderer.texture.atlas;

import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;

/**
 * The class for all of Blueprint's sprite source types.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintSpriteSources {
	public static SpriteSourceType PALETTED_PERMUTATIONS;

	public static void register(RegisterSpriteSourceTypesEvent event) {
		event.register(ResourceLocation.fromNamespaceAndPath(Blueprint.MOD_ID, "paletted_permutations"), PALETTED_PERMUTATIONS = new SpriteSourceType(BlueprintPalettedPermutations.CODEC));
	}
}
