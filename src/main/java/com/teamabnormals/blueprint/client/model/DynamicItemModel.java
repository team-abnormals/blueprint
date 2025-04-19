package com.teamabnormals.blueprint.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ModelEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class DynamicItemModel implements BakedModel {
	private final BakedModel model;
	private final ItemOverrides overrideList;

	public DynamicItemModel(String folder, ModelResourceLocation defaultModel, ModelPredicate predicate, Map<ModelResourceLocation, BakedModel> modelManager) {
		this.model = modelManager.get(defaultModel);
		this.overrideList = new Overrides(modelManager, folder, defaultModel, predicate);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
		return this.model.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.model.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return this.model.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return this.model.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return this.model.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return this.model.getParticleIcon();
	}

	@Override
	public ItemOverrides getOverrides() {
		return this.overrideList;
	}

	public static class Overrides extends ItemOverrides {
		private final Map<ModelResourceLocation, BakedModel> modelManager;
		private final BakedModel model;
		private final Map<String, ResourceLocation> locationCache;
		private final Map<ResourceLocation, ModelResourceLocation> modelLocations;
		private final ModelPredicate predicate;

		private Overrides(Map<ModelResourceLocation, BakedModel> modelManager, String folder, ModelResourceLocation defaultModel, ModelPredicate predicate) {
			this.predicate = predicate;
			this.modelManager = modelManager;
			this.model = modelManager.get(defaultModel);
			this.locationCache = new HashMap<>();
			this.modelLocations = new HashMap<>();
			for (ResourceLocation location : Minecraft.getInstance().getResourceManager().listResources("models/item/" + folder, s -> s.getPath().endsWith(".json")).keySet()) {
				this.modelLocations.put(
						location.withPath(location.getPath().substring(("models/item/" + folder + "/").length(), location.getPath().length() - ".json".length())),
						ModelResourceLocation.standalone(location.withPath(location.getPath().substring("models/".length(), location.getPath().length() - ".json".length())))
				);
			}
		}

		@Nullable
		@Override
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity entity, int i) {
			ClientLevel level = clientLevel;
			if (level == null) {
				level = Minecraft.getInstance().level;
			}

			Optional<String> str = this.predicate.test(stack, level, entity);
			if (str.isPresent()) {
				ResourceLocation variant = this.locationCache.computeIfAbsent(str.get(), ResourceLocation::parse);
				if (this.modelLocations.containsKey(variant)) {
					return this.modelManager.get(this.modelLocations.get(variant));
				}
			}

			return this.model;
		}
	}

	public interface ModelPredicate {
		Optional<String> test(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity);
	}

	public static FishBucketPredicate fishBucket() {
		return FishBucketPredicate.INSTANCE;
	}

	public static class FishBucketPredicate implements ModelPredicate {
		private static final FishBucketPredicate INSTANCE = new FishBucketPredicate();

		private FishBucketPredicate() {
		}

		@Override
		public Optional<String> test(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
			CustomData data = stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
			if (!data.isEmpty() && level != null) {
				CompoundTag tag = data.copyTag();
				if (tag.contains("BucketVariantTag", Tag.TAG_STRING)) {
					return Optional.of(tag.getString("BucketVariantTag"));
				}
			}

			return Optional.empty();
		}
	}

	public static void register(ModelEvent.RegisterAdditional event, String folder) {
		for (ResourceLocation location : Minecraft.getInstance().getResourceManager().listResources("models/item/" + folder, s -> s.getPath().endsWith(".json")).keySet()) {
			event.register(ModelResourceLocation.standalone(location.withPath(location.getPath().substring("models/".length(), location.getPath().length() - ".json".length()))));
		}
	}

	public static void bake(ModelEvent.ModifyBakingResult event, ResourceLocation model, String folder, ModelResourceLocation defaultModel, ModelPredicate predicate) {
		event.getModels().put(ModelResourceLocation.inventory(model), new DynamicItemModel(folder, defaultModel, predicate, event.getModels()));
	}
}