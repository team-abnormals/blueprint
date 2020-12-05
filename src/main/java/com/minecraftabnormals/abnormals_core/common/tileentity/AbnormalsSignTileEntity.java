package com.minecraftabnormals.abnormals_core.common.tileentity;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.minecraftabnormals.abnormals_core.core.registry.ACTileEntities;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AbnormalsSignTileEntity extends TileEntity {
	public final ITextComponent[] signText = new ITextComponent[]{new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent("")};
	private boolean isEditable = true;
	private PlayerEntity player;
	private final IReorderingProcessor[] renderText = new IReorderingProcessor[4];
	private DyeColor textColor = DyeColor.BLACK;

	public AbnormalsSignTileEntity() {
		super(ACTileEntities.SIGN.get());
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

		for (int i = 0; i < 4; ++i) {
			String s = ITextComponent.Serializer.toJson(this.signText[i]);
			compound.putString("Text" + (i + 1), s);
		}

		compound.putString("Color", this.textColor.getTranslationKey());
		return compound;
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		super.read(state, compound);
		this.isEditable = false;
		this.textColor = DyeColor.byTranslationKey(compound.getString("Color"), DyeColor.BLACK);

		for (int i = 0; i < 4; ++i) {
			String s = compound.getString("Text" + (i + 1));
			ITextComponent itextcomponent = ITextComponent.Serializer.getComponentFromJson(s.isEmpty() ? "\"\"" : s);
			if (this.world instanceof ServerWorld) {
				try {
					this.signText[i] = TextComponentUtils.func_240645_a_(this.getCommandSource((ServerPlayerEntity) null), itextcomponent, (Entity) null, 0);
				} catch (CommandSyntaxException var6) {
					this.signText[i] = itextcomponent;
				}
			} else {
				this.signText[i] = itextcomponent;
			}

			this.renderText[i] = null;
		}
	}


	public ITextComponent getText(int line) {
		return this.signText[line];
	}

	public void setText(int line, ITextComponent p_212365_2_) {
		this.signText[line] = p_212365_2_;
		this.renderText[line] = null;
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public IReorderingProcessor getRenderText(int line, Function<ITextComponent, IReorderingProcessor> function) {
		if (this.renderText[line] == null && this.signText[line] != null) {
			this.renderText[line] = function.apply(this.signText[line]);
		}

		return this.renderText[line];
	}

	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 100, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public boolean onlyOpsCanSetNbt() {
		return true;
	}

	public boolean getIsEditable() {
		return this.isEditable;
	}

	@OnlyIn(Dist.CLIENT)
	public void setEditable(boolean isEditableIn) {
		this.isEditable = isEditableIn;
		if (!isEditableIn) {
			this.player = null;
		}
	}

	public void setPlayer(PlayerEntity playerIn) {
		this.player = playerIn;
	}

	public PlayerEntity getPlayer() {
		return this.player;
	}

	public boolean executeCommand(PlayerEntity playerIn) {
		for (ITextComponent itextcomponent : this.signText) {
			Style style = itextcomponent == null ? null : itextcomponent.getStyle();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickevent = style.getClickEvent();
				if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					playerIn.getServer().getCommandManager().handleCommand(this.getCommandSource((ServerPlayerEntity) playerIn), clickevent.getValue());
				}
			}
		}

		return true;
	}

	public CommandSource getCommandSource(@Nullable ServerPlayerEntity playerIn) {
		String s = playerIn == null ? "Sign" : playerIn.getName().getString();
		ITextComponent itextcomponent = (ITextComponent) (playerIn == null ? new StringTextComponent("Sign") : playerIn.getDisplayName());
		return new CommandSource(ICommandSource.DUMMY, new Vector3d((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D), Vector2f.ZERO, (ServerWorld) this.world, 2, s, itextcomponent, this.world.getServer(), playerIn);
	}

	public DyeColor getTextColor() {
		return this.textColor;
	}

	public boolean setTextColor(DyeColor newColor) {
		if (newColor != this.getTextColor()) {
			this.textColor = newColor;
			this.markDirty();
			this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
			return true;
		} else {
			return false;
		}
	}
}