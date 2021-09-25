package com.minecraftabnormals.abnormals_core.client;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;

/**
 * Contains some useful RenderTypes
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class ACRenderTypes extends RenderStateShard {

	public ACRenderTypes(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType getEmissiveEntity(ResourceLocation texture) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(NO_DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
		return RenderType.create(AbnormalsCore.MODID + ":entity_emissive_cutout", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, state);
	}

	public static RenderType getEmissiveTransluscentEntity(ResourceLocation texture, boolean outline) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(NO_DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(outline);
		return RenderType.create(AbnormalsCore.MODID + ":entity_emmisive_translucent", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, state);
	}

	public static RenderType getEmissiveTransluscentEntityWithDiffusedLight(ResourceLocation texture, boolean outline) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(outline);
		return RenderType.create(AbnormalsCore.MODID + ":entity_emmisive_translucent", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, state);
	}

}