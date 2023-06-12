package core.registry;

import com.google.common.collect.Sets;
import com.teamabnormals.blueprint.core.util.registry.BlockEntitySubRegistryHelper;
import common.block.entity.TestEndimatedBlockEntity;
import core.BlueprintTest;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestBlockEntities {
	private static final BlockEntitySubRegistryHelper HELPER = BlueprintTest.REGISTRY_HELPER.getBlockEntitySubHelper();

	public static final RegistryObject<BlockEntityType<TestEndimatedBlockEntity>> TEST_ENDIMATED = HELPER.createBlockEntity("test_endimated", TestEndimatedBlockEntity::new, () -> Sets.newHashSet(TestBlocks.TEST_ENDIMATED.get()));
}
