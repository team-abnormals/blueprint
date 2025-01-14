package com.teamabnormals.blueprint.core.data.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public abstract class BlueprintLanguageProvider extends LanguageProvider {
	public final String modid;

	public BlueprintLanguageProvider(PackOutput output, String modid) {
		super(output, modid, "en_us");
		this.modid = modid;
	}

	public void add(Block... blocks) {
		List.of(blocks).forEach((block -> this.add(block, format(BuiltInRegistries.BLOCK.getKey(block)))));
	}

	public void addStorageBlock(Block... blocks) {
		List.of(blocks).forEach((block -> this.add(block, "Block of " + format(BuiltInRegistries.BLOCK.getKey(block)).replace(" Block", ""))));
	}

	public void add(Item... items) {
		List.of(items).forEach((item -> this.add(item, format(BuiltInRegistries.ITEM.getKey(item)))));
	}


	public void addMusicDisc(Item item, String description) {
		ResourceLocation name = BuiltInRegistries.ITEM.getKey(item);
		if (name != null) {
			this.add(item, "Music Disc");
			this.add(item.getDescriptionId() + ".desc", description);
		}
	}

	public void addDamageType(String suffix, String value) {
		this.add("death.attack." + this.modid + "." + suffix, value);
	}

	public String format(ResourceLocation name) {
		return format(name.getPath());
	}

	public String format(String path) {
		return WordUtils.capitalizeFully(path.replace("_", " ")).replace(" Of ", " of ");
	}
}