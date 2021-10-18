package com.teamabnormals.blueprint.client;

import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.util.registry.BlockSubRegistryHelper;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for texture information for Abnormals Chests.
 *
 * @author SmellyModder (Luke Tonon)
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ChestManager {
	private static final Map<String, ChestInfo> CHEST_INFO_MAP = new HashMap<>();

	/**
	 * Puts a created {@link ChestInfo} onto the {@link #CHEST_INFO_MAP} for a given ID and type.
	 * <p>Called in chest related methods in {@link BlockSubRegistryHelper}</p>
	 *
	 * @param modId   Mod ID for the chest.
	 * @param type    Type for the chest. (e.g. "oak")
	 * @param trapped If the chest is trapped.
	 */
	public static synchronized void putChestInfo(String modId, String type, boolean trapped) {
		CHEST_INFO_MAP.put(modId + ":" + type + (trapped ? "_trapped" : ""), new ChestInfo(modId, type, trapped));
	}

	/**
	 * Gets the {@link ChestInfo} for a given chest type.
	 *
	 * @param chestType A string for the {@link ChestInfo} to lookup.
	 * @return The {@link ChestInfo} for the given chest type, or null if there is no {@link ChestInfo} for the given type.
	 */
	@Nullable
	public static ChestInfo getInfoForChest(String chestType) {
		return CHEST_INFO_MAP.get(chestType);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onStitch(TextureStitchEvent.Pre event) {
		if (event.getMap().location().equals(Sheets.CHEST_SHEET)) {
			for (ChestInfo chestInfo : CHEST_INFO_MAP.values()) {
				chestInfo.setup(event);
			}
		}
	}

	public static class ChestInfo {
		private final ResourceLocation single, left, right;
		@OnlyIn(Dist.CLIENT)
		private Material singleMaterial, leftMaterial, rightMaterial;

		public ChestInfo(String modId, String type, boolean trapped) {
			String chest = trapped ? "trapped" : "normal";
			this.single = new ResourceLocation(modId, "entity/chest/" + type + "/" + chest);
			this.left = new ResourceLocation(modId, "entity/chest/" + type + "/" + chest + "_left");
			this.right = new ResourceLocation(modId, "entity/chest/" + type + "/" + chest + "_right");
		}

		/**
		 * Adds the internal textures to the stitch event and initializes the {@link Material}s.
		 *
		 * @param event A {@link TextureStitchEvent.Pre} to setup this info from.
		 */
		@OnlyIn(Dist.CLIENT)
		private void setup(TextureStitchEvent.Pre event) {
			event.addSprite(this.single);
			event.addSprite(this.left);
			event.addSprite(this.right);
			this.singleMaterial = new Material(Sheets.CHEST_SHEET, this.single);
			this.leftMaterial = new Material(Sheets.CHEST_SHEET, this.left);
			this.rightMaterial = new Material(Sheets.CHEST_SHEET, this.right);
		}

		/**
		 * Gets this info's default/single {@link Material}.
		 *
		 * @return This info's default/single {@link Material}.
		 */
		@OnlyIn(Dist.CLIENT)
		public Material getSingleMaterial() {
			return this.singleMaterial;
		}

		/**
		 * Gets this info's left {@link Material}.
		 *
		 * @return This info's left {@link Material}.
		 */
		@OnlyIn(Dist.CLIENT)
		public Material getLeftMaterial() {
			return this.leftMaterial;
		}

		/**
		 * Gets this info's right {@link Material}.
		 *
		 * @return This info's right {@link Material}.
		 */
		@OnlyIn(Dist.CLIENT)
		public Material getRightMaterial() {
			return this.rightMaterial;
		}
	}
}
