package core.registry;

import com.minecraftabnormals.abnormals_core.core.annotations.Test;
import com.minecraftabnormals.abnormals_core.core.util.registry.EntitySubRegistryHelper;
import common.entities.TestEndimatedEntity;
import core.ACTest;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.CowEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Test
@Mod.EventBusSubscriber(modid = ACTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TestEntities {
	private static final EntitySubRegistryHelper HELPER = ACTest.REGISTRY_HELPER.getEntitySubHelper();

	public static final RegistryObject<EntityType<CowEntity>> COW = HELPER.createLivingEntity("example", CowEntity::new, EntityClassification.CREATURE, 1.0F, 1.0F);
	public static final RegistryObject<EntityType<TestEndimatedEntity>> ENDIMATED_TEST = HELPER.createLivingEntity("endimated_test", TestEndimatedEntity::new, EntityClassification.CREATURE, 1.0F, 1.0F);

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(TestEntities.ENDIMATED_TEST.get(), CreatureEntity.func_233666_p_().create());
		event.put(TestEntities.COW.get(), CowEntity.func_234188_eI_().create());
	}
}
