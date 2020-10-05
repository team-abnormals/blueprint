package com.teamabnormals.abnormals_core.common.blocks.sign;

import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

public class AbnormalsStandingSignBlock extends StandingSignBlock implements ITexturedSign {
	private final ResourceLocation texture;

	public AbnormalsStandingSignBlock(AbstractBlock.Properties properties, ResourceLocation texture) {
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