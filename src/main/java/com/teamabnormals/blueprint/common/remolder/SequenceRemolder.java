package com.teamabnormals.blueprint.common.remolder;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

/**
 * A {@link Remolder} implementation that runs multiple remolders in an efficient sequential order.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record SequenceRemolder(List<Remolder> remolders) implements Remolder {
	public static final Codec<SequenceRemolder> CODEC = ExtraCodecs.nonEmptyList(Remolder.CODEC.listOf()).fieldOf("remolders").codec().xmap(SequenceRemolder::new, SequenceRemolder::remolders);

	@Override
	public Remold remold() throws Exception {
		List<Remolder> remolders = this.remolders;
		int remoldersSize = remolders.size();
		Remold.Visitor visitor;
		if (remoldersSize == 1) {
			visitor = (molding, owner, method) -> remolders.get(0).remold().visitor().visit(molding, owner, method);
		} else {
			visitor = (molding, owner, method) -> {
				for (Remolder remolder : remolders) remolder.remold().visitor().visit(molding, owner, method);
			};
		}
		Remold.Fields fields = new Remold.Fields();
		for (Remolder remolder : remolders) fields.addFields(remolder.remold().fields());
		return new Remold(this.getClass().getSimpleName(), visitor, fields);
	}

	@Override
	public Codec<? extends Remolder> codec() {
		return CODEC;
	}
}
