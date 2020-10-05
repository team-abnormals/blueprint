package com.teamabnormals.abnormals_core.common.blocks.sign;

import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

public abstract class AbnormalsAbstractSignBlock extends AbstractSignBlock implements ITexturedSign {
	private final ResourceLocation textureLocation;

	public AbnormalsAbstractSignBlock(Properties properties, ResourceLocation textureLocation) {
		super(properties, null);
		this.textureLocation = textureLocation;
	}

	@Override
	public ResourceLocation getTextureLocation() {
		return this.textureLocation;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new AbnormalsSignTileEntity();
	}
}