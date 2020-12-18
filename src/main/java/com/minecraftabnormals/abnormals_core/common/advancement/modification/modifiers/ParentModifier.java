package com.minecraftabnormals.abnormals_core.common.advancement.modification.modifiers;

import com.minecraftabnormals.abnormals_core.common.advancement.modification.AdvancementModifier;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;

/**
 * An {@link AdvancementModifier} extension that modifies the parent advancement of an advancement.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class ParentModifier extends AdvancementModifier<ResourceLocation> {

	public ParentModifier() {
		super(((element, conditionArrayParser) -> new ResourceLocation(element.getAsString())));
	}

	@Override
	public void modify(Advancement.Builder builder, ResourceLocation location) {
		builder.withParentId(location);
	}

}
