package core.registry;

import com.minecraftabnormals.abnormals_core.common.items.AbnormalsSpawnEggItem;
import com.minecraftabnormals.abnormals_core.core.annotations.Test;
import com.minecraftabnormals.abnormals_core.core.util.registry.ItemSubRegistryHelper;
import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import core.ACTest;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;

@Test
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestItems {
	private static final Helper HELPER = ACTest.REGISTRY_HELPER.getItemSubHelper();

	public static final RegistryObject<Item> ITEM = HELPER.createTest();
	public static final RegistryObject<AbnormalsSpawnEggItem> COW_SPAWN_EGG = HELPER.createSpawnEggItem("test", TestEntities.COW::get, 100, 200);
	public static final RegistryObject<Item> BOAT = HELPER.createBoatItem("test", TestBlocks.BLOCK);

	public static class Helper extends ItemSubRegistryHelper {

		public Helper(RegistryHelper parent) {
			super(parent, parent.getItemSubHelper().getDeferredRegister());
		}

		private RegistryObject<Item> createTest() {
			return this.deferredRegister.register("test", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));
		}

	}
}
