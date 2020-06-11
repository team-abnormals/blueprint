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
import net.minecraft.client.renderer.Vector3f;
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
import net.minecraft.world.World;

public class AbnormalsChestTileEntityRenderer<T extends TileEntity & IChestLid> extends TileEntityRenderer<T> {
	
	public static Block itemBlock = null; 
	
	public final ModelRenderer field_228862_a_;
	public final ModelRenderer field_228863_c_;
	public final ModelRenderer field_228864_d_;
	public final ModelRenderer field_228865_e_;
	public final ModelRenderer field_228866_f_;
	public final ModelRenderer field_228867_g_;
	public final ModelRenderer field_228868_h_;
	public final ModelRenderer field_228869_i_;
	public final ModelRenderer field_228870_j_;
	public boolean isChristmas;

	public AbnormalsChestTileEntityRenderer(TileEntityRendererDispatcher p_i226008_1_) {
		super(p_i226008_1_);
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.isChristmas = true;
		}

		this.field_228863_c_ = new ModelRenderer(64, 64, 0, 19);
		this.field_228863_c_.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.field_228862_a_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228862_a_.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.field_228862_a_.rotationPointY = 9.0F;
		this.field_228862_a_.rotationPointZ = 1.0F;
		this.field_228864_d_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228864_d_.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.field_228864_d_.rotationPointY = 8.0F;
		this.field_228866_f_ = new ModelRenderer(64, 64, 0, 19);
		this.field_228866_f_.addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.field_228865_e_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228865_e_.addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.field_228865_e_.rotationPointY = 9.0F;
		this.field_228865_e_.rotationPointZ = 1.0F;
		this.field_228867_g_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228867_g_.addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.field_228867_g_.rotationPointY = 8.0F;
		this.field_228869_i_ = new ModelRenderer(64, 64, 0, 19);
		this.field_228869_i_.addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.field_228868_h_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228868_h_.addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.field_228868_h_.rotationPointY = 9.0F;
		this.field_228868_h_.rotationPointZ = 1.0F;
		this.field_228870_j_ = new ModelRenderer(64, 64, 0, 0);
		this.field_228870_j_.addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.field_228870_j_.rotationPointY = 8.0F;
	}

	public void render(T tileEntity, float p_225616_2_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_) {
		World world = tileEntity.getWorld();
		boolean flag = world != null;
		BlockState blockstate = flag ? tileEntity.getBlockState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chesttype = blockstate.has(ChestBlock.TYPE) ? blockstate.get(ChestBlock.TYPE) : ChestType.SINGLE;
		Block block = blockstate.getBlock();
		if (block instanceof AbstractChestBlock) {
			@SuppressWarnings("rawtypes")
			AbstractChestBlock<?> abstractchestblock = (AbstractChestBlock) block;
			boolean flag1 = chesttype != ChestType.SINGLE;
			matrixStack.push();
			float f = blockstate.get(ChestBlock.FACING).getHorizontalAngle();
			matrixStack.translate(0.5D, 0.5D, 0.5D);
			matrixStack.rotate(Vector3f.YP.rotationDegrees(-f));
			matrixStack.translate(-0.5D, -0.5D, -0.5D);
			TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> icallbackwrapper;
			if (flag) {
				icallbackwrapper = abstractchestblock.func_225536_a_(blockstate, world, tileEntity.getPos(), true); // getBlockEntitySource
			} else {
				icallbackwrapper = TileEntityMerger.ICallback::func_225537_b_; // getFallback
			}
			
			float f1 = icallbackwrapper.apply(ChestBlock.func_226917_a_((IChestLid)tileEntity)).get(p_225616_2_);
			f1 = 1.0F - f1;
			f1 = 1.0F - f1 * f1 * f1;
			int i = icallbackwrapper.apply(new DualBrightnessCallback<>()).applyAsInt(p_225616_5_);
			IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.getEntityCutoutNoCull(this.getChestTexture(tileEntity, chesttype)));
			if (flag1) {
				if (chesttype == ChestType.LEFT) {
					this.func_228871_a_(matrixStack, ivertexbuilder, this.field_228868_h_, this.field_228870_j_, this.field_228869_i_, f1, i, p_225616_6_);
				} else {
					this.func_228871_a_(matrixStack, ivertexbuilder, this.field_228865_e_, this.field_228867_g_, this.field_228866_f_, f1, i, p_225616_6_);
				}
			} else {
				this.func_228871_a_(matrixStack, ivertexbuilder, this.field_228862_a_, this.field_228864_d_, this.field_228863_c_, f1, i, p_225616_6_);
			}

			matrixStack.pop();
		}
	}

	public ResourceLocation getChestTexture(T t, ChestType type) {
		Block inventoryBlock = itemBlock;
		if(inventoryBlock == null) inventoryBlock = t.getBlockState().getBlock();
		
		IChestBlock block = (IChestBlock) inventoryBlock;
		String chestType = block.isTrapped() ? "/trapped" : "/normal";
		
		switch(type) {
			case LEFT: return new ResourceLocation(block.getModid(), "textures/entity/chest/" + block.getChestName() + chestType +"_left.png");
			case RIGHT: return new ResourceLocation(block.getModid(), "textures/entity/chest/" + block.getChestName() + chestType + "_right.png");
			case SINGLE: default: return new ResourceLocation(block.getModid(), "textures/entity/chest/" + block.getChestName() + chestType +".png");
		}
	}

	public void func_228871_a_(MatrixStack matrixStack, IVertexBuilder builder, ModelRenderer p_228871_3_, ModelRenderer p_228871_4_, ModelRenderer p_228871_5_, float p_228871_6_, int p_228871_7_, int p_228871_8_) {
		p_228871_3_.rotateAngleX = -(p_228871_6_ * ((float)Math.PI / 2F));
		p_228871_4_.rotateAngleX = p_228871_3_.rotateAngleX;
		p_228871_3_.render(matrixStack, builder, p_228871_7_, p_228871_8_);
		p_228871_4_.render(matrixStack, builder, p_228871_7_, p_228871_8_);
		p_228871_5_.render(matrixStack, builder, p_228871_7_, p_228871_8_);
	}
}