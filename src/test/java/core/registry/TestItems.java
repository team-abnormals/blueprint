package core.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.util.item.CreativeModeTabContentsPopulator;
import com.teamabnormals.blueprint.core.util.registry.ItemSubRegistryHelper;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import core.BlueprintTest;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import static com.teamabnormals.blueprint.core.util.item.ItemStackUtil.is;
import static net.minecraft.world.item.CreativeModeTabs.*;
import static net.minecraft.world.item.crafting.Ingredient.of;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestItems {
	private static final Helper HELPER = BlueprintTest.REGISTRY_HELPER.getItemSubHelper();

	public static final RegistryObject<Item> ITEM = HELPER.createTest();
	public static final RegistryObject<ForgeSpawnEggItem> COW_SPAWN_EGG = HELPER.createItem("test_spawn_egg", () -> new ForgeSpawnEggItem(TestEntities.COW, 100, 200, new Item.Properties()));
	public static final Pair<RegistryObject<Item>, RegistryObject<Item>> BOAT = HELPER.createBoatAndChestBoatItem("test", TestBlocks.BLOCK, false);

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
			super(parent, parent.getItemSubHelper().getDeferredRegister());
		}

		private RegistryObject<Item> createTest() {
			return this.deferredRegister.register("test", () -> new Item(new Item.Properties()));
		}

	}
}
