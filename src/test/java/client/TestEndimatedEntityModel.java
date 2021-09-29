package client;

import com.minecraftabnormals.abnormals_core.core.annotations.Test;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorModelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import common.entities.TestEndimatedEntity;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

@Test
public final class TestEndimatedEntityModel<E extends TestEndimatedEntity> extends EndimatorEntityModel<E> {
	private EndimatorModelRenderer cube;

	public TestEndimatedEntityModel() {
		this.cube = new EndimatorModelRenderer(createLayer().bakeRoot().getChild("cube"));
		this.setDefaultBoxValues();
	}

	private static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16), PartPose.offset(0.0F, 16.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.cube.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotateAngle(EndimatorModelRenderer EndimatorModelRenderer, float x, float y, float z) {
		EndimatorModelRenderer.xRot = x;
		EndimatorModelRenderer.yRot = y;
		EndimatorModelRenderer.zRot = z;
	}

	@Override
	public void animateModel(E exampleEntity) {
		super.animateModel(exampleEntity);

		if (this.tryToPlayEndimation(TestEndimatedEntity.SINK_ANIMATION)) {
			this.startKeyframe(10);
			this.offset(this.cube, 0.0F, 1.0F, 0.0F);
			this.endKeyframe();
			this.resetKeyframe(10);
		} else if (this.tryToPlayEndimation(TestEndimatedEntity.GROW_ANIMATION)) {
			this.startKeyframe(10);
			this.scaleAdditive(this.cube, 0.5F, 0.5F, 0.5F);
			this.endKeyframe();
			this.resetKeyframe(10);
		} else if (this.tryToPlayEndimation(TestEndimatedEntity.DEATH_ANIMATION)) {
			TestEndimatedEntity.DEATH_ANIMATION.processInstructions(this);
		}
	}
}