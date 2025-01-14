package core.registry;

import com.teamabnormals.blueprint.core.events.LoadThisClassEvent;
import com.teamabnormals.blueprint.core.util.registry.EntitySubRegistryHelper;
import common.entity.EndimatedWalkingEntity;
import common.entity.TestEndimatedEntity;
import core.BlueprintTest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Cow;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class TestEntities {
	@SubscribeEvent
	public static void $(LoadThisClassEvent event) {}

	private static final EntitySubRegistryHelper HELPER = BlueprintTest.REGISTRY_HELPER.getEntitySubHelper();

	public static final DeferredHolder<? super EntityType<Cow>, EntityType<Cow>> COW = HELPER.createEntity("example", Cow::new, MobCategory.CREATURE, 1.0F, 1.0F);
	public static final DeferredHolder<? super EntityType<TestEndimatedEntity>, EntityType<TestEndimatedEntity>> ENDIMATED_TEST = HELPER.createEntity("endimated_test", TestEndimatedEntity::new, MobCategory.CREATURE, 1.0F, 1.0F);
	public static final DeferredHolder<? super EntityType<EndimatedWalkingEntity>, EntityType<EndimatedWalkingEntity>> ENDIMATED_WALKING = HELPER.createEntity("endimated_walking", EndimatedWalkingEntity::new, MobCategory.CREATURE, 1.0F, 1.0F);

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(TestEntities.COW.get(), Cow.createAttributes().build());
		event.put(TestEntities.ENDIMATED_TEST.get(), PathfinderMob.createMobAttributes().build());
		event.put(TestEntities.ENDIMATED_WALKING.get(), PathfinderMob.createMobAttributes().build());
	}
}
