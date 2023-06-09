package com.teamabnormals.blueprint.client;

import com.teamabnormals.blueprint.core.util.registry.BlockSubRegistryHelper;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for {@link Material} instances for Blueprint Chests.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintChestMaterials {
	private static final Map<String, ChestMaterials> MATERIALS = new HashMap<>();

	/**
	 * Creates and registers a {@link ChestMaterials} instance for a given ID and type.
	 * <p>Called in chest related methods in {@link BlockSubRegistryHelper}</p>
	 *
	 * @param modId   Mod ID for the chest.
	 * @param name    Name for the chest. (e.g. "oak")
	 * @param trapped If the chest is trapped.
	 * @return The chest materials' registry name.
	 */
	public static synchronized String registerMaterials(String modId, String name, boolean trapped) {
		String chestType = trapped ? "trapped" : "normal";
		String registryName = modId + ":" + name + "_" + chestType;
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			Material single = new Material(Sheets.CHEST_SHEET, new ResourceLocation(modId, "entity/chest/" + name + "/" + chestType));
			Material left = new Material(Sheets.CHEST_SHEET, new ResourceLocation(modId, "entity/chest/" + name + "/" + chestType + "_left"));
			Material right = new Material(Sheets.CHEST_SHEET, new ResourceLocation(modId, "entity/chest/" + name + "/" + chestType + "_right"));
			MATERIALS.put(registryName, new ChestMaterials(single, left, right));
		});
		return registryName;
	}

	/**
	 * Gets the {@link ChestMaterials} for a given chest type.
	 *
	 * @param name A string for the {@link ChestMaterials} to lookup.
	 * @return The {@link ChestMaterials} mapped to the given name, or null if there is no {@link ChestMaterials} mapped to the given type.
	 */
	@Nullable
	public static ChestMaterials getMaterials(String name) {
		return MATERIALS.get(name);
	}

	public record ChestMaterials(Material singleMaterial, Material leftMaterial, Material rightMaterial) {}
}
