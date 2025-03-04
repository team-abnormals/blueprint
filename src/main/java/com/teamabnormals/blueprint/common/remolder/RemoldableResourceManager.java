package com.teamabnormals.blueprint.common.remolder;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;

public interface RemoldableResourceManager extends CloseableResourceManager {
    RemolderLoader updateRemolderLoader(PackType packType);

    RemolderLoader getRemolderLoader();
}
