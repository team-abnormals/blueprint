package com.teamabnormals.abnormals_core.client.example;

import com.teamabnormals.abnormals_core.client.EntitySkinHelper;
import com.teamabnormals.abnormals_core.common.entity.ExampleEndimatedEntity;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;

public final class ExampleEndimatedEntityRenderer extends LivingRenderer<ExampleEndimatedEntity, ExampleEndimatedEntityModel<ExampleEndimatedEntity>> {
	private static final EntitySkinHelper<ExampleEndimatedEntity> SKIN_HELPER = EntitySkinHelper.create(AbnormalsCore.MODID, "textures/entity/skins", "them", (helper) -> {
		helper.putSkins("dudes", "them", "dudes", "smelly", "test");
	});

	public ExampleEndimatedEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new ExampleEndimatedEntityModel<>(), 0.0F);
    }
	
	@Override
	public ResourceLocation getEntityTexture(ExampleEndimatedEntity entity) {
		return SKIN_HELPER.getSkinForEntityOrElse(entity, AbnormalsCore.REGISTRY_HELPER.prefix("textures/entity/example.png"));
	}
}