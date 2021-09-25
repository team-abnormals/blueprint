package com.minecraftabnormals.abnormals_core.core.endimator.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Much like the vanilla RendererModel but can store data of default values and has some more advanced features;
 * Such as setting an individual RendererModel's opacity, scale, and texture position
 * 
 * @author - SmellyModder(Luke Tonon)
 */
@OnlyIn(Dist.CLIENT)
public class EndimatorModelRenderer extends ModelPart {
	public float defaultRotationPointX, defaultRotationPointY, defaultRotationPointZ;
	public float defaultRotateAngleX, defaultRotateAngleY, defaultRotateAngleZ;
	public float defaultOffsetX, defaultOffsetY, defaultOffsetZ, offsetX, offsetY, offsetZ;
	public float defaultScaleX, defaultScaleY, defaultScaleZ, scaleX, scaleY, scaleZ;
	public int textureOffsetX, textureOffsetY;
	public float textureWidth, textureHeight;
	public boolean scaleChildren = true;
	private String name;
	private final ObjectList<ModelBox> cubeList = new ObjectArrayList<>();
	private final ObjectList<EndimatorModelRenderer> childModels = new ObjectArrayList<>();

	/**
	 * @param model - Entity model this ModelRenderer belongs to
	 */
	public EndimatorModelRenderer(EndimatorEntityModel<? extends Entity> model) {
		super(model);
		this.setScale(1.0F, 1.0F, 1.0F);
		model.addBoxToSavedBoxes(this);
		model.accept(this);
		this.setTexSize(model.texWidth, model.texHeight);
	}
	
	/**
	 * Texture offset constuctor 
	 * @param model - Entity model this ModelRenderer belongs to
	 * @param textureOffsetX - X offset on the texture
	 * @param textureOffsetY - Y offset on the texture
	 */
	public EndimatorModelRenderer(EndimatorEntityModel<? extends Entity> model, int textureOffsetX, int textureOffsetY) {
		this(model.texWidth, model.texHeight, textureOffsetX, textureOffsetY);
		model.addBoxToSavedBoxes(this);
		model.accept(this);
	}
	
	public EndimatorModelRenderer(int textureWidthIn, int textureHeightIn, int textureOffsetXIn, int textureOffsetYIn) {
		super(textureWidthIn, textureHeightIn, textureOffsetXIn, textureOffsetYIn);
		this.setScale(1.0F, 1.0F, 1.0F);
	}
	
	public void addChild(EndimatorModelRenderer renderer) {
		this.childModels.add(renderer);
	}
	
	/**
	 * Sets the name for this ModelRenderer to be used Endimation JSONs
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return - The name of this ModelRenderer
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Performs the same function as vanilla's setTextureOffset
	 */
	@Override
	public EndimatorModelRenderer texOffs(int x, int y) {
		this.textureOffsetX = x;
		this.textureOffsetY = y;
		return this;
	}
	
	@Override
	public EndimatorModelRenderer setTexSize(int textureWidthIn, int textureHeightIn) {
		this.textureWidth = (float)textureWidthIn;
		this.textureHeight = (float)textureHeightIn;
		return this;
	}
	
