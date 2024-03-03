package com.teamabnormals.blueprint.client.renderer.texture.atlas;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;

/**
 * The class for all of Blueprint's sprite source types.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintSpriteSources {
	public static SpriteSourceType PALETTED_PERMUTATIONS;

	public static void register() {
		PALETTED_PERMUTATIONS = register("paletted_permutations", BlueprintPalettedPermutations.CODEC);
	}

	// TODO: Use NeoForge register method when we move
	private static SpriteSourceType register(String name, Codec<? extends SpriteSource> codec) {
		return SpriteSources.register(Blueprint.MOD_ID + ":" + name, codec);
	}
}
