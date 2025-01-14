package core.registry;

import com.teamabnormals.blueprint.core.api.BlueprintRabbitVariants;
import core.BlueprintTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = BlueprintTest.MOD_ID)
public class TestRabbitVariants extends BlueprintRabbitVariants {
	private static final int UNIQUE_OFFSET = 11555;

	public static final BlueprintRabbitVariant CRAIG = register(UNIQUE_OFFSET, ResourceLocation.fromNamespaceAndPath(BlueprintTest.MOD_ID, "craig"), context -> getBiome(context).is(BiomeTags.IS_END));
}