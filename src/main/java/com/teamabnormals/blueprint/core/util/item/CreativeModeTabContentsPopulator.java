package com.teamabnormals.blueprint.core.util.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Manager class for registering concise and handy ways of adding Item Stacks to Creative Tabs.
 * <p>Call {@link #mod(String)} to get an entry for a Mod ID.</p>
 *
 * @author SmellyModder
 */
public final class CreativeModeTabContentsPopulator {
	private static final TreeMap<String, Entry> ENTRIES = new TreeMap<>();

	/**
	 * Computes and returns a {@link CreativeModeTabContentsPopulator.Entry} instance to use for adding Item Stacks to Creative Tabs.
	 *
	 * @param id A Mod ID to compute a {@link CreativeModeTabContentsPopulator.Entry} object for.
	 * @return A {@link CreativeModeTabContentsPopulator.Entry} instance to use for adding Item Stacks to Creative Tabs.
	 */
	public static synchronized Entry mod(String id) {
		return ENTRIES.computeIfAbsent(id, key -> new Entry());
	}

	/**
	 * Processes all the entries in {@link #ENTRIES} on a {@link BuildCreativeModeTabContentsEvent} instance.
	 *
	 * @param event A {@link BuildCreativeModeTabContentsEvent} instance to process the entries on.
	 */
	public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
		ENTRIES.values().forEach(entry -> entry.onBuildCreativeModeTabContents(event));
	}

	/**
	 * The functional interface for defining an editor of a {@link BuildCreativeModeTabContentsEvent} instance.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	@FunctionalInterface
	public interface CreativeModeTabContentsEditor {
		/**
		 * Edits a {@link BuildCreativeModeTabContentsEvent} instance.
		 *
		 * @param event A {@link BuildCreativeModeTabContentsEvent} instance to edit.
		 */
		void edit(BuildCreativeModeTabContentsEvent event);

		/**
		 * Creates a new {@link CreativeModeTabContentsPopulator} instance that runs a bi-consumer on array of items.
		 *
		 * @param items    An array of items to use the consumer on.
		 * @param consumer A bi-consumer to accept a {@link BuildCreativeModeTabContentsEvent} instance along with each individual item in the array.
		 * @return A new {@link CreativeModeTabContentsPopulator} instance that runs a bi-consumer on array of items.
		 */
		static CreativeModeTabContentsEditor forAllStacks(Supplier<ItemStack>[] items, BiConsumer<BuildCreativeModeTabContentsEvent, ItemStack> consumer) {
			return (event) -> {
				for (Supplier<ItemStack> supplier : items) consumer.accept(event, supplier.get());
			};
		}
	}

	/**
	 * The builder class for representing a populator entry that handles various {@link CreativeModeTabContentsEditor} instances.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Entry {
		private final HashMap<ResourceKey<CreativeModeTab>, ArrayList<CreativeModeTabContentsEditor>> keyedContentEditors = new HashMap<>();
		private final HashMap<Predicate<BuildCreativeModeTabContentsEvent>, ArrayList<CreativeModeTabContentsEditor>> otherContentEditors = new HashMap<>();
		private CreativeModeTab.TabVisibility visibility = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
		private ArrayList<CreativeModeTabContentsEditor> currentEditors = null;

		/**
		 * Sets the visibility for the item stacks added by this entry until this method is called again.
		 *
		 * @param visibility The {@link CreativeModeTab.TabVisibility} to set.
		 * @return This instance.
		 */
		public Entry visibility(CreativeModeTab.TabVisibility visibility) {
			this.visibility = visibility;
			return this;
		}

		/**
		 * Sets the current list of editors to be the editors assigned to a specific tab key.
		 * <p><b>This must be called before calling any method that adds to the editors!</b></p>
		 *
		 * @param tabKey The {@link ResourceKey} of the {@link CreativeModeTab} that the new current editors will target.
		 * @return This instance.
		 */
		public Entry tab(ResourceKey<CreativeModeTab> tabKey) {
			this.currentEditors = this.keyedContentEditors.computeIfAbsent(tabKey, key -> new ArrayList<>());
			return this;
		}

		/**
		 * Sets the current list of editors to be the editors assigned to a specific predicate.
		 *
		 * @param predicate A {@link Predicate} of {@link BuildCreativeModeTabContentsEvent} to determine if an event should be edited.
		 * @return This instance.
		 */
		public Entry predicate(Predicate<BuildCreativeModeTabContentsEvent> predicate) {
			this.currentEditors = this.otherContentEditors.computeIfAbsent(predicate, key -> new ArrayList<>());
			return this;
		}

		/**
		 * Adds a {@link CreativeModeTabContentsEditor} instance to the current editors.
		 *
		 * @param editor A {@link CreativeModeTabContentsEditor} instance to add to the current editors.
		 * @return This instance.
		 */
		public Entry editor(CreativeModeTabContentsEditor editor) {
			this.currentEditors.add(editor);
			return this;
		}

		/**
		 * Adds an editor that will add multiple item stacks to the end of a tab.
		 *
		 * @param items An array of {@link Supplier} of {@link ItemStack} to add to the end of a tab.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addStacks(Supplier<ItemStack>... items) {
			return this.editor(CreativeModeTabContentsEditor.forAllStacks(items, (event, stack) -> event.getEntries().put(stack, this.visibility)));
		}

		/**
		 * Adds an editor that will add multiple item stacks to the end of a tab.
		 *
		 * @param items An array of {@link Supplier} of {@link ItemLike} to add to the end of a tab.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addItems(Supplier<? extends ItemLike>... items) {
			return this.addStacks(convertItemLikesToStacks(items));
		}

		/**
		 * Adds an editor that will add multiple item stacks to the start of a tab.
		 *
		 * @param items An array of {@link Supplier} of {@link ItemStack} to add to the start of a tab.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addStacksFirst(Supplier<ItemStack>... items) {
			return this.editor(CreativeModeTabContentsEditor.forAllStacks(items, (event, stack) -> event.getEntries().putFirst(stack, this.visibility)));
		}

		/**
		 * Adds an editor that will add multiple item stacks to the start of a tab.
		 *
		 * @param items An array of {@link Supplier} of {@link ItemLike} to add to the start of a tab.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addItemsFirst(Supplier<? extends ItemLike>... items) {
			return this.addStacksFirst(convertItemLikesToStacks(items));
		}

		/**
		 * Adds an editor that will add multiple item stacks after the first valid item stack.
		 *
		 * @param predicate A {@link Predicate} of {@link ItemStack} to determine which item stack is valid.
		 * @param items     An array of {@link Supplier} of {@link ItemStack} to add after the first valid item stack.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addStacksAfter(Predicate<ItemStack> predicate, Supplier<ItemStack>... items) {
			return this.editor(event -> {
				MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();
				for (var entry : entries) {
					ItemStack stack = entry.getKey();
					if (predicate.test(stack)) {
						CreativeModeTab.TabVisibility visibility = this.visibility;
						for (Supplier<ItemStack> item : items) {
							ItemStack itemValue = item.get();
							entries.putAfter(stack, itemValue, visibility);
							stack = itemValue;
						}
						return;
					}
				}
			});
		}

		/**
		 * Adds an editor that will add multiple item stacks after the first valid item stack.
		 *
		 * @param predicate A {@link Predicate} of {@link ItemStack} to determine which item stack is valid.
		 * @param items     An array of {@link Supplier} of {@link ItemLike} to add after the first valid item stack.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addItemsAfter(Predicate<ItemStack> predicate, Supplier<? extends ItemLike>... items) {
			return this.addStacksAfter(predicate, convertItemLikesToStacks(items));
		}

		/**
		 * Adds an editor that will add multiple item stacks before the first valid item stack.
		 *
		 * @param predicate A {@link Predicate} of {@link ItemStack} to determine which item stack is valid.
		 * @param items     An array of {@link Supplier} of {@link ItemStack} to add before the first valid item stack.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addStacksBefore(Predicate<ItemStack> predicate, Supplier<ItemStack>... items) {
			return this.editor(event -> {
				MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();
				for (var entry : entries) {
					ItemStack stack = entry.getKey();
					if (predicate.test(stack)) {
						CreativeModeTab.TabVisibility visibility = this.visibility;
						for (Supplier<ItemStack> item : items) {
							entries.putBefore(stack, item.get(), visibility);
						}
						return;
					}
				}
			});
		}

		/**
		 * Adds an editor that will add multiple item stacks before the first valid item stack.
		 *
		 * @param predicate A {@link Predicate} of {@link ItemStack} to determine which item stack is valid.
		 * @param items     An array of {@link Supplier} of {@link ItemLike} to add before the first valid item stack.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addItemsBefore(Predicate<ItemStack> predicate, Supplier<? extends ItemLike>... items) {
			return this.addStacksBefore(predicate, convertItemLikesToStacks(items));
		}

		/**
		 * Adds an editor that will add multiple item stacks in alphabetical order.
		 *
		 * @param shouldCompareToStack A {@link Predicate} of {@link ItemStack} to determine which item stacks should be compared.
		 * @param items                An array of {@link Supplier} of {@link ItemStack} to add in alphabetical order.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addStacksAlphabetically(Predicate<ItemStack> shouldCompareToStack, Supplier<ItemStack>... items) {
			return this.editor(event -> {
				MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();
				TreeMap<String, ItemStack> treeMap = new TreeMap<>();
				entries.forEach(entry -> {
					ItemStack stack = entry.getKey();
					ResourceLocation location = ForgeRegistries.ITEMS.getKey(stack.getItem());
					if (location != null && shouldCompareToStack.test(stack))
						treeMap.putIfAbsent(location.getPath(), stack);
				});
				CreativeModeTab.TabVisibility visibility = this.visibility;
				for (Supplier<ItemStack> supplier : items) {
					ItemStack stack = supplier.get();
					ResourceLocation location = ForgeRegistries.ITEMS.getKey(stack.getItem());
					if (location != null) {
						String path = location.getPath();
						var entry = treeMap.floorEntry(path);
						if (entry != null) {
							entries.putAfter(entry.getValue(), stack, visibility);
						} else {
							entries.put(stack, visibility);
						}
						treeMap.put(path, stack);
					}
				}
			});
		}

		/**
		 * Adds an editor that will add multiple item stacks in alphabetical order.
		 *
		 * @param shouldCompareToStack A {@link Predicate} of {@link ItemStack} to determine which item stacks should be compared.
		 * @param items                An array of {@link Supplier} of {@link ItemLike} to add in alphabetical order.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addItemsAlphabetically(Predicate<ItemStack> shouldCompareToStack, Supplier<? extends ItemLike>... items) {
			return this.addStacksAlphabetically(shouldCompareToStack, convertItemLikesToStacks(items));
		}

		private void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
			var editorsForKey = this.keyedContentEditors.get(event.getTabKey());
			if (editorsForKey != null) editorsForKey.forEach(editor -> editor.edit(event));
			this.otherContentEditors.forEach((eventPredicate, editors) -> {
				if (eventPredicate.test(event)) editors.forEach(editor -> editor.edit(event));
			});
		}

		@SuppressWarnings("unchecked")
		private static Supplier<ItemStack>[] convertItemLikesToStacks(Supplier<? extends ItemLike>... items) {
			int length = items.length;
			Supplier<ItemStack>[] newItems = new Supplier[length];
			for (int i = 0; i < length; i++) {
				int finalI = i;
				newItems[i] = () -> new ItemStack(items[finalI].get());
			}
			return newItems;
		}
	}
}
