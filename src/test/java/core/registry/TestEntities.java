package core.registry;

import com.minecraftabnormals.abnormals_core.core.annotations.Test;
import com.minecraftabnormals.abnormals_core.core.util.registry.EntitySubRegistryHelper;
import common.entities.TestEndimatedEntity;
import core.ACTest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Cow;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;

@Test
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestEntities {
	private static final EntitySubRegistryHelper HELPER = ACTest.REGISTRY_HELPER.getEntitySubHelper();

	public static final RegistryObject<EntityType<Cow>> COW = HELPER.createLivingEntity("example", Cow::new, MobCategory.CREATURE, 1.0F, 1.0F);
	public static final RegistryObject<EntityType<TestEndimatedEntity>> ENDIMATED_TEST = HELPER.createLivingEntity("endimated_test", TestEndimatedEntity::new, MobCategory.CREATURE, 1.0F, 1.0F);

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(TestEntities.ENDIMATED_TEST.get(), PathfinderMob.createMobAttributes().build());
		event.put(TestEntities.COW.get(), Cow.createAttributes().build());
	}
}
