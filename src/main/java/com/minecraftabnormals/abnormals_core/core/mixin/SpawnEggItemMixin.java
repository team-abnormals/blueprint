package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.AlphabeticalItemGroupFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

/**
 * This fixes incompatibility issues that occur when other modded spawn eggs are not sorted alphabetically.
 * <p>Also technically a feature.</p>
 */
@Mixin(SpawnEggItem.class)
public final class SpawnEggItemMixin extends Item {
	private static final AlphabeticalItemGroupFiller FILLER = AlphabeticalItemGroupFiller.forClass(SpawnEggItem.class);

	private SpawnEggItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (this.allowdedIn(group)) {
			ResourceLocation name = this.getRegistryName();
			if ((name == null || !name.getNamespace().equals("minecraft")) && (group == CreativeModeTab.TAB_MISC || group == CreativeModeTab.TAB_SEARCH)) {
				FILLER.fillItem(this, group, items);
			} else {
				super.fillItemCategory(group, items);
			}
		}
	}
}
