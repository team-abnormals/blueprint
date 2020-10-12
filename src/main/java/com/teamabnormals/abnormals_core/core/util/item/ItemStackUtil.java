package com.teamabnormals.abnormals_core.core.util.item;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class ItemStackUtil {
	/**
	 * Searches for a specific item in a {@link NonNullList} of {@link ItemStack} and returns its index
	 *
	 * @param item  - The item to search for
	 * @param items - The list of ItemStacks
	 * @return - The index of the specified item in the list, if no item was found returns -1
	 */
	public static int findIndexOfItem(Item item, NonNullList<ItemStack> items) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getItem() == item) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Used in {@link Item#fillItemGroup(ItemGroup, NonNullList)} and {@link Block#fillItemGroup(ItemGroup, NonNullList)} to fill an item after a specific item for a group.
	 *
	 * @param item       The item to fill.
	 * @param targetItem The item to fill after.
	 * @param group      The group to fill it in.
	 * @param items      The {@link NonNullList} of item stacks to search for the target item and inject the item in.
	 */
	public static void fillAfterItemForGroup(Item item, Item targetItem, ItemGroup group, NonNullList<ItemStack> items) {
		if (isInGroup(item, group)) {
			int targetIndex = findIndexOfItem(targetItem, items);
			if (targetIndex != -1) {
				items.add(targetIndex + 1, new ItemStack(item));
			} else {
				items.add(new ItemStack(item));
			}
		}
	}

	/**
	 * Converts an Integer to a String of Roman Numerals; useful for levels
	 *
	 * @param number - The Integer to convert
	 * @return - The String of the Integer converted to Roman Numerals
	 */
	public static String intToRomanNumerals(int number) {
		String m[] = {"", "M", "MM", "MMM"};
		String c[] = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
		String x[] = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
		String i[] = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

		String thousands = m[number / 1000];
		String hundereds = c[(number % 1000) / 100];
		String tens = x[(number % 100) / 10];
		String ones = i[number % 10];

		return thousands + hundereds + tens + ones;
	}

	/**
	 * Searches for if an {@link Item} is present in an {@link ItemGroup} and returns if it is
	 *
	 * @param item  - The item searched
	 * @param group - The group searched
	 * @return - Whether or not the item is present in the group
	 */
	public static boolean isInGroup(Item item, ItemGroup group) {
		if (item.getCreativeTabs().stream().anyMatch(tab -> tab == group)) return true;
		ItemGroup itemgroup = item.getGroup();
		return itemgroup != null && (group == ItemGroup.SEARCH || group == itemgroup);
	}

	public static final class ItemStackBuilder {
		private final ItemStack stack;
		private final CompoundNBT tag;

		public ItemStackBuilder(ItemStack stack) {
			this.stack = stack;
			this.tag = stack.getOrCreateTag();
		}

		public ItemStackBuilder(IItemProvider item) {
			this(new ItemStack(item));
		}

		/**
		 * Sets the stack's count.
		 *
		 * @return This builder.
		 * @see ItemStack#setCount(int).
		 */
		public ItemStackBuilder setCount(int count) {
			this.stack.setCount(count);
			return this;
		}

		/**
		 * Grows the stack by an amount.
		 *
		 * @param amount Amount to grow the stack by.
		 * @return This builder.
		 * @see ItemStack#grow(int).
		 */
		public ItemStackBuilder grow(int amount) {
			this.stack.grow(amount);
			return this;
		}

		/**
		 * Shrinks the stack by an amount.
		 *
		 * @param amount Amount to shrink the stack by.
		 * @return This builder.
		 * @see ItemStack#shrink(int).
		 */
		public ItemStackBuilder shrink(int amount) {
			this.stack.shrink(amount);
			return this;
		}

		/**
		 * Sets the stack unbreakable.
		 *
		 * @return This builder.
		 */
		public ItemStackBuilder setUnbreakable() {
			this.tag.putBoolean("Unbreakable", true);
			return this;
		}

		/**
		 * Adds an enchantment with a level to the stack.
		 *
		 * @param enchantment The {@link Enchantment} to add.
		 * @param level       The level of the {@link Enchantment} to add.
		 * @return This builder.
		 */
		public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
			this.stack.addEnchantment(enchantment, level);
			return this;
		}

		/**
		 * Sets the name of the stack.
		 *
		 * @param text The name to set.
		 * @return This builder.
		 * @see ItemStack#setDisplayName(ITextComponent).
		 */
		public ItemStackBuilder setName(@Nullable ITextComponent text) {
			this.stack.setDisplayName(text);
			return this;
		}

		/**
		 * Adds lore to the stack.
		 *
		 * @param text The lore text to add.
		 * @return This builder.
		 */
		public ItemStackBuilder addLore(ITextComponent text) {
			CompoundNBT display = this.stack.getOrCreateChildTag("display");
			ListNBT loreListTag;
			if (display.contains("Lore", 9)) {
				loreListTag = display.getList("Lore", 8);
			} else {
				loreListTag = new ListNBT();
				display.put("Lore", loreListTag);
			}
			loreListTag.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(text)));
			return this;
		}

		/**
		 * Adds an {@link AttributeModifier} for an {@link Attribute} for an {@link EquipmentSlotType} on the stack.
		 *
		 * @param attribute The attribute to apply the {@link AttributeModifier} for.
		 * @param modifier  The {@link AttributeModifier} to apply to the {@link Attribute}.
		 * @param slot      The slot for when the {@link AttributeModifier} should be applied.
		 * @return This builder.
		 * @see ItemStack#addAttributeModifier(Attribute, AttributeModifier, EquipmentSlotType).
		 */
		public ItemStackBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier, @Nullable EquipmentSlotType slot) {
			this.stack.addAttributeModifier(attribute, modifier, slot);
			return this;
		}

		/**
		 * Adds an {@link AttributeModifier} for an {@link Attribute} for a multiple {@link EquipmentSlotType}s on the stack.
		 *
		 * @param attribute The attribute to apply the {@link AttributeModifier} for.
		 * @param modifier  The {@link AttributeModifier} to apply to the {@link Attribute}.
		 * @param slots     The slots for when the {@link AttributeModifier} should be applied.
		 * @return This builder.
		 * @see ItemStack#addAttributeModifier(Attribute, AttributeModifier, EquipmentSlotType).
		 * @see #addAttributeModifier(Attribute, AttributeModifier, EquipmentSlotType).
		 */
		public ItemStackBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier, EquipmentSlotType... slots) {
			for (EquipmentSlotType slot : slots) {
				this.stack.addAttributeModifier(attribute, modifier, slot);
			}
			return this;
		}

		/**
		 * Adds a predicate string tag for a predicate key.
		 * The two types of predicate keys are "CanDestroy" and "CanPlace".
		 *
		 * @param key       The predicate key.
		 * @param predicate The predicate string, this should be a string id.
		 * @return This builder.
		 */
		public ItemStackBuilder addPredicate(String key, String predicate) {
			ListNBT predicateList;
			if (this.tag.contains(key, 9)) {
				predicateList = this.tag.getList(key, 8);
			} else {
				predicateList = new ListNBT();
				this.tag.put(key, predicateList);
			}
			predicateList.add(StringNBT.valueOf(predicate));
			return this;
		}

		/**
		 * Adds a can destroy predicate for a specific block.
		 *
		 * @param block The block to mark as able to be destroyed.
		 * @return This builder.
		 */
		public ItemStackBuilder addCanDestroy(Block block) {
			return this.addPredicate("CanDestroy", ForgeRegistries.BLOCKS.getKey(block).toString());
		}

		/**
		 * Adds a can place on predicate for a specific block.
		 *
		 * @param block The block to mark as able to be placed on.
		 * @return This builder.
		 */
		public ItemStackBuilder addCanPlaceOn(Block block) {
			return this.addPredicate("CanPlaceOn", ForgeRegistries.BLOCKS.getKey(block).toString());
		}

		/**
		 * @return The built stack.
		 */
		public ItemStack build() {
			return this.stack.copy();
		}
	}
}