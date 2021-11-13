package client;

import com.teamabnormals.blueprint.client.BlueprintRenderTypes;
import com.teamabnormals.blueprint.client.EntitySkinHelper;
import common.entity.TestEndimatedEntity;
import core.BlueprintTest;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class TestEndimatedEntityRenderer extends MobRenderer<TestEndimatedEntity, TestEndimatedEntityModel<TestEndimatedEntity>> {
	private static final ResourceLocation TEXTURE = BlueprintTest.REGISTRY_HELPER.prefix("textures/entity/test.png");
	private static final EntitySkinHelper<TestEndimatedEntity> SKIN_HELPER = EntitySkinHelper.create(BlueprintTest.MOD_ID, "textures/entity/skins", "them", (helper) -> {
		helper.putSkins("dudes", "them", "dudes", "smelly", "test");
	});

	public TestEndimatedEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new TestEndimatedEntityModel<>(), 0.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(TestEndimatedEntity entity) {
		return SKIN_HELPER.getSkinForEntityOrElse(entity, TEXTURE);
	}

	@Override
	protected RenderType getRenderType(TestEndimatedEntity entity, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
		return entity.getId() % 2 == 0 ? BlueprintRenderTypes.getUnshadedCutoutEntity(this.getTextureLocation(entity), true) : BlueprintRenderTypes.getUnshadedTranslucentEntity(this.getTextureLocation(entity), true);
	}
}