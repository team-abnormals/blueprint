package client;

import com.teamabnormals.abnormals_core.client.EntitySkinHelper;
import com.teamabnormals.abnormals_core.core.annotations.Test;
import common.entities.TestEndimatedEntity;
import core.ACTest;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;

@Test
public final class TestEndimatedEntityRenderer extends LivingRenderer<TestEndimatedEntity, TestEndimatedEntityModel<TestEndimatedEntity>> {
	private static final ResourceLocation TEXTURE = ACTest.REGISTRY_HELPER.prefix("textures/entity/test.png");
	private static final EntitySkinHelper<TestEndimatedEntity> SKIN_HELPER = EntitySkinHelper.create(ACTest.MOD_ID, "textures/entity/skins", "them", (helper) -> {
		helper.putSkins("dudes", "them", "dudes", "smelly", "test");
	});

	public TestEndimatedEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new TestEndimatedEntityModel<>(), 0.0F);
    }
	
	@Override
	public ResourceLocation getEntityTexture(TestEndimatedEntity entity) {
		return SKIN_HELPER.getSkinForEntityOrElse(entity, TEXTURE);
	}
}