package com.teamabnormals.blueprint.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.teamabnormals.blueprint.client.BlueprintChestMaterials;
import com.teamabnormals.blueprint.client.BlueprintChestMaterials.ChestMaterials;
import com.teamabnormals.blueprint.core.api.IChestBlock;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.Calendar;

/**
 * The {@link BlockEntityRenderer} responsible for the rendering of Blueprint's chests.
 *
 * @param <T> The type of {@link LidBlockEntity} the renderer is for.
 */
public class BlueprintChestBlockEntityRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T> {
	public static Block itemBlock = null;

	private final ModelPart lid;
	private final ModelPart bottom;
	private final ModelPart lock;
	private final ModelPart doubleLeftLid;
	private final ModelPart doubleLeftBottom;
	private final ModelPart doubleLeftLock;
	private final ModelPart doubleRightLid;
	private final ModelPart doubleRightBottom;
	private final ModelPart doubleRightLock;
	public boolean isChristmas;

	public BlueprintChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26) {
			this.isChristmas = true;
		}
		ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
		this.bottom = modelpart.getChild("bottom");
		this.lid = modelpart.getChild("lid");
		this.lock = modelpart.getChild("lock");
		ModelPart modelpart1 = context.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
		this.doubleLeftBottom = modelpart1.getChild("bottom");
		this.doubleLeftLid = modelpart1.getChild("lid");
		this.doubleLeftLock = modelpart1.getChild("lock");
		ModelPart modelpart2 = context.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
		this.doubleRightBottom = modelpart2.getChild("bottom");
		this.doubleRightLid = modelpart2.getChild("lid");
		this.doubleRightLock = modelpart2.getChild("lock");
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
			matrixStackIn.mulPose(Axis.YP.rotationDegrees(-f));
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
					this.render(matrixStackIn, ivertexbuilder, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, f1, i, combinedOverlayIn);
				} else {
					this.render(matrixStackIn, ivertexbuilder, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, f1, i, combinedOverlayIn);
				}
			} else {
				this.render(matrixStackIn, ivertexbuilder, this.lid, this.lock, this.bottom, f1, i, combinedOverlayIn);
			}

			matrixStackIn.popPose();
		}
	}

	public Material getChestMaterial(T t, ChestType type) {
		if (this.isChristmas) {
			return switch (type) {
				case SINGLE -> Sheets.CHEST_XMAS_LOCATION;
				case LEFT -> Sheets.CHEST_XMAS_LOCATION_LEFT;
				case RIGHT -> Sheets.CHEST_XMAS_LOCATION_RIGHT;
			};
		} else {
			Block inventoryBlock = itemBlock;
			if (inventoryBlock == null) inventoryBlock = t.getBlockState().getBlock();
			ChestMaterials chestMaterials = BlueprintChestMaterials.getMaterials(((IChestBlock) inventoryBlock).getChestMaterialsName());
			return switch (type) {
				case SINGLE -> chestMaterials != null ? chestMaterials.singleMaterial() : Sheets.CHEST_LOCATION;
				case LEFT -> chestMaterials != null ? chestMaterials.leftMaterial() : Sheets.CHEST_LOCATION_LEFT;
				case RIGHT -> chestMaterials != null ? chestMaterials.rightMaterial() : Sheets.CHEST_LOCATION_RIGHT;
			};
		}
	}

	public void render(PoseStack matrixStack, VertexConsumer builder, ModelPart chestLid, ModelPart chestLatch, ModelPart chestBottom, float lidAngle, int combinedLightIn, int combinedOverlayIn) {
		chestLid.xRot = -(lidAngle * ((float) Math.PI / 2F));
		chestLatch.xRot = chestLid.xRot;
		chestLid.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		chestLatch.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		chestBottom.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
	}
}