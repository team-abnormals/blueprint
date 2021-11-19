package client;

import common.entity.EndimatedWalkingEntity;
import core.BlueprintTest;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class EndimatedWalkingEntityRenderer extends MobRenderer<EndimatedWalkingEntity, EndimatedWalkingEntityModel<EndimatedWalkingEntity>> {
	private static final ResourceLocation TEXTURE = BlueprintTest.REGISTRY_HELPER.prefix("textures/entity/walking.png");

	public EndimatedWalkingEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new EndimatedWalkingEntityModel<>(), 0.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(EndimatedWalkingEntity entity) {
		return TEXTURE;
	}
}