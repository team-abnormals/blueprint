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
public final class BoatRegistry {
	private static final Map<String, BoatData> BOATS = Util.make(Maps.newHashMap(), (entries) -> {
		entries.put("minecraft:oak", new BoatData(() -> Items.OAK_BOAT, () -> Blocks.OAK_PLANKS, "minecraft:oak"));
	});

	/**
	 * Registers a new {@link BoatData} using a given name, boat item, and plank item.
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param boatName A registry name for the {@link BoatData}.
	 * @param boat     A boat item.
	 * @param plank    A planks item.
	 */
	public static synchronized void registerBoat(String boatName, Supplier<Item> boat, Supplier<Block> plank) {
		BOATS.put(boatName, new BoatData(boat, plank, boatName));
	}

	/**
	 * Gets the {@link BoatData} for a given name, or null if the given name is not in the registry.
	 *
	 * @param boatName A name to look up.
	 * @return The {@link BoatData} for a given name, or null if the given name is not in the registry.
	 */
	@Nullable
	public static BoatData getDataForBoat(String boatName) {
		return BOATS.get(boatName);
	}

	/**
	 * Gets serializing name for a given {@link BoatData}.
	 *
	 * @param data A {@link BoatData} instance to process.
	 * @return The serializing name for a given {@link BoatData}.
	 */
	public static String getNameForData(BoatData data) {
		for (Map.Entry<String, BoatData> entries : BOATS.entrySet()) {
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
	public static class BoatData {
		private final Supplier<Item> boat;
		private final Supplier<Block> plank;
		private final ResourceLocation texture;

		public BoatData(Supplier<Item> boat, Supplier<Block> plank, String texture) {
			this.boat = boat;
			this.plank = plank;
			this.texture = this.processTexture(texture);
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

		private ResourceLocation processTexture(String texture) {
			String modId = findModId(texture);
			String wood = findWood(texture);
			return new ResourceLocation(modId, "textures/entity/boat/" + wood + ".png");
		}

		private String findModId(String parentString) {
			StringBuilder builder = new StringBuilder();
			for (char parentChars : parentString.toCharArray()) {
				if (parentChars == ':') {
					break;
				}
				builder.append(parentChars);
			}
			return builder.toString();
		}

		private String findWood(String parentString) {
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