package client;

import com.minecraftabnormals.abnormals_core.client.EntitySkinHelper;
import common.entities.TestEndimatedEntity;
import core.ACTest;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public final class TestEndimatedEntityRenderer extends LivingEntityRenderer<TestEndimatedEntity, TestEndimatedEntityModel<TestEndimatedEntity>> {
	private static final ResourceLocation TEXTURE = ACTest.REGISTRY_HELPER.prefix("textures/entity/test.png");
	private static final EntitySkinHelper<TestEndimatedEntity> SKIN_HELPER = EntitySkinHelper.create(ACTest.MOD_ID, "textures/entity/skins", "them", (helper) -> {
		helper.putSkins("dudes", "them", "dudes", "smelly", "test");
	});

	public TestEndimatedEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new TestEndimatedEntityModel<>(), 0.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(TestEndimatedEntity entity) {
		return SKIN_HELPER.getSkinForEntityOrElse(entity, TEXTURE);
	}
}