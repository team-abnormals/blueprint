package com.teamabnormals.abnormals_core.client.tile;

import java.util.Calendar;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamabnormals.abnormals_core.core.library.api.IChestBlock;

import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class AbnormalsChestTileEntityRenderer<T extends TileEntity & IChestLid> extends TileEntityRenderer<T> {
	
	public static Block itemBlock = null; 
	
	public final ModelRenderer singleLid;
	public final ModelRenderer singleBottom;
	public final ModelRenderer singleLatch;
	public final ModelRenderer rightLid;
	public final ModelRenderer rightBottom;
	public final ModelRenderer rightLatch;
	public final ModelRenderer leftLid;
	public final ModelRenderer leftBottom;
	public final ModelRenderer leftLatch;
	public boolean isChristmas;

	public AbnormalsChestTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.isChristmas = true;
		}

		this.singleBottom = new ModelRenderer(64, 64, 0, 19);
		this.singleBottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.singleLid = new ModelRenderer(64, 64, 0, 0);
		this.singleLid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.singleLid.rotationPointY = 9.0F;
		this.singleLid.rotationPointZ = 1.0F;
		this.singleLatch = new ModelRenderer(64, 64, 0, 0);
		this.singleLatch.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.singleLatch.rotationPointY = 8.0F;
		this.rightBottom = new ModelRenderer(64, 64, 0, 19);
		this.rightBottom.addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.rightLid = new ModelRenderer(64, 64, 0, 0);
		this.rightLid.addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.rightLid.rotationPointY = 9.0F;
		this.rightLid.rotationPointZ = 1.0F;
		this.rightLatch = new ModelRenderer(64, 64, 0, 0);
		this.rightLatch.addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.rightLatch.rotationPointY = 8.0F;
		this.leftBottom = new ModelRenderer(64, 64, 0, 19);
		this.leftBottom.addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.leftLid = new ModelRenderer(64, 64, 0, 0);
		this.leftLid.addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.leftLid.rotationPointY = 9.0F;
		this.leftLid.rotationPointZ = 1.0F;
		this.leftLatch = new ModelRenderer(64, 64, 0, 0);
		this.leftLatch.addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.leftLatch.rotationPointY = 8.0F;
	}

	@SuppressWarnings("rawtypes")
	public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		World world = tileEntityIn.getWorld();
		boolean flag = world != null;
		BlockState blockstate = flag ? tileEntityIn.getBlockState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chesttype = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.get(ChestBlock.TYPE) : ChestType.SINGLE;
		Block block = blockstate.getBlock();
		if (block instanceof AbstractChestBlock) {
			AbstractChestBlock<?> abstractchestblock = (AbstractChestBlock) block;
			boolean flag1 = chesttype != ChestType.SINGLE;
			matrixStackIn.push();
			float f = blockstate.get(ChestBlock.FACING).getHorizontalAngle();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
			TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> icallbackwrapper;
			if (flag) {
				icallbackwrapper = abstractchestblock.combine(blockstate, world, tileEntityIn.getPos(), true);
			} else {
				icallbackwrapper = TileEntityMerger.ICallback::func_225537_b_;
			}
			
			float f1 = icallbackwrapper.apply(ChestBlock.getLidRotationCallback((IChestLid)tileEntityIn)).get(partialTicks);
			f1 = 1.0F - f1;
			f1 = 1.0F - f1 * f1 * f1;
			int i = icallbackwrapper.apply(new DualBrightnessCallback<>()).applyAsInt(combinedLightIn);
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(this.getChestTexture(tileEntityIn, chesttype)));
			if (flag1) {
				if (chesttype == ChestType.LEFT) {
					this.func_228871_a_(matrixStackIn, ivertexbuilder, this.leftLid, this.leftLatch, this.leftBottom, f1, i, combinedOverlayIn);
				} else {
					this.func_228871_a_(matrixStackIn, ivertexbuilder, this.rightLid, this.rightLatch, this.rightBottom, f1, i, combinedOverlayIn);
				}
			} else {
				this.func_228871_a_(matrixStackIn, ivertexbuilder, this.singleLid, this.singleLatch, this.singleBottom, f1, i, combinedOverlayIn);
			}

			matrixStackIn.pop();
		}
	}

	public ResourceLocation getChestTexture(T t, ChestType type) {
		Block inventoryBlock = itemBlock;
		if(inventoryBlock == null) inventoryBlock = t.getBlockState().getBlock();
		IChestBlock block = (IChestBlock) inventoryBlock;
		
		String chestType = block.getChestName() + (block.isTrapped() ? "/trapped" : "/normal");
		String modid = block.getModid();
		
		if (this.isChristmas) {
			chestType = "christmas"; 
			modid = "minecraft";
		}

		switch(type) {
			default:
			case SINGLE:	return new ResourceLocation(modid, "textures/entity/chest/" + chestType +".png");
			case LEFT: 		return new ResourceLocation(modid, "textures/entity/chest/" + chestType + "_left.png");
			case RIGHT: 	return new ResourceLocation(modid, "textures/entity/chest/" + chestType + "_right.png");
		}
	}

	public void func_228871_a_(MatrixStack matrixStack, IVertexBuilder builder, ModelRenderer chestLid, ModelRenderer chestLatch, ModelRenderer chestBottom, float lidAngle, int combinedLightIn, int combinedOverlayIn) {
		chestLid.rotateAngleX = -(lidAngle * ((float)Math.PI / 2F));
		chestLatch.rotateAngleX = chestLid.rotateAngleX;
		chestLid.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		chestLatch.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		chestBottom.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
	}
}