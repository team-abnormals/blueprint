package com.teamabnormals.blueprint.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * A class containing some methods for creating useful {@link RenderType} instances.
 *
 * @author SmellyModder(Luke Tonon)
 */
public final class BlueprintRenderTypes extends RenderType {
	public static final ShaderStateShard RENDERTYPE_ENTITY_UNSHADED_CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(BlueprintShaders::getRendertypeEntityUnshadedCutout);
	public static final ShaderStateShard RENDERTYPE_ENTITY_UNSHADED_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(BlueprintShaders::getRendertypeEntityUnshadedTranslucent);

	private BlueprintRenderTypes(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
		super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
	}

	/**
	 * Creates a new unshaded {@link RenderType} for cutout entities for a given texture.
	 *
	 * @param texture A {@link ResourceLocation} to use for the texture.
	 * @param outline If the {@link RenderType} should affect the outline effect.
	 * @return A new unshaded {@link RenderType} for cutout entities for a given texture.
	 */
	public static RenderType getUnshadedCutoutEntity(ResourceLocation texture, boolean outline) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setShaderState(RENDERTYPE_ENTITY_UNSHADED_CUTOUT_SHADER).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(outline);
		return create(Blueprint.MOD_ID + ":entity_unshaded_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, state);
	}

	/**
	 * Creates a new unshaded and translucent {@link RenderType} for entities for a given texture.
	 *
	 * @param texture A {@link ResourceLocation} to use for the texture.
	 * @param outline If the {@link RenderType} should affect the outline effect.
	 * @return A new unshaded and translucent {@link RenderType} for entities for a given texture.
	 */
	public static RenderType getUnshadedTranslucentEntity(ResourceLocation texture, boolean outline) {
		RenderType.CompositeState state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setShaderState(RENDERTYPE_ENTITY_UNSHADED_TRANSLUCENT_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(outline);
		return create(Blueprint.MOD_ID + ":entity_unshaded_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, state);
	}
}