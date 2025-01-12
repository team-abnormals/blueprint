package core.registry;

import core.BlueprintTest;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = BlueprintTest.MOD_ID)
public final class TestTriggers {
	public static final PlayerTrigger EMPTY_TEST = CriteriaTriggers.register(prefix("empty_test"), new PlayerTrigger());

	private static String prefix(String name) {
		return BlueprintTest.MOD_ID + ":" + name;
	}
}
