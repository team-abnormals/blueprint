package com.minecraftabnormals.abnormals_core.client.tile;

import com.minecraftabnormals.abnormals_core.client.ChestManager;
import com.minecraftabnormals.abnormals_core.core.api.IChestBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.block.*;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;

import java.util.Calendar;

import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AbnormalsChestTileEntityRenderer<T extends BlockEntity & LidBlockEntity> extends BlockEntityRenderer<T> {
	public static Block itemBlock = null;
	
	public final ModelPart singleLid;
	public final ModelPart singleBottom;
	public final ModelPart singleLatch;
	public final ModelPart rightLid;
	public final ModelPart rightBottom;
	public final ModelPart rightLatch;
	public final ModelPart leftLid;
	public final ModelPart leftBottom;
	public final ModelPart leftLatch;
	public boolean isChristmas;

	public AbnormalsChestTileEntityRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26) {
			this.isChristmas = true;
		}

		this.singleBottom = new ModelPart(64, 64, 0, 19);
		this.singleBottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.singleLid = new ModelPart(64, 64, 0, 0);
		this.singleLid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.singleLid.y = 9.0F;
		this.singleLid.z = 1.0F;
		this.singleLatch = new ModelPart(64, 64, 0, 0);
		this.singleLatch.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.singleLatch.y = 8.0F;
		this.rightBottom = new ModelPart(64, 64, 0, 19);
		this.rightBottom.addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.rightLid = new ModelPart(64, 64, 0, 0);
		this.rightLid.addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.rightLid.y = 9.0F;
		this.rightLid.z = 1.0F;
		this.rightLatch = new ModelPart(64, 64, 0, 0);
		this.rightLatch.addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.rightLatch.y = 8.0F;
		this.leftBottom = new ModelPart(64, 64, 0, 19);
		this.leftBottom.addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.leftLid = new ModelPart(64, 64, 0, 0);
		this.leftLid.addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.leftLid.y = 9.0F;
		this.leftLid.z = 1.0F;
		this.leftLatch = new ModelPart(64, 64, 0, 0);
		this.leftLatch.addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.leftLatch.y = 8.0F;
	}

	@SuppressWarnings("rawtypes")
	public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Level world = tileEntityIn.getLevel();
		boolean flag = world != null;
		BlockState blockstate = flag ? tileEntityIn.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
		ChestType chesttype = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
		Block block = blockstate.getBlock();
		if (block instanceof AbstractChestBlock) {
			AbstractChestBlock<?> abstractchestblock = (AbstractChestBlock) block;
			boolean flag1 = chesttype != ChestType.SINGLE;
			matrixStackIn.pushPose();
			float f = blockstate.getValue(ChestBlock.FACING).toYRot();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-f));
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
			DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> icallbackwrapper;
			if (flag) {
				icallbackwrapper = abstractchestblock.combine(blockstate, world, tileEntityIn.getBlockPos(), true);
			} else {
				icallbackwrapper = DoubleBlockCombiner.Combiner::acceptNone;
			}
			
			float f1 = icallbackwrapper.apply(ChestBlock.opennessCombiner(tileEntityIn)).get(partialTicks);
			f1 = 1.0F - f1;
			f1 = 1.0F - f1 * f1 * f1;
			int i = icallbackwrapper.apply(new BrightnessCombiner<>()).applyAsInt(combinedLightIn);
			VertexConsumer ivertexbuilder = this.getChestMaterial(tileEntityIn, chesttype).buffer(bufferIn, RenderType::entityCutout);
			if (flag1) {
				if (chesttype == ChestType.LEFT) {
					this.render(matrixStackIn, ivertexbuilder, this.leftLid, this.leftLatch, this.leftBottom, f1, i, combinedOverlayIn);
				} else {
					this.render(matrixStackIn, ivertexbuilder, this.rightLid, this.rightLatch, this.rightBottom, f1, i, combinedOverlayIn);
				}
			} else {
				this.render(matrixStackIn, ivertexbuilder, this.singleLid, this.singleLatch, this.singleBottom, f1, i, combinedOverlayIn);
			}

			matrixStackIn.popPose();
		}
	}

	public Material getChestMaterial(T t, ChestType type) {
		if (this.isChristmas) {
			switch (type) {
				default:
				case SINGLE:	return Sheets.CHEST_XMAS_LOCATION;
				case LEFT: 		return Sheets.CHEST_XMAS_LOCATION_LEFT;
				case RIGHT: 	return Sheets.CHEST_XMAS_LOCATION_RIGHT;
			}
		} else {
			Block inventoryBlock = itemBlock;
			if (inventoryBlock == null) inventoryBlock = t.getBlockState().getBlock();
			ChestManager.ChestInfo chestInfo = ChestManager.getInfoForChest(((IChestBlock) inventoryBlock).getChestType());
			switch (type) {
				default:
				case SINGLE:
					return chestInfo != null ? chestInfo.getSingleMaterial() : Sheets.CHEST_LOCATION;
				case LEFT:
					return chestInfo != null ? chestInfo.getLeftMaterial() : Sheets.CHEST_LOCATION_LEFT;
				case RIGHT:
					return chestInfo != null ? chestInfo.getRightMaterial() : Sheets.CHEST_LOCATION_RIGHT;
			}
		}
	}

	public void render(PoseStack matrixStack, VertexConsumer builder, ModelPart chestLid, ModelPart chestLatch, ModelPart chestBottom, float lidAngle, int combinedLightIn, int combinedOverlayIn) {
		chestLid.xRot = -(lidAngle * ((float)Math.PI / 2F));
		chestLatch.xRot = chestLid.xRot;
		chestLid.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		chestLatch.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		chestBottom.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
	}
}