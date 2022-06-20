package com.teamabnormals.blueprint.core.registry;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registry class for Blueprint's custom boat system.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BoatTypeRegistry {
	private static final Map<String, BoatTypeData> BOATS = Util.make(Maps.newHashMap(), (entries) -> {
		entries.put("minecraft:oak", new BoatTypeData(() -> Items.OAK_BOAT, () -> Items.OAK_CHEST_BOAT, () -> Blocks.OAK_PLANKS, "minecraft:oak"));
	});

	/**
	 * Registers a new {@link BoatTypeData} using a given name, boat item, and plank item.
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param boatName A registry name for the {@link BoatTypeData}.
	 * @param boat     A boat item.
	 * @param chestBoat A chest boat item.
	 * @param plank    A planks item.
	 */
	public static synchronized void registerBoat(String boatName, Supplier<Item> boat, Supplier<Item> chestBoat, Supplier<Block> plank) {
		BOATS.put(boatName, new BoatTypeData(boat, chestBoat, plank, boatName));
	}

	/**
	 * Gets the {@link BoatTypeData} for a given name, or null if the given name is not in the registry.
	 *
	 * @param boatName A name to look up.
	 * @return The {@link BoatTypeData} for a given name, or null if the given name is not in the registry.
	 */
	@Nullable
	public static BoatTypeData getTypeData(String boatName) {
		return BOATS.get(boatName);
	}

	/**
	 * Gets serializing name for a given {@link BoatTypeData}.
	 *
	 * @param data A {@link BoatTypeData} instance to process.
	 * @return The serializing name for a given {@link BoatTypeData}.
	 */
	public static String getNameForData(BoatTypeData data) {
		for (Map.Entry<String, BoatTypeData> entries : BOATS.entrySet()) {
			if (entries.getValue().equals(data)) {
				return entries.getKey();
			}
		}
		return getBaseBoatName();
	}

	/**
	 * Gets the base (first) boat name.
	 * <p>This is equivalent to the first (fallback) name in the registry if no custom boats exist.</p>
	 * <p>If custom boats do exist, this will return the second (first non-fallback) name in the registry.</p>
	 *
	 * @return The base (first) boat name.
	 */
	public static String getBaseBoatName() {
		return BOATS.size() > 1 ? (String) BOATS.keySet().toArray()[1] : "minecraft:oak";
	}

	/**
	 * A class representing the needed information about a custom boat type.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class BoatTypeData {
		private final Supplier<Item> boat;
		private final Supplier<Item> chestBoat;
		private final Supplier<Block> plank;
		private final ResourceLocation texture;
		private final ResourceLocation chestVariantTexture;

		public BoatTypeData(Supplier<Item> boat, Supplier<Item> chestBoat, Supplier<Block> plank, String texture) {
			this.boat = boat;
			this.chestBoat = chestBoat;
			this.plank = plank;
			String modId = findModId(texture);
			String wood = findWood(texture);
			this.texture = new ResourceLocation(modId, "textures/entity/boat/" + wood + ".png");
			this.chestVariantTexture = new ResourceLocation(modId, "textures/entity/chest_boat/" + wood + ".png");
		}

		/**
		 * Gets the value of {@link #boat} in this data.
		 *
		 * @return The value of {@link #boat} in this data.
		 */
		public Item getBoatItem() {
			return this.boat.get();
		}

		/**
		 * Gets the value of {@link #chestBoat} in this data.
		 *
		 * @return The value of {@link #chestBoat} in this data.
		 */
		public Item getChestBoatItem() {
			return this.chestBoat.get();
		}

		/**
		 * Gets the value of {@link #plank} as an item in this data.
		 *
		 * @return The value of {@link #plank} as an item in this data.
		 */
		public Item getPlankItem() {
			return this.plank.get().asItem();
		}

		/**
		 * Gets the {@link #texture} in this data.
		 *
		 * @return The {@link #texture} in this data.
		 */
		public ResourceLocation getTexture() {
			return this.texture;
		}

		/**
		 * Gets the {@link #chestVariantTexture} in this data.
		 *
		 * @return The {@link #chestVariantTexture} in this data.
		 */
		public ResourceLocation getChestVariantTexture() {
			return this.chestVariantTexture;
		}

		private static String findModId(String parentString) {
			StringBuilder builder = new StringBuilder();
			for (char parentChars : parentString.toCharArray()) {
				if (parentChars == ':') {
					break;
				}
				builder.append(parentChars);
			}
			return builder.toString();
		}

		private static String findWood(String parentString) {
			StringBuilder builder = new StringBuilder();
			boolean start = false;
			for (char parentChars : parentString.toCharArray()) {
				if (start) {
					builder.append(parentChars);
				}

				if (parentChars == ':') {
					start = true;
				}
			}
			return builder.toString();
		}
	}
}