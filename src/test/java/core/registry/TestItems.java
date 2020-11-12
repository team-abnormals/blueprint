package core.registry;

import com.teamabnormals.abnormals_core.common.items.AbnormalsSpawnEggItem;
import com.teamabnormals.abnormals_core.core.annotations.Test;
import com.teamabnormals.abnormals_core.core.util.registry.ItemSubRegistryHelper;
import core.ACTest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Test
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestItems {
	private static final ItemSubRegistryHelper HELPER = ACTest.REGISTRY_HELPER.getItemSubHelper();

	public static final RegistryObject<Item> ITEM = HELPER.createItem("test", () -> new Item(new Item.Properties().group(ItemGroup.FOOD)));
	public static final RegistryObject<AbnormalsSpawnEggItem> COW_SPAWN_EGG = HELPER.createSpawnEggItem("test", TestEntities.COW::get, 100, 200);
	public static final RegistryObject<Item> BOAT = HELPER.createBoatItem("test", TestBlocks.BLOCK);
}
