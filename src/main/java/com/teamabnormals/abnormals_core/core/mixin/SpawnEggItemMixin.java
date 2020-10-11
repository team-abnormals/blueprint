package com.teamabnormals.abnormals_core.core.mixin;

import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(SpawnEggItem.class)
public final class SpawnEggItemMixin extends Item {
	private static final Pattern NAME_PATTERN = Pattern.compile("(\\w+)[.](\\w+)[.](\\w+)");

	private SpawnEggItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {
			ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(this);
			if (resourceLocation != null && !resourceLocation.getNamespace().equals("minecraft") && (group == ItemGroup.MISC || group == ItemGroup.SEARCH)) {
				Map<String, Integer> nameToIndex = Util.make(Maps.newHashMap(), (map) -> {
					for (int i = 0; i < items.size(); i++) {
						Item item = items.get(i).getItem();
						if (item instanceof SpawnEggItem) {
							Matcher compareMatcher = NAME_PATTERN.matcher(item.getTranslationKey());
							if (compareMatcher.matches()) {
								map.put(compareMatcher.group(3), i);
							}
						}
					}
				});
				if (!nameToIndex.isEmpty()) {
					Matcher matcher = NAME_PATTERN.matcher(this.getTranslationKey());
					if (matcher.matches()) {
						String name = matcher.group(3);
						List<String> list = new ArrayList<>(nameToIndex.keySet());
						list.add(name);
						Collections.sort(list);
						items.add(nameToIndex.get(list.get(list.indexOf(name) - 1)) + 1, new ItemStack(this));
					}
				} else {
					items.add(new ItemStack(this));
				}
			} else {
				items.add(new ItemStack(this));
			}
		}
	}
}
