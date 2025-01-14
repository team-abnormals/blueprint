package com.teamabnormals.blueprint.core.data.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;


public abstract class BlueprintItemModelProvider extends ItemModelProvider {

	public BlueprintItemModelProvider(PackOutput output, String modid, ExistingFileHelper helper) {
		super(output, modid, helper);
	}

	public ItemModelBuilder item(DeferredHolder<? extends ItemLike, ?> item, String type) {
		return this.withExistingParent(name(item.get()), "item/" + type).texture("layer0", itemTexture(item.get()));
	}

	public ItemModelBuilder item(DeferredHolder<? extends ItemLike, ?> item, String path, String type) {
		return this.withExistingParent(name(item.get()), "item/" + type).texture("layer0", ResourceLocation.fromNamespaceAndPath(this.modid, "item/" + path));
	}

	public ItemModelBuilder item(ResourceLocation location, String type) {
		return this.withExistingParent(location.getPath(), "item/" + type).texture("layer0", ResourceLocation.fromNamespaceAndPath(this.modid, "item/" + location.getPath()));
	}

	public ItemModelBuilder blockItem(DeferredHolder<Block, ?> block) {
		return this.getBuilder(BlueprintBlockStateProvider.name(block.get())).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(this.modid, "block/" + BlueprintBlockStateProvider.name(block.get()))));
	}

	@SafeVarargs
	public final void generatedItem(DeferredHolder<? extends ItemLike, ?>... items) {
		for (DeferredHolder<? extends ItemLike, ?> item : items) {
			this.item(item, "generated");
		}
	}

	@SafeVarargs
	public final void handheldItem(DeferredHolder<? extends ItemLike, ?>... items) {
		for (DeferredHolder<? extends ItemLike, ?> item : items) {
			this.item(item, "handheld");
		}
	}

	@SafeVarargs
	public final void spawnEggItem(DeferredHolder<? extends ItemLike, ?>... items) {
		for (DeferredHolder<? extends ItemLike, ?> item : items) {
			this.withExistingParent(name(item.get()), "item/template_spawn_egg");
		}
	}

	public void animatedItem(DeferredHolder<? extends ItemLike, ?> item, int count) {
		for (int i = 0; i < count; i++) {
			String path = name(item.get()) + "_" + String.format("%02d", i);
			this.withExistingParent(path, "item/generated").texture("layer0", ResourceLocation.fromNamespaceAndPath(this.modid, "item/" + path));
		}
	}

	@SafeVarargs
	public final void trimmableArmorItem(DeferredHolder<? extends ItemLike, ?>... items) {
		for (DeferredHolder<? extends ItemLike, ?> item : items) {
			if (item.get().asItem() instanceof ArmorItem armor) {
				ResourceLocation location = BuiltInRegistries.ITEM.getKey(armor);
				ItemModelBuilder itemModel = this.item(item, "generated");
				int trimType = 1;
				for (String trim : new String[]{"quartz", "iron", "netherite", "redstone", "copper", "gold", "emerald", "diamond", "lapis", "amethyst"}) {
					ResourceLocation name = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath() + "_" + trim + "_trim");
					itemModel.override().model(new ModelFile.UncheckedModelFile(name)).predicate(ResourceLocation.withDefaultNamespace("trim_type"), (float) (trimType / 10.0));
					ResourceLocation texture = ResourceLocation.withDefaultNamespace("trims/items/" + armor.getType().getName() + "_trim_" + trim);
					this.existingFileHelper.trackGenerated(texture, PackType.CLIENT_RESOURCES, ".png", "textures");
					withExistingParent(name.getPath(), "item/generated").texture("layer0", ResourceLocation.fromNamespaceAndPath(this.modid, "item/" + location.getPath())).texture("layer1", texture);
					trimType++;
				}
			}
		}
	}

	public static ResourceLocation key(ItemLike item) {
		return BuiltInRegistries.ITEM.getKey(item.asItem());
	}

	public static String name(ItemLike item) {
		return key(item).getPath();
	}

	public static ResourceLocation itemTexture(ItemLike item) {
		ResourceLocation name = key(item);
		return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), ModelProvider.ITEM_FOLDER + "/" + name.getPath());
	}
}