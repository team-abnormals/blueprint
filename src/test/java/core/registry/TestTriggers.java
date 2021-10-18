package core.registry;

import com.teamabnormals.blueprint.common.advancement.EmptyTrigger;
import core.BlueprintTest;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID)
public final class TestTriggers {
	public static final EmptyTrigger EMPTY_TEST = CriteriaTriggers.register(new EmptyTrigger(prefix("empty_test")));

	private static ResourceLocation prefix(String name) {
		return new ResourceLocation(BlueprintTest.MOD_ID, name);
	}
}
