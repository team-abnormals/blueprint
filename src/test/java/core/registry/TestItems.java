package core.registry;

import com.teamabnormals.abnormals_core.core.annotations.Test;
import com.teamabnormals.abnormals_core.core.util.RegistryHelper;
import core.ACTest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Test
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestItems {
	private static final RegistryHelper HELPER = ACTest.REGISTRY_HELPER;

	public static final RegistryObject<Item> ITEM = HELPER.createItem("test", () -> RegistryHelper.createSimpleItem(ItemGroup.FOOD));
	public static final RegistryObject<Item> COW_SPAWN_EGG = HELPER.createSpawnEggItem("test", () -> TestEntities.COW.get(), 100, 200);
	public static final RegistryObject<Item> BOAT = HELPER.createBoatItem("test", TestBlocks.BLOCK);
}
