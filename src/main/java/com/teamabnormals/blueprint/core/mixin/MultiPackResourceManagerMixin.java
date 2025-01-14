package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.common.remolder.RemoldableResourceManager;
import com.teamabnormals.blueprint.common.remolder.RemolderLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(MultiPackResourceManager.class)
public abstract class MultiPackResourceManagerMixin implements RemoldableResourceManager {
    private RemolderLoader remolderLoader;

    @Inject(method = "getResource", at = @At("RETURN"), cancellable = true)
    private void getRemoldedResource(ResourceLocation location, CallbackInfoReturnable<Optional<Resource>> info) {
        if (remolderLoader == null) return;
        info.setReturnValue(this.remolderLoader.getResource(location, info.getReturnValue()));
    }

    @Inject(method = "getResourceStack", at = @At("RETURN"), cancellable = true)
    private void getRemoldedResourceStack(ResourceLocation location, CallbackInfoReturnable<List<Resource>> info) {
        if (remolderLoader == null) return;
        info.setReturnValue(this.remolderLoader.getResourceStack(info.getReturnValue(), location));
    }

    @Inject(method = "listResources", at = @At("RETURN"), cancellable = true)
    private void listRemoldedResources(String path, Predicate<ResourceLocation> filter, CallbackInfoReturnable<Map<ResourceLocation, Resource>> info) {
        if (remolderLoader == null) return;
        info.setReturnValue(this.remolderLoader.listResources(info.getReturnValue()));
    }

    @Inject(method = "listResourceStacks", at = @At("RETURN"), cancellable = true)
    private void listRemoldedResourceStacks(String path, Predicate<ResourceLocation> filter, CallbackInfoReturnable<Map<ResourceLocation, List<Resource>>> info) {
        if (remolderLoader == null) return;
        info.setReturnValue(this.remolderLoader.listResourceStacks(info.getReturnValue()));
    }

    @Override
    public RemolderLoader updateRemolderLoader(PackType packType, boolean needsAutoReload) {
        return this.remolderLoader = new RemolderLoader(this, packType, needsAutoReload);
    }

    @Override
    public RemolderLoader getRemolderLoader() {
        return this.remolderLoader;
    }
}
