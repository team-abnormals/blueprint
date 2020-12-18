package com.minecraftabnormals.abnormals_core.core.mixin;

import com.minecraftabnormals.abnormals_core.core.util.item.filling.AlphabeticalItemGroupFiller;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
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
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {
			ResourceLocation name = this.getRegistryName();
			if ((name == null || !name.getNamespace().equals("minecraft")) && (group == ItemGroup.MISC || group == ItemGroup.SEARCH)) {
				FILLER.fillItem(this, group, items);
			} else {
				super.fillItemGroup(group, items);
			}
		}
	}
}
