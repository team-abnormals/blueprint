package com.teamabnormals.blueprint.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.event.RegisterShadersEvent;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * The class for all of Blueprint's shaders.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintShaders {
	@Nullable
	private static ShaderInstance rendertypeEntityUnshadedCutout;
	@Nullable
	private static ShaderInstance rendertypeEntityUnshadedTranslucent;

	public static void registerShaders(RegisterShadersEvent event) {
		try {
			ResourceManager resourceManager = event.getResourceManager();
			event.registerShader(new ShaderInstance(resourceManager, new ResourceLocation(Blueprint.MOD_ID, "rendertype_entity_unshaded_cutout"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> {
				rendertypeEntityUnshadedCutout = shaderInstance;
			});
			event.registerShader(new ShaderInstance(resourceManager, new ResourceLocation(Blueprint.MOD_ID, "rendertype_entity_unshaded_translucent"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> {
				rendertypeEntityUnshadedTranslucent = shaderInstance;
			});
		} catch (IOException e) {
			throw new RuntimeException("Could not reload Blueprint's shaders!", e);
		}
	}

	/**
	 * Gets the {@link ShaderInstance} used for unshaded cutout entities.
	 *
	 * @return The {@link ShaderInstance} used for unshaded cutout entities.
	 * @see BlueprintRenderTypes#getUnshadedCutoutEntity(ResourceLocation, boolean)
	 */
	@Nullable
	public static ShaderInstance getRendertypeEntityUnshadedCutout() {
		return rendertypeEntityUnshadedCutout;
	}

	/**
	 * Gets the {@link ShaderInstance} used for unshaded translucent entities.
	 *
	 * @return The {@link ShaderInstance} used for unshaded translucent entities.
	 * @see BlueprintRenderTypes#getUnshadedTranslucentEntity(ResourceLocation, boolean)
	 */
	@Nullable
	public static ShaderInstance getRendertypeEntityUnshadedTranslucent() {
		return rendertypeEntityUnshadedTranslucent;
	}
}