	@Override
	public EndimatorModelRenderer addBox(String partName, float x, float y, float z, int width, int height, int depth, float delta, int texX, int texY) {
		this.texOffs(texX, texY);
		this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, (float)width, (float)height, (float)depth, delta, delta, delta, this.mirror, false);
		return this;
	}

	public EndimatorModelRenderer addBox(float x, float y, float z, float width, float height, float depth) {
		this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, this.mirror, false);
		return this;
	}

	public EndimatorModelRenderer addBox(float x, float y, float z, float width, float height, float depth, boolean mirrorIn) {
		this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, mirrorIn, false);
		return this;
	}

	public void addBox(float x, float y, float z, float width, float height, float depth, float delta) {
		this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, delta, delta, delta, this.mirror, false);
	}

	public void addBox(float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ) {
		this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, this.mirror, false);
	}

	public void addBox(float x, float y, float z, float width, float height, float depth, float delta, boolean mirrorIn) {
		this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, delta, delta, delta, mirrorIn, false);
	}

	private void addBox(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirorIn, boolean p_228305_13_) {
		this.cubeList.add(new ModelBox(texOffX, texOffY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, mirorIn, this.textureWidth, this.textureHeight));
	}

	/**
	 * A method that sets the default box's values
	 * Should be called after all the boxes in an entity model have been initialized
	 */
	public void setDefaultBoxValues() {
		this.defaultRotationPointX = this.x;
		this.defaultRotationPointY = this.y;
		this.defaultRotationPointZ = this.z;
		
		this.defaultOffsetX = this.offsetX;
		this.defaultOffsetY = this.offsetY;
		this.defaultOffsetZ = this.offsetZ;
		
		this.defaultRotateAngleX = this.xRot;
		this.defaultRotateAngleY = this.yRot;
		this.defaultRotateAngleZ = this.zRot;
		
		this.defaultScaleX = this.scaleX;
		this.defaultScaleY = this.scaleY;
		this.defaultScaleZ = this.scaleZ;
	}
	
	/**
	 * A method that reverts the current box's values back to the default values.
	 *
	 * Should be called before applying further rotations and/or animations.
	 */
	public void revertToDefaultBoxValues() {
		this.x = this.defaultRotationPointX;
		this.y = this.defaultRotationPointY;
		this.z = this.defaultRotationPointZ;
		
		this.offsetX = this.defaultOffsetX;
		this.offsetY = this.defaultOffsetY;
		this.offsetZ = this.defaultOffsetZ;
		
		this.xRot = this.defaultRotateAngleX;
		this.yRot = this.defaultRotateAngleY;
		this.zRot = this.defaultRotateAngleZ;
		
		this.scaleX = this.defaultScaleX;
		this.scaleY = this.defaultScaleY;
		this.scaleZ = this.defaultScaleZ;
	}
	
	/**
	 * Sets the scale
	 */
	public void setScale(float x, float y, float z) {
		this.scaleX = x;
		this.scaleY = y;
		this.scaleZ = z;
	}
	
	/**
	 * Sets the scale of the X axis on this ModelRenderer
	 * @param scaleX - Value of scale
	 */
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}
	
	/**
	 * Sets the scale of the Y axis on this ModelRenderer
	 * @param scaleY - Value of scale
	 */
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}
	
	/**
	 * Sets the scale of the Z axis on this ModelRenderer
	 * @param scaleZ - Value of scale
	 */
	public void setScaleZ(float scaleZ) {
		this.scaleZ = scaleZ;
	}
	
	public void applyScaling(EndimatorModelRenderer modelRenderer) {
		this.setScale(modelRenderer.x, modelRenderer.y, modelRenderer.z);
	}
	
	public void setShouldScaleChildren(boolean scaleChildren) {
		this.scaleChildren = scaleChildren;
	}
	
	public void setDefaultOffset(float x, float y, float z) {
		this.defaultOffsetX = x;
		this.defaultOffsetY = y;
		this.defaultOffsetZ = z;
	}
	
	public void setOffset(float x, float y, float z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
	}
	
	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.visible) {
			if (!this.cubeList.isEmpty() || !this.childModels.isEmpty()) {
				matrixStackIn.pushPose();
				this.translateAndRotate(matrixStackIn);
				
				if (this.scaleChildren) {
					matrixStackIn.translate(this.offsetX, this.offsetY, this.offsetZ);
					matrixStackIn.scale(this.scaleX, this.scaleY, this.scaleZ);
					this.doRender(matrixStackIn.last(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
					
					for (EndimatorModelRenderer modelrenderer : this.childModels) {
						modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
					}
				} else {
					matrixStackIn.pushPose();
					matrixStackIn.translate(this.offsetX, this.offsetY, this.offsetZ);
					matrixStackIn.scale(this.scaleX, this.scaleY, this.scaleZ);
					this.doRender(matrixStackIn.last(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
					matrixStackIn.popPose();
					
					for (EndimatorModelRenderer modelrenderer : this.childModels) {
						modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
					}
				}
				matrixStackIn.popPose();
			}
		}
	}
	
	private void doRender(PoseStack.Pose matrixEntryIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		Matrix4f matrix4f = matrixEntryIn.pose();
		Matrix3f matrix3f = matrixEntryIn.normal();

		for (ModelBox modelrenderer$modelbox : this.cubeList) {
			for (TexturedQuad modelrenderer$texturedquad : modelrenderer$modelbox.quads) {
				Vector3f vector3f = modelrenderer$texturedquad.normal.copy();
				vector3f.transform(matrix3f);
				float f = vector3f.x();
				float f1 = vector3f.y();
				float f2 = vector3f.z();

				for (int i = 0; i < 4; ++i) {
					PositionTextureVertex modelrenderer$positiontexturevertex = modelrenderer$texturedquad.vertexPositions[i];
					float f3 = modelrenderer$positiontexturevertex.position.x() / 16.0F;
					float f4 = modelrenderer$positiontexturevertex.position.y() / 16.0F;
					float f5 = modelrenderer$positiontexturevertex.position.z() / 16.0F;
					Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
					vector4f.transform(matrix4f);
					bufferIn.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, modelrenderer$positiontexturevertex.textureU, modelrenderer$positiontexturevertex.textureV, packedOverlayIn, packedLightIn, f, f1, f2);
				}
			}
		}
	}
	
	public static class ModelBox {
		protected final TexturedQuad[] quads;
		public final float posX1;
		public final float posY1;
		public final float posZ1;
		public final float posX2;
		public final float posY2;
		public final float posZ2;

		public ModelBox(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirorIn, float texWidth, float texHeight) {
			this.posX1 = x;
			this.posY1 = y;
			this.posZ1 = z;
			this.posX2 = x + width;
			this.posY2 = y + height;
			this.posZ2 = z + depth;
			this.quads = new TexturedQuad[6];
			float f = x + width;
			float f1 = y + height;
			float f2 = z + depth;
			x = x - deltaX;
			y = y - deltaY;
			z = z - deltaZ;
			f = f + deltaX;
			f1 = f1 + deltaY;
			f2 = f2 + deltaZ;
			if(mirorIn) {
				float f3 = f;
				f = x;
				x = f3;
			}

			PositionTextureVertex modelrenderer$positiontexturevertex7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			PositionTextureVertex modelrenderer$positiontexturevertex = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
			PositionTextureVertex modelrenderer$positiontexturevertex1 = new PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
			PositionTextureVertex modelrenderer$positiontexturevertex2 = new PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
			PositionTextureVertex modelrenderer$positiontexturevertex3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			PositionTextureVertex modelrenderer$positiontexturevertex4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			PositionTextureVertex modelrenderer$positiontexturevertex5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
			PositionTextureVertex modelrenderer$positiontexturevertex6 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
			float f4 = (float)texOffX;
			float f5 = (float)texOffX + depth;
			float f6 = (float)texOffX + depth + width;
			float f7 = (float)texOffX + depth + width + width;
			float f8 = (float)texOffX + depth + width + depth;
			float f9 = (float)texOffX + depth + width + depth + width;
			float f10 = (float)texOffY;
			float f11 = (float)texOffY + depth;
			float f12 = (float)texOffY + depth + height;
			this.quads[2] = new TexturedQuad(new PositionTextureVertex[]{modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex}, f5, f10, f6, f11, texWidth, texHeight, mirorIn, Direction.DOWN);
			this.quads[3] = new TexturedQuad(new PositionTextureVertex[]{modelrenderer$positiontexturevertex1, modelrenderer$positiontexturevertex2, modelrenderer$positiontexturevertex6, modelrenderer$positiontexturevertex5}, f6, f11, f7, f10, texWidth, texHeight, mirorIn, Direction.UP);
			this.quads[1] = new TexturedQuad(new PositionTextureVertex[]{modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex6, modelrenderer$positiontexturevertex2}, f4, f11, f5, f12, texWidth, texHeight, mirorIn, Direction.WEST);
			this.quads[4] = new TexturedQuad(new PositionTextureVertex[]{modelrenderer$positiontexturevertex, modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex2, modelrenderer$positiontexturevertex1}, f5, f11, f6, f12, texWidth, texHeight, mirorIn, Direction.NORTH);
			this.quads[0] = new TexturedQuad(new PositionTextureVertex[]{modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex, modelrenderer$positiontexturevertex1, modelrenderer$positiontexturevertex5}, f6, f11, f8, f12, texWidth, texHeight, mirorIn, Direction.EAST);
			this.quads[5] = new TexturedQuad(new PositionTextureVertex[]{modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex5, modelrenderer$positiontexturevertex6}, f8, f11, f9, f12, texWidth, texHeight, mirorIn, Direction.SOUTH);
		}
	}
	
	protected static class PositionTextureVertex {
		public final Vector3f position;
		public final float textureU;
		public final float textureV;

		public PositionTextureVertex(float x, float y, float z, float texU, float texV) {
			this(new Vector3f(x, y, z), texU, texV);
		}

		public PositionTextureVertex setTextureUV(float texU, float texV) {
			return new PositionTextureVertex(this.position, texU, texV);
		}

		public PositionTextureVertex(Vector3f posIn, float texU, float texV) {
			this.position = posIn;
			this.textureU = texU;
			this.textureV = texV;
		}
	}
	
	protected static class TexturedQuad {
		public final PositionTextureVertex[] vertexPositions;
		public final Vector3f normal;

		public TexturedQuad(PositionTextureVertex[] positionsIn, float u1, float v1, float u2, float v2, float texWidth, float texHeight, boolean mirrorIn, Direction directionIn) {
			this.vertexPositions = positionsIn;
			float f = 0.0F / texWidth;
			float f1 = 0.0F / texHeight;
			positionsIn[0] = positionsIn[0].setTextureUV(u2 / texWidth - f, v1 / texHeight + f1);
			positionsIn[1] = positionsIn[1].setTextureUV(u1 / texWidth + f, v1 / texHeight + f1);
			positionsIn[2] = positionsIn[2].setTextureUV(u1 / texWidth + f, v2 / texHeight - f1);
			positionsIn[3] = positionsIn[3].setTextureUV(u2 / texWidth - f, v2 / texHeight - f1);
			if (mirrorIn) {
				int i = positionsIn.length;

				for (int j = 0; j < i / 2; ++j) {
	               PositionTextureVertex modelrenderer$positiontexturevertex = positionsIn[j];
	               positionsIn[j] = positionsIn[i - 1 - j];
	               positionsIn[i - 1 - j] = modelrenderer$positiontexturevertex;
	            }
			}

			this.normal = directionIn.step();
			if (mirrorIn) {
				this.normal.mul(-1.0F, 1.0F, 1.0F);
			}
		}
	}
}