package core.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.item.BlueprintBoatItem;
import com.teamabnormals.blueprint.core.events.LoadThisClassEvent;
import com.teamabnormals.blueprint.core.util.item.CreativeModeTabContentsPopulator;
import com.teamabnormals.blueprint.core.util.registry.ItemSubRegistryHelper;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import core.BlueprintTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.teamabnormals.blueprint.core.util.item.ItemStackUtil.is;
import static net.minecraft.world.item.CreativeModeTabs.*;
import static net.minecraft.world.item.crafting.Ingredient.of;

@EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class TestItems {
	@SubscribeEvent
	public static void $(LoadThisClassEvent event) {}

	private static final Helper HELPER = BlueprintTest.REGISTRY_HELPER.getItemSubHelper();

	public static final DeferredItem<Item> ITEM = HELPER.createTest();
	public static final DeferredItem<DeferredSpawnEggItem> COW_SPAWN_EGG = HELPER.createItem("test_spawn_egg", () -> new DeferredSpawnEggItem(TestEntities.COW, 100, 200, new Item.Properties()));
	public static final Pair<DeferredItem<BlueprintBoatItem>, DeferredItem<BlueprintBoatItem>> BOAT = HELPER.createBoatAndChestBoatItem("test", TestBlocks.BLOCK, false);

	public static final DeferredRegister<DecoratedPotPattern> DECORATED_POT_PATTERNS = DeferredRegister.create(Registries.DECORATED_POT_PATTERN, BlueprintTest.MOD_ID);
	public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> TEST_POTTERY_SHERD = DECORATED_POT_PATTERNS.register("test_pottery_pattern", () -> new DecoratedPotPattern(ResourceLocation.fromNamespaceAndPath(BlueprintTest.MOD_ID, "test_pottery_pattern")));
	public static final DeferredItem<Item> PRIMAL_ARMOR_TRIM_SMITHING_TEMPLATE = HELPER.createItem("primal_armor_trim_smithing_template", () -> SmithingTemplateItem.createArmorTrimTemplate(TestTrimPatterns.PRIMAL));

	public static void setupTabEditors() {
		CreativeModeTabContentsPopulator.mod(BlueprintTest.MOD_ID)
				.tab(SPAWN_EGGS)
				.addItemsAlphabetically(is(SpawnEggItem.class), COW_SPAWN_EGG)
				.tab(TOOLS_AND_UTILITIES)
				.addItemsAfter(of(Items.BAMBOO_CHEST_RAFT), BOAT.getFirst(), BOAT.getSecond())
				.addItemsFirst(ITEM)
				.tab(FUNCTIONAL_BLOCKS)
				.addItemsAfter(of(Items.BAMBOO_HANGING_SIGN), TestBlocks.SIGNS.getFirst(), TestBlocks.HANGING_SIGNS.getFirst())
				.addItemsAfter(of(Items.CHEST), TestBlocks.EXAMPLE_CHEST)
				.tab(REDSTONE_BLOCKS)
				.addItemsAfter(of(Items.CHEST), TestBlocks.EXAMPLE_CHEST)
				.addItemsAfter(of(Items.TRAPPED_CHEST), TestBlocks.EXAMPLE_TRAPPED_CHEST);
	}

	public static class Helper extends ItemSubRegistryHelper {

		public Helper(RegistryHelper parent) {
			super(parent, (DeferredRegister.Items) parent.getItemSubHelper().getDeferredRegister());
		}

		private DeferredItem<Item> createTest() {
			return this.deferredRegister.register("test", () -> new Item(new Item.Properties()));
		}

	}
}
