package com.teamabnormals.abnormals_core.common.world.storage;

import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

/**
 * Handles saving registered {@link GlobalStorage}s.
 *
 * @author SmellyModder (Luke Tonon)
 * @see net.minecraft.world.storage.WorldSavedData
 */
public final class GlobalStorageManager extends WorldSavedData {
	private static final String KEY = AbnormalsCore.MODID + "_storage";
	private static boolean loaded = false;

	private GlobalStorageManager() {
		super(KEY);
	}

	public static GlobalStorageManager getOrCreate(ServerWorld world) {
		return world.getSavedData().getOrCreate(GlobalStorageManager::new, KEY);
	}

	public static boolean isLoaded() {
		return loaded;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		ListNBT storageList = new ListNBT();
		GlobalStorage.STORAGES.forEach((key, value) -> {
			CompoundNBT storageTag = value.toTag();
			storageTag.putString("id", key.toString());
			storageList.add(storageTag);
		});
		compound.put("storages", storageList);
		return compound;
	}

	@Override
	public void read(CompoundNBT compound) {
		loaded = true;
		ListNBT storageTags = compound.getList("storages", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < storageTags.size(); i++) {
			CompoundNBT storageTag = storageTags.getCompound(i);
			GlobalStorage storage = GlobalStorage.STORAGES.get(new ResourceLocation(storageTag.getString("id")));
			if (storage != null) {
				storage.fromTag(storageTag);
			}
		}
	}

	@Override
	public boolean isDirty() {
		return true;
	}
}
