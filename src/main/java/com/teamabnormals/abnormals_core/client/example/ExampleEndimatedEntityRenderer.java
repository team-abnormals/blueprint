package com.teamabnormals.abnormals_core.client.example;

import com.teamabnormals.abnormals_core.common.entity.ExampleEndimatedEntity;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;

public class ExampleEndimatedEntityRenderer extends LivingRenderer<ExampleEndimatedEntity, ExampleEndimatedEntityModel<ExampleEndimatedEntity>> {
	
	public ExampleEndimatedEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new ExampleEndimatedEntityModel<>(), 0.0F);
    }
	
	@Override
	public ResourceLocation getEntityTexture(ExampleEndimatedEntity entity) {
		return AbnormalsCore.REGISTRY_HELPER.prefix("textures/example.png");
	}
	
}