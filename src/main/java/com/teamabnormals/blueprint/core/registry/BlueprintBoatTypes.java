package com.teamabnormals.blueprint.core.registry;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registry class for Blueprint's custom boat system.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintBoatTypes {
	public static final ResourceLocation UNDEFINED_BOAT_LOCATION = new ResourceLocation("oak");
	private static final Map<ResourceLocation, BlueprintBoatType> BOATS = new HashMap<>();

	static {
		registerType(UNDEFINED_BOAT_LOCATION, () -> Items.OAK_BOAT, () -> Items.OAK_CHEST_BOAT, () -> Blocks.OAK_PLANKS, false);
	}

	/**
	 * Registers a Blueprint Boat Type.
	 * <p>This method is safe to call during parallel mod loading.</p>
	 *
	 * @param name     The registry name for the {@link BlueprintBoatType}.
	 * @param boat     The boat item.
	 * @param chestBoat The chest boat item.
	 * @param plank    The planks item.
	 * @param raft     If the boat is a raft.
	 */
	public static synchronized void registerType(ResourceLocation name, Supplier<Item> boat, Supplier<Item> chestBoat, Supplier<Block> plank, boolean raft) {
		BOATS.put(name, new BlueprintBoatType(name, boat, chestBoat, plank, raft));
	}

	/**
	 * Gets the {@link BlueprintBoatType} for a given name, or null if the given name is not in the registry.
	 *
	 * @param name A {@link ResourceLocation} name to look up.
	 * @return The {@link BlueprintBoatType} for a given name, or null if the given name is not in the registry.
	 */
	@Nullable
	public static BlueprintBoatType getType(ResourceLocation name) {
		return BOATS.get(name);
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		LayerDefinition boatModel = BoatModel.createBodyModel();
		LayerDefinition chestBoatModel = ChestBoatModel.createBodyModel();
		LayerDefinition raftModel = RaftModel.createBodyModel();
		LayerDefinition chestRaftModel = ChestRaftModel.createBodyModel();
		BOATS.forEach((name, type) -> {
			if (name == UNDEFINED_BOAT_LOCATION) return;
			boolean isRaft = type.isRaft();
			event.registerLayerDefinition(type.getBoatModelLayerLocation(), isRaft ? () -> raftModel : () -> boatModel);
			event.registerLayerDefinition(type.getChestBoatModelLayerLocation(), isRaft ? () -> chestRaftModel : () -> chestBoatModel);
		});
	}

	@OnlyIn(Dist.CLIENT)
	public static IdentityHashMap<BlueprintBoatType, Pair<ResourceLocation, ListModel<Boat>>> createBoatResources(EntityRendererProvider.Context context, boolean chest) {
		IdentityHashMap<BlueprintBoatType, Pair<ResourceLocation, ListModel<Boat>>> boatTypeToModel = new IdentityHashMap<>();
		BOATS.values().forEach(type -> boatTypeToModel.put(type, Pair.of(chest ? type.getChestVariantTexture() : type.getTexture(), createBoatModel(context, type, chest))));
		return boatTypeToModel;
	}

	@OnlyIn(Dist.CLIENT)
	private static ListModel<Boat> createBoatModel(EntityRendererProvider.Context context, BlueprintBoatType type, boolean chest) {
		ModelPart modelpart = context.bakeLayer(chest ? type.getChestBoatModelLayerLocation() : type.getBoatModelLayerLocation());
		if (type.isRaft()) {
			return chest ? new ChestRaftModel(modelpart) : new RaftModel(modelpart);
		} else {
			return chest ? new ChestBoatModel(modelpart) : new BoatModel(modelpart);
		}
	}

	/**
	 * A class representing the needed information about a custom boat type.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static class BlueprintBoatType {
		private final ResourceLocation name;
		private final Supplier<Item> boat;
		private final Supplier<Item> chestBoat;
		private final Supplier<Block> plank;
		private final boolean raft;
		private final ResourceLocation texture;
		private final ResourceLocation chestVariantTexture;

		public BlueprintBoatType(ResourceLocation name, Supplier<Item> boat, Supplier<Item> chestBoat, Supplier<Block> plank, boolean raft) {
			this.name = name;
			this.boat = boat;
			this.chestBoat = chestBoat;
			this.plank = plank;
			this.raft = raft;
			String namespace = name.getNamespace();
			String path = name.getPath();
			this.texture = new ResourceLocation(namespace, "textures/entity/boat/" + path + ".png");
			this.chestVariantTexture = new ResourceLocation(namespace, "textures/entity/chest_boat/" + path + ".png");
		}

		/**
		 * Gets this boat type's name.
		 *
		 * @return This boat type's name.
		 */
		public ResourceLocation getName() {
			return this.name;
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
		 * If this boat type is a raft.
		 *
		 * @return Whether this boat type is a raft.
		 */
		public boolean isRaft() {
			return this.raft;
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

		/**
		 * Gets the {@link ModelLayerLocation} instance for accessing this type's model.
		 *
		 * @return The {@link ModelLayerLocation} instance for accessing this type's model.
		 */
		@OnlyIn(Dist.CLIENT)
		public ModelLayerLocation getBoatModelLayerLocation() {
			ResourceLocation name = this.getName();
			String namespace = name.getNamespace();
			String path = name.getPath();
			return new ModelLayerLocation(new ResourceLocation(namespace, "boat/" + path), "main");
		}

		/**
		 * Gets the {@link ModelLayerLocation} instance for accessing this type's chest-variant model.
		 *
		 * @return The {@link ModelLayerLocation} instance for accessing this type's chest-variant model.
		 */
		@OnlyIn(Dist.CLIENT)
		public ModelLayerLocation getChestBoatModelLayerLocation() {
			ResourceLocation name = this.getName();
			String namespace = name.getNamespace();
			String path = name.getPath();
			return new ModelLayerLocation(new ResourceLocation(namespace, "chest_boat/" + path), "main");
		}
	}
}