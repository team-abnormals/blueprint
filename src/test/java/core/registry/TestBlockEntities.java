package core.registry;

import com.google.common.collect.Sets;
import com.teamabnormals.blueprint.core.events.LoadThisClassEvent;
import com.teamabnormals.blueprint.core.util.registry.BlockEntitySubRegistryHelper;
import common.block.entity.TestEndimatedBlockEntity;
import core.BlueprintTest;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class TestBlockEntities {
	@SubscribeEvent
	public static void $(LoadThisClassEvent event) {}

	private static final BlockEntitySubRegistryHelper HELPER = BlueprintTest.REGISTRY_HELPER.getBlockEntitySubHelper();

	public static final DeferredHolder<? super BlockEntityType<TestEndimatedBlockEntity>, BlockEntityType<TestEndimatedBlockEntity>> TEST_ENDIMATED = HELPER.createBlockEntity("test_endimated", TestEndimatedBlockEntity::new, () -> Sets.newHashSet(TestBlocks.TEST_ENDIMATED.get()));
}
