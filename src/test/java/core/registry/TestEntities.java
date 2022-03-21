package core.registry;

import com.teamabnormals.blueprint.core.util.registry.EntitySubRegistryHelper;
import common.entity.EndimatedWalkingEntity;
import common.entity.TestEndimatedEntity;
import core.BlueprintTest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestEntities {
	private static final EntitySubRegistryHelper HELPER = BlueprintTest.REGISTRY_HELPER.getEntitySubHelper();

	public static final RegistryObject<EntityType<Cow>> COW = HELPER.createLivingEntity("example", Cow::new, MobCategory.CREATURE, 1.0F, 1.0F);
	public static final RegistryObject<EntityType<Cod>> COD = HELPER.createLivingEntity("fish_example", Cod::new, MobCategory.WATER_AMBIENT, 1.0F, 1.0F);
	public static final RegistryObject<EntityType<TestEndimatedEntity>> ENDIMATED_TEST = HELPER.createLivingEntity("endimated_test", TestEndimatedEntity::new, MobCategory.CREATURE, 1.0F, 1.0F);
	public static final RegistryObject<EntityType<EndimatedWalkingEntity>> ENDIMATED_WALKING = HELPER.createLivingEntity("endimated_walking", EndimatedWalkingEntity::new, MobCategory.CREATURE, 1.0F, 1.0F);

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(TestEntities.COW.get(), Cow.createAttributes().build());
		event.put(TestEntities.COD.get(), Cod.createAttributes().build());
		event.put(TestEntities.ENDIMATED_TEST.get(), PathfinderMob.createMobAttributes().build());
		event.put(TestEntities.ENDIMATED_WALKING.get(), PathfinderMob.createMobAttributes().build());
	}
}
