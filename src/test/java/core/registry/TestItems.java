package core.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.util.registry.ItemSubRegistryHelper;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import core.BlueprintTest;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestItems {
	private static final Helper HELPER = BlueprintTest.REGISTRY_HELPER.getItemSubHelper();

	public static final RegistryObject<Item> ITEM = HELPER.createTest();
	public static final RegistryObject<ForgeSpawnEggItem> COW_SPAWN_EGG = HELPER.createItem("test_spawn_egg", () -> new ForgeSpawnEggItem(TestEntities.COW, 100, 200, new Item.Properties()));
	public static final Pair<RegistryObject<Item>, RegistryObject<Item>> BOAT = HELPER.createBoatAndChestBoatItem("test", TestBlocks.BLOCK, false);

	public static class Helper extends ItemSubRegistryHelper {

		public Helper(RegistryHelper parent) {
			super(parent, parent.getItemSubHelper().getDeferredRegister());
		}

		private RegistryObject<Item> createTest() {
			return this.deferredRegister.register("test", () -> new Item(new Item.Properties()));
		}

	}
}
