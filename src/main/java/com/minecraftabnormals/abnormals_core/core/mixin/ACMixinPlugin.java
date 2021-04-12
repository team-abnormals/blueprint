package com.minecraftabnormals.abnormals_core.core.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ACMixinPlugin implements IMixinConfigPlugin {
    private boolean chlorineLoaded;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            Class.forName("dev.hanetzer.chlorine.common.Chlorine"); // Checks if Chlorine's main class is available since mods haven't loaded at this stage
            this.chlorineLoaded = true;
        } catch (ClassNotFoundException ignored) {}
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return chlorineLoaded ? !mixinClassName.equals("com.minecraftabnormals.abnormals_core.core.mixin.compat.client.ClientWorldVanillaMixin") : !mixinClassName.equals("com.minecraftabnormals.abnormals_core.core.mixin.compat.client.ClientWorldChlorineMixin");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
