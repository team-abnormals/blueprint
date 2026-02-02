package core.data.client;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.common.remolder.data.RemolderProvider;
import core.BlueprintTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static com.teamabnormals.blueprint.common.remolder.RemolderTypes.*;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.*;

public final class TestAssetsRemolderProvider extends RemolderProvider {

	public TestAssetsRemolderProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(BlueprintTest.MOD_ID, PackOutput.Target.RESOURCE_PACK, packOutput, lookupProvider);
	}

	@Override
	protected void registerEntries(HolderLookup.Provider provider) {
		this.entry("bow_arrow_test")
				.path("minecraft:models/item/bow")
				.remolder(replace(
						target("overrides[2].model"),
						value("item/diamond", Codec.STRING)
				));
		this.entry("rotate_acacia_planks_blockstate")
				.path("minecraft:blockstates/acacia_planks")
				.pack(Sets.newHashSet("vanilla"))
				.remolder(add(
						target("variants[\"\"].x"),
						parse("#element(90)")
				));
	}

}
