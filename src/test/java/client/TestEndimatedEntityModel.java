package client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.endimator.Endimation;
import com.teamabnormals.blueprint.core.endimator.Endimator;
import com.teamabnormals.blueprint.core.endimator.entity.EndimatorEntityModel;
import com.teamabnormals.blueprint.core.endimator.EndimatorModelPart;
import com.teamabnormals.blueprint.core.endimator.interpolation.EndimationEasers;
import common.entity.TestEndimatedEntity;
import core.BlueprintTest;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public final class TestEndimatedEntityModel<E extends TestEndimatedEntity> extends EndimatorEntityModel<E> {
	private static final Endimation IDLE = Blueprint.ENDIMATION_LOADER.getEndimation(new ResourceLocation(BlueprintTest.MOD_ID, "idle"));
	private final EndimatorModelPart cube;

	public TestEndimatedEntityModel() {
		super();
		this.cube = new EndimatorModelPart(createLayer().bakeRoot().getChild("cube"));
		this.endimator.put("cube", this.cube);
	}

	private static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16), PartPose.offset(0.0F, 16.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void animateModel(E test, float partialTicks) {
		super.animateModel(test, partialTicks);
		assert IDLE != null;
		float time = ((test.tickCount + partialTicks) * 0.05F) % IDLE.getLength();
		this.endimator.apply(IDLE, time, Endimator.ResetMode.UNAPPLY);
		test.idleEffectHandler.update(IDLE, time);
		Endimator.PosedPart cube = this.endimator.getPosedPart("cube");
		assert cube != null;
		float scale = test.hurt.getProgress(EndimationEasers.EASE_IN_OUT_SINE, partialTicks);
		cube.applyAdd(part -> part.addScale(0.25F * scale, 0.25F * scale, 0.25F * scale));
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.cube.render(matrixStack, buffer, 240, packedOverlay, red, green, blue, alpha);
	}

	public void setRotateAngle(EndimatorModelPart EndimatorModelPart, float x, float y, float z) {
		EndimatorModelPart.xRot = x;
		EndimatorModelPart.yRot = y;
		EndimatorModelPart.zRot = z;
	}
}