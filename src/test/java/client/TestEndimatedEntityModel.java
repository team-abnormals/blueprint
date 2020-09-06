package client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamabnormals.abnormals_core.core.annotations.Test;
import com.teamabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;
import com.teamabnormals.abnormals_core.core.endimator.entity.EndimatorModelRenderer;
import common.entities.TestEndimatedEntity;

@Test
public final class TestEndimatedEntityModel<E extends TestEndimatedEntity> extends EndimatorEntityModel<E> {
	private EndimatorModelRenderer cube;

    public TestEndimatedEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.cube = new EndimatorModelRenderer(this, 0, 0);
        this.cube.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.cube.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F);
        this.cube.setName("cube");
        
        this.setDefaultBoxValues();
    }
    
    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    	super.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    	this.cube.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
    
    public void setRotateAngle(EndimatorModelRenderer EndimatorModelRenderer, float x, float y, float z) {
        EndimatorModelRenderer.rotateAngleX = x;
        EndimatorModelRenderer.rotateAngleY = y;
        EndimatorModelRenderer.rotateAngleZ = z;
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