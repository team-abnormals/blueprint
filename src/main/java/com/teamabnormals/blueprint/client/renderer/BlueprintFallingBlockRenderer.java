package com.teamabnormals.blueprint.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamabnormals.blueprint.common.entity.BlueprintFallingBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The {@link EntityRenderer} responsible for the rendering of Blueprint's falling block entities.
 */
@OnlyIn(Dist.CLIENT)
public class BlueprintFallingBlockRenderer<E extends BlueprintFallingBlockEntity> extends EntityRenderer<E> {
	private final BlockRenderDispatcher dispatcher;

	public BlueprintFallingBlockRenderer(EntityRendererProvider.Context p_174112_) {
		super(p_174112_);
		this.shadowRadius = 0.5F;
		this.dispatcher = p_174112_.getBlockRenderDispatcher();
	}

	@Override
	public void render(E entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLightIn) {
		BlockState blockstate = entity.getBlockState();
		if (blockstate.getRenderShape() == RenderShape.MODEL) {
			Level level = entity.getLevel();
			if (blockstate != level.getBlockState(entity.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
				poseStack.pushPose();
				BlockPos blockpos = new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
				poseStack.translate(-0.5D, 0.0D, -0.5D);
				var model = this.dispatcher.getBlockModel(blockstate);
				for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(entity.getStartPos())), net.minecraftforge.client.model.data.ModelData.EMPTY))
					this.dispatcher.getModelRenderer().tesselateBlock(level, model, blockstate, blockpos, poseStack, source.getBuffer(renderType), false, RandomSource.create(), blockstate.getSeed(entity.getStartPos()), OverlayTexture.NO_OVERLAY, net.minecraftforge.client.model.data.ModelData.EMPTY, renderType);
				poseStack.popPose();
				super.render(entity, entityYaw, partialTicks, poseStack, source, packedLightIn);
			}
		}
	}

	@Override
	public ResourceLocation getTextureLocation(E entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}