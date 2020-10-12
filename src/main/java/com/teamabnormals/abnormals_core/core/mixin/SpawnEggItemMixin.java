package com.teamabnormals.abnormals_core.core.mixin;

import com.teamabnormals.abnormals_core.core.util.item.filling.AlphabeticalItemGroupFiller;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpawnEggItem.class)
public final class SpawnEggItemMixin extends Item {
	private static final AlphabeticalItemGroupFiller FILLER = AlphabeticalItemGroupFiller.forClass(SpawnEggItem.class);

	private SpawnEggItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {
			ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(this);
			if (resourceLocation != null && !resourceLocation.getNamespace().equals("minecraft") && (group == ItemGroup.MISC || group == ItemGroup.SEARCH)) {
				FILLER.fillItem(this, group, items);
			} else {
				items.add(new ItemStack(this));
			}
		}
	}
}
