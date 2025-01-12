package com.teamabnormals.blueprint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.BiFunction;

public final class MemoizedBEWLR {
    private final BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> factory;
    private BlockEntityWithoutLevelRenderer bewlr = null;

    public MemoizedBEWLR(BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> factory) {
        this.factory = factory;
    }

    public static IClientItemExtensions asCustomItemRenderer(BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> factory) {
        MemoizedBEWLR memoizedBEWLR = new MemoizedBEWLR(factory);
        return new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return memoizedBEWLR.get();
            }
        };
    }

    public BlockEntityWithoutLevelRenderer get() {
        return this.bewlr != null ? this.bewlr : (this.bewlr = this.factory.apply(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));
    }
}
