package core.registry;

import core.BlueprintTest;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PlayerTrigger;

// TODO: Remove
public final class TestTriggers {
	public static final PlayerTrigger EMPTY_TEST = CriteriaTriggers.register(prefix("empty_test"), new PlayerTrigger());

	private static String prefix(String name) {
		return BlueprintTest.MOD_ID + ":" + name;
	}
}
