package com.minecraftabnormals.abnormals_core.client;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;

/**
 * Contains some useful RenderTypes
 *
 * @author SmellyModder(Luke Tonon)
 */
//TODO: Test the types
public final class ACRenderTypes extends RenderStateShard {

	public ACRenderTypes(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType getEmissiveEntity(ResourceLocation texture) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER).setCullState(NO_CULL).setTransparencyState(NO_TRANSPARENCY).setOverlayState(OVERLAY).createCompositeState(true);
		return RenderType.create(AbnormalsCore.MODID + ":entity_emissive_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, state);
	}

	public static RenderType getEmissiveTransluscentEntity(ResourceLocation texture, boolean outline) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setCullState(NO_CULL).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOverlayState(OVERLAY).createCompositeState(outline);
		return RenderType.create(AbnormalsCore.MODID + ":entity_emissive_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, state);
	}

	public static RenderType getEmissiveTransluscentEntityWithDiffusedLight(ResourceLocation texture, boolean outline) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setCullState(NO_CULL).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(outline);
		return RenderType.create(AbnormalsCore.MODID + ":entity_emissive_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, state);
	}

}