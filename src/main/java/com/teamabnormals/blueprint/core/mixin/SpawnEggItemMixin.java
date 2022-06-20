package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.util.item.filling.AlphabeticalItemCategoryFiller;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;

/**
 * This fixes incompatibility issues that occur when other modded spawn eggs are not sorted alphabetically.
 * <p>Also technically a feature.</p>
 */
@Mixin(SpawnEggItem.class)
public final class SpawnEggItemMixin extends Item {
	private static final AlphabeticalItemCategoryFiller FILLER = AlphabeticalItemCategoryFiller.forClass(SpawnEggItem.class);

	private SpawnEggItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (this.allowedIn(group)) {
			ResourceLocation name = ForgeRegistries.ITEMS.getKey(this);
			if ((name == null || !name.getNamespace().equals("minecraft")) && (group == CreativeModeTab.TAB_MISC || group == CreativeModeTab.TAB_SEARCH)) {
				FILLER.fillItem(this, group, items);
			} else {
				super.fillItemCategory(group, items);
			}
		}
	}
}
