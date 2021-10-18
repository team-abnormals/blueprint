package com.teamabnormals.blueprint.client;

import com.teamabnormals.blueprint.core.Blueprint;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * A class containing some methods for creating useful {@link RenderType}s.
 *
 * @author SmellyModder(Luke Tonon)
 */
//TODO: Test the types
public final class ACRenderTypes extends RenderStateShard {

	public ACRenderTypes(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, setupTaskIn, clearTaskIn);
	}

	/**
	 * Creates a new full-bright {@link RenderType} for entities.
	 *
	 * @param texture A {@link ResourceLocation} to use for the texture.
	 * @return A new full-bright {@link RenderType} for entities.
	 */
	public static RenderType getEmissiveEntity(ResourceLocation texture) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER).setCullState(NO_CULL).setTransparencyState(NO_TRANSPARENCY).setOverlayState(OVERLAY).createCompositeState(true);
		return RenderType.create(Blueprint.MOD_ID + ":entity_emissive_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, state);
	}

	/**
	 * Creates a new full-bright and translucent {@link RenderType} for entities.
	 *
	 * @param texture A {@link ResourceLocation} to use for the texture.
	 * @param outline If the {@link RenderType} should affect the outline effect.
	 * @return A new full-bright and translucent {@link RenderType} for entities.
	 */
	public static RenderType getEmissiveTranslucentEntity(ResourceLocation texture, boolean outline) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setCullState(NO_CULL).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOverlayState(OVERLAY).createCompositeState(outline);
		return RenderType.create(Blueprint.MOD_ID + ":entity_emissive_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, state);
	}

	/**
	 * Creates a new bright and translucent {@link RenderType} for entities.
	 *
	 * @param texture A {@link ResourceLocation} to use for the texture.
	 * @param outline If the {@link RenderType} should affect the outline effect.
	 * @return A new bright and translucent {@link RenderType} for entities.
	 */
	//TODO: Possibly remove this method?
	public static RenderType getEmissiveTranslucentEntityWithDiffusedLight(ResourceLocation texture, boolean outline) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setCullState(NO_CULL).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(outline);
		return RenderType.create(Blueprint.MOD_ID + ":entity_emissive_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, state);
	}

}