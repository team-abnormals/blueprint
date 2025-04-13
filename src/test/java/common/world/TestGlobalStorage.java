package common.world;

import com.teamabnormals.blueprint.common.world.storage.GlobalStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public final class TestGlobalStorage implements GlobalStorage {
	private int testInt;

	public TestGlobalStorage() {
		this.testInt = new Random().nextInt(500);
	}

	@Override
	public CompoundTag createTag(HolderLookup.Provider provider) {
		CompoundTag compound = new CompoundTag();
		compound.putInt("TestInt", this.testInt);
		return compound;
	}

	@Override
	public void fromTag(CompoundTag tag, HolderLookup.Provider provider) {
		int loadedTestInt = tag.getInt("TestInt");
		System.out.println(String.format("Loaded Test Integer with value: %o", loadedTestInt));
		this.testInt = loadedTestInt;
	}
}
