package com.teamabnormals.blueprint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public final class MemoizedBEWLR {
	private final Factory factory;
	private BlockEntityWithoutLevelRenderer bewlr = null;

	public MemoizedBEWLR(Factory factory) {
		this.factory = factory;
	}

	public static IClientItemExtensions asCustomItemRenderer(Factory factory) {
		MemoizedBEWLR memoizedBEWLR = new MemoizedBEWLR(factory);
		return new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return memoizedBEWLR.get();
			}
		};
	}

	public BlockEntityWithoutLevelRenderer get() {
		return this.bewlr != null ? this.bewlr : (this.bewlr = this.factory.create(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));
	}

	public interface Factory {
		BlockEntityWithoutLevelRenderer create(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet);
	}
}
