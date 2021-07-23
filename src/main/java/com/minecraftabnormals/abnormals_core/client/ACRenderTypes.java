package com.minecraftabnormals.abnormals_core.client;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

/**
 * Contains some useful RenderTypes
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class ACRenderTypes extends RenderState {

	public ACRenderTypes(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType getEmissiveEntity(ResourceLocation texture) {
		RenderType.State state = RenderType.State.builder().setTextureState(new RenderState.TextureState(texture, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(NO_DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
		return RenderType.create(AbnormalsCore.MODID + ":entity_emissive_cutout", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, state);
	}

	public static RenderType getEmissiveTransluscentEntity(ResourceLocation texture, boolean outline) {
		RenderType.State state = RenderType.State.builder().setTextureState(new RenderState.TextureState(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(NO_DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(outline);
		return RenderType.create(AbnormalsCore.MODID + ":entity_emmisive_translucent", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, state);
	}

	public static RenderType getEmissiveTransluscentEntityWithDiffusedLight(ResourceLocation texture, boolean outline) {
		RenderType.State state = RenderType.State.builder().setTextureState(new RenderState.TextureState(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(outline);
		return RenderType.create(AbnormalsCore.MODID + ":entity_emmisive_translucent", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, state);
	}

}