package com.teamabnormals.abnormals_core.client;

import com.teamabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

/**
 * Contains some useful RenderTypes
 * @author SmellyModder(Luke Tonon)
 */
public class ACRenderTypes extends RenderState {

	public ACRenderTypes(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType getEmissiveEntity(ResourceLocation texture) {
		RenderType.State state = RenderType.State.getBuilder().texture(new RenderState.TextureState(texture, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_DISABLED).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true);
		return RenderType.makeType(AbnormalsCore.MODID + ":entity_emissive_cutout", DefaultVertexFormats.ENTITY, 7, 256, true, false, state);
	}
	
	public static RenderType getEmissiveTransluscentEntity(ResourceLocation texture, boolean outline) {
		RenderType.State state = RenderType.State.getBuilder().texture(new RenderState.TextureState(texture, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_DISABLED).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(outline);
		return RenderType.makeType(AbnormalsCore.MODID + ":entity_emmisive_translucent", DefaultVertexFormats.ENTITY, 7, 256, true, true, state);
	}
	
	public static RenderType getEmissiveTransluscentEntityWithDiffusedLight(ResourceLocation texture, boolean outline) {
		RenderType.State state = RenderType.State.getBuilder().texture(new RenderState.TextureState(texture, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(outline);
		return RenderType.makeType(AbnormalsCore.MODID + ":entity_emmisive_translucent", DefaultVertexFormats.ENTITY, 7, 256, true, true, state);
	}

}