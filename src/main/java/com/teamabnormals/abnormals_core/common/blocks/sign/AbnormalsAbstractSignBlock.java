package com.teamabnormals.abnormals_core.common.blocks.sign;

import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import com.teamabnormals.abnormals_core.core.config.ACConfig;
import com.teamabnormals.abnormals_core.core.util.NetworkUtil;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class AbnormalsAbstractSignBlock extends AbstractSignBlock {
	private final ResourceLocation textureLocation;

	public AbnormalsAbstractSignBlock(Properties properties, ResourceLocation textureLocation) {
		super(properties, null);
		this.textureLocation = textureLocation;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack itemstack = player.getHeldItem(handIn);
		boolean canEdit = player.abilities.allowEdit;
		boolean canDye = itemstack.getItem() instanceof DyeItem && canEdit;
		if (worldIn.isRemote) {
			return canDye ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof AbnormalsSignTileEntity) {
				AbnormalsSignTileEntity signtileentity = (AbnormalsSignTileEntity) tileentity;
				if (canDye) {
					boolean tryToSetColor = signtileentity.setTextColor(((DyeItem) itemstack.getItem()).getDyeColor());
					if (tryToSetColor) {
						NetworkUtil.updateSignText(pos, signtileentity.getText(0), signtileentity.getText(1), signtileentity.getText(2), signtileentity.getText(3), signtileentity.getTextColor());
						if (!player.isCreative()) {
							itemstack.shrink(1);
						}
					}
				} else {
					if (canEdit && !this.doesSignHaveCommand(signtileentity) && ACConfig.ValuesHolder.isQuarkSignEditingEnabled() && (!ACConfig.ValuesHolder.doesSignEditingRequireEmptyHand() || itemstack.isEmpty()) && !player.isSneaking()) {
						NetworkUtil.openSignEditor(player, signtileentity);
						return ActionResultType.SUCCESS;
					}
				}
				return signtileentity.executeCommand(player) ? ActionResultType.SUCCESS : ActionResultType.PASS;
			} else {
				return ActionResultType.PASS;
			}
		}
	}

	private boolean doesSignHaveCommand(AbnormalsSignTileEntity sign) {
		for (ITextComponent itextcomponent : sign.signText) {
			Style style = itextcomponent == null ? null : itextcomponent.getStyle();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickevent = style.getClickEvent();
				if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					return true;
				}
			}
		}
		return false;
	}

	public ResourceLocation getTextureLocation() {
		return this.textureLocation;
	}

	public TileEntity createNewTileEntity(IBlockReader world) {
		return new AbnormalsSignTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}