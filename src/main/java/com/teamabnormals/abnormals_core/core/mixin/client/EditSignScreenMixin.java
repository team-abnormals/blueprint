package com.teamabnormals.abnormals_core.core.mixin.client;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamabnormals.abnormals_core.common.blocks.sign.ITexturedSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(EditSignScreen.class)
public class EditSignScreenMixin {

	@Shadow @Final private SignTileEntity tileSign;
	@Shadow @Final private SignTileEntityRenderer.SignModel signModel;

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/tileentity/SignTileEntityRenderer;getMaterial(Lnet/minecraft/block/Block;)Lnet/minecraft/client/renderer/model/RenderMaterial;"))
	public RenderMaterial changeTexture(Block blockIn) {
		if(blockIn instanceof ITexturedSign)
			return Atlases.getSignMaterial(WoodType.OAK);

		return SignTileEntityRenderer.getMaterial(blockIn);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/model/RenderMaterial;getBuffer(Lnet/minecraft/client/renderer/IRenderTypeBuffer;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/IVertexBuilder;"))
	public IVertexBuilder changeTexture(RenderMaterial renderMaterial, IRenderTypeBuffer bufferIn, Function<ResourceLocation, RenderType> renderTypeGetter) {
		BlockState state = this.tileSign.getBlockState();
		if(state.getBlock() instanceof ITexturedSign)
			return bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(getTexture(state)));

		return renderMaterial.getBuffer(bufferIn, this.signModel::getRenderType);
	}

	private static ResourceLocation getTexture(BlockState state) {
		return ((ITexturedSign) state.getBlock()).getTextureLocation();
	}
}
