package com.teamabnormals.blueprint.common.world.storage;

import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Handles the reading and writing of registered {@link GlobalStorage}s.
 *
 * @author SmellyModder (Luke Tonon)
 * @see SavedData
 * @see GlobalStorage
 */
public final class GlobalStorageManager extends SavedData {
	private static final GlobalStorageManager INSTANCE = new GlobalStorageManager();
	private static final String KEY = Blueprint.MOD_ID + "_storage";
	private static boolean loaded = false;

	private GlobalStorageManager() {
		super();
	}

	public static GlobalStorageManager getOrCreate(ServerLevel world) {
		return world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(() -> INSTANCE, (compound, provider) -> {
			loaded = true;
			ListTag storageTags = compound.getList("storages", Tag.TAG_COMPOUND);

			for (int i = 0; i < storageTags.size(); i++) {
				CompoundTag storageTag = storageTags.getCompound(i);
				GlobalStorage storage = GlobalStorage.STORAGES.get(ResourceLocation.parse(storageTag.getString("id")));
				if (storage != null) {
					storage.fromTag(storageTag);
				}
			}
			return INSTANCE;
		}), KEY);
	}

	public static boolean isLoaded() {
		return loaded;
	}

	@Override
	public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
		ListTag storageList = new ListTag();
		GlobalStorage.STORAGES.forEach((key, value) -> {
			CompoundTag storageTag = value.toTag();
			storageTag.putString("id", key.toString());
			storageList.add(storageTag);
		});
		compound.put("storages", storageList);
		return compound;
	}

	@Override
	public boolean isDirty() {
		return true;
	}
}
