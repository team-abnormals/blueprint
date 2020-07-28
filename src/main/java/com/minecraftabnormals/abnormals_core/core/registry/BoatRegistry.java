package com.minecraftabnormals.abnormals_core.core.registry;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.minecraftabnormals.abnormals_core.core.registry.BoatRegistry.BoatData.DefaultBoatData;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.fml.RegistryObject;

public class BoatRegistry {
	private static final Map<String, BoatData> BOATS = Util.make(Maps.newHashMap(), (entries) -> {
		entries.put("minecraft:oak", new DefaultBoatData());
	});
	
	public static synchronized void registerBoat(String boatName, RegistryObject<Item> boat, RegistryObject<Block> plank) {
		BOATS.put(boatName, new BoatData(boat, plank, boatName));
	}
	
	@Nullable
	public static BoatData getDataForBoat(String boatName) {
		return BOATS.get(boatName);
	}
	
	public static String getNameForData(BoatData data) {
		for(Map.Entry<String, BoatData> entries : BOATS.entrySet()) {
			if(entries.getValue().equals(data)) {
				return entries.getKey();
			}
		}
		return getBaseBoatName();
	}
	
	public static String getBaseBoatName() {
		return BOATS.size() > 1 ? (String) BOATS.keySet().toArray()[1] : "minecraft:oak";
	}
	
	public static class BoatData {
		private final RegistryObject<Item> boat;
		private final RegistryObject<Block> plank;
		private final ResourceLocation texture;
		
		public BoatData(RegistryObject<Item> boat, RegistryObject<Block> plank, String texture) {
			this.boat = boat;
			this.plank = plank;
			this.texture = this.processTexture(texture);
		}
		
		public Item getBoatItem() {
			return this.boat.get();
		}
		
		public Item getPlankItem() {
			return this.plank.get().asItem();
		}
		
		public ResourceLocation getTexture() {
			return this.texture;
		}
		
		private ResourceLocation processTexture(String texture) {
			String parentString = texture;
			String modId = findModId(parentString);
			String wood = findWood(parentString);
			return new ResourceLocation(modId, "textures/entity/boat/" + wood + ".png");
		}
		
		private String findModId(String parentString) {
			StringBuilder builder = new StringBuilder();
			for(char parentChars : parentString.toCharArray()) {
				if(parentChars == ':') {
					break;
				}
				builder.append(parentChars);
			}
			return builder.toString();
		}
		
		private String findWood(String parentString) {
			StringBuilder builder = new StringBuilder();
			boolean start = false;
			for(char parentChars : parentString.toCharArray()) {
				if(start) {
					builder.append(parentChars);
				}
				
				if(parentChars == ':') {
					start = true;
				}
			}
			return builder.toString();
		}
		
		public static class DefaultBoatData extends BoatData {
			
			public DefaultBoatData() {
				super(null, null, "minecraft:oak");
			}
			
			@Override
			public Item getBoatItem() {
				return Items.OAK_BOAT;
			}
			
			@Override
			public Item getPlankItem() {
				return Items.OAK_PLANKS.asItem();
			}
			
		}
	}
}