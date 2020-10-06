package com.teamabnormals.abnormals_core.common.blocks.sign;

import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

public class AbnormalsWallSignBlock extends WallSignBlock implements ITexturedSign {
	private final ResourceLocation texture;

	public AbnormalsWallSignBlock(AbstractBlock.Properties properties, ResourceLocation texture) {
		super(properties, null);
		this.texture = texture;
	}

	@Override
	public ResourceLocation getTextureLocation() {
		return this.texture;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new AbnormalsSignTileEntity();
	}
}