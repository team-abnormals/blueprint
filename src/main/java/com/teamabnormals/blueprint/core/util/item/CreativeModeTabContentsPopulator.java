package com.teamabnormals.blueprint.core.util.item;

import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

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
			return this.editor(CreativeModeTabContentsEditor.forAllStacks(items, (event, stack) -> event.accept(stack, this.visibility)));
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
			return this.editor(CreativeModeTabContentsEditor.forAllStacks(items, (event, stack) -> event.insertFirst(stack, this.visibility)));
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

		@SafeVarargs
		private static void addStacksAfter(BuildCreativeModeTabContentsEvent event, CreativeModeTab.TabVisibility visibility, ObjectSortedSet<ItemStack> entries, Predicate<ItemStack> predicate, Supplier<ItemStack>... items) {
			for (var stack : entries) {
				if (predicate.test(stack)) {
					for (Supplier<ItemStack> item : items) {
						ItemStack itemValue = item.get();
						event.insertAfter(stack, itemValue, visibility);
						stack = itemValue;
					}
					return;
				}
			}
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
				var visibility = this.visibility;
				if (visibility == CreativeModeTab.TabVisibility.PARENT_TAB_ONLY || visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
					addStacksAfter(event, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY, event.getParentEntries(), predicate, items);
				}
				if (visibility == CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY || visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
					addStacksAfter(event, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY, event.getSearchEntries(), predicate, items);
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

		@SafeVarargs
		private static void addStacksBefore(BuildCreativeModeTabContentsEvent event, CreativeModeTab.TabVisibility visibility, ObjectSortedSet<ItemStack> entries, Predicate<ItemStack> predicate, Supplier<ItemStack>... items) {
			for (var stack : entries) {
				if (predicate.test(stack)) {
					for (Supplier<ItemStack> item : items) {
						event.insertBefore(stack, item.get(), visibility);
					}
					return;
				}
			}
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
				var visibility = this.visibility;
				if (visibility == CreativeModeTab.TabVisibility.PARENT_TAB_ONLY || visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
					addStacksBefore(event, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY, event.getParentEntries(), predicate, items);
				}
				if (visibility == CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY || visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
					addStacksBefore(event, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY, event.getSearchEntries(), predicate, items);
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

		@SafeVarargs
		private static void addStacksAlphabetically(BuildCreativeModeTabContentsEvent event, CreativeModeTab.TabVisibility visibility, ObjectSortedSet<ItemStack> entries, Predicate<ItemStack> shouldCompareToStack, String trimRegex, Supplier<ItemStack>... items) {
			TreeMap<String, ItemStack> treeMap = new TreeMap<>();
			String lastPath = "";
			for (ItemStack stack : entries) {
				var key = BuiltInRegistries.ITEM.getResourceKey(stack.getItem());
				if (key.isPresent() && shouldCompareToStack.test(stack)) {
					String path = key.get().location().getPath().replaceAll(trimRegex, "");
					if (path.compareTo(lastPath) > 0) {
						treeMap.putIfAbsent(path, stack);
						lastPath = path;
					} else break;
				}
			}
			for (Supplier<ItemStack> supplier : items) {
				ItemStack stack = supplier.get();
				var key = BuiltInRegistries.ITEM.getResourceKey(stack.getItem());
				if (key.isEmpty()) continue;
				String path = key.get().location().getPath().replace(trimRegex, "");
				var entry = treeMap.floorEntry(path);
				if (entry != null) {
					event.insertAfter(entry.getValue(), stack, visibility);
				} else {
					var firstEntry = treeMap.firstEntry();
					if (firstEntry != null) {
						event.insertBefore(firstEntry.getValue(), stack, visibility);
					} else {
						event.accept(stack, visibility);
					}
				}
				treeMap.put(path, stack);
			}
		}

		/**
		 * Adds an editor that will add multiple item stacks in alphabetical order.
		 *
		 * @param shouldCompareToStack A {@link Predicate} of {@link ItemStack} to determine which item stacks should be compared.
		 * @param trimRegex            A regex expression for trimming item names before sorting.
		 * @param items                An array of {@link Supplier} of {@link ItemStack} to add in alphabetical order.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addStacksAlphabetically(Predicate<ItemStack> shouldCompareToStack, String trimRegex, Supplier<ItemStack>... items) {
			return this.editor(event -> {
				var visibility = this.visibility;
				if (visibility == CreativeModeTab.TabVisibility.PARENT_TAB_ONLY || visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
					addStacksAlphabetically(event, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY, event.getParentEntries(), shouldCompareToStack, trimRegex, items);
				}
				if (visibility == CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY || visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
					addStacksAlphabetically(event, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY, event.getSearchEntries(), shouldCompareToStack, trimRegex, items);
				}
			});
		}

		/**
		 * Adds an editor that will add multiple item stacks in alphabetical order.
		 *
		 * @param shouldCompareToStack A {@link Predicate} of {@link ItemStack} to determine which item stacks should be compared.
		 * @param trimRegex            A regex expression for trimming item names before sorting.
		 * @param items                An array of {@link Supplier} of {@link ItemLike} to add in alphabetical order.
		 * @return This instance.
		 */
		@SafeVarargs
		public final Entry addItemsAlphabetically(Predicate<ItemStack> shouldCompareToStack, String trimRegex, Supplier<? extends ItemLike>... items) {
			return this.addStacksAlphabetically(shouldCompareToStack, trimRegex, convertItemLikesToStacks(items));
		}

		@SafeVarargs
		public final Entry addSpawnEggsAlphabetically(Supplier<? extends ItemLike>... items) {
			return this.addItemsAlphabetically(ItemStackUtil.is(SpawnEggItem.class), "spawn_egg|_", items);
		}

		@SafeVarargs
		public final Entry addPotterySherdsAlphabetically(Supplier<? extends ItemLike>... items) {
			return this.addItemsAlphabetically(stack -> stack.is(ItemTags.DECORATED_POT_SHERDS), "pottery_sherd|_", items);
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
