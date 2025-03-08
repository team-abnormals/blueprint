package com.teamabnormals.blueprint.common.world.modification.structure;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Interface mixin'd into {@link net.minecraft.world.level.levelgen.structure.StructureStart} for internal use.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface RepalettedStructureStart {
	void initializeRepaletters(StructureModificationContext context);

	void setRepaletters(@Nullable ArrayList<StructureRepaletterManager.Entry> repaletters);
}
