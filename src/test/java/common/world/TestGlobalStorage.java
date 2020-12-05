package common.world;

import com.minecraftabnormals.abnormals_core.common.world.storage.GlobalStorage;
import com.minecraftabnormals.abnormals_core.core.annotations.Test;
import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

@Test
public final class TestGlobalStorage implements GlobalStorage {
	private int testInt;

	public TestGlobalStorage() {
		this.testInt = new Random().nextInt(500);
	}

	@Override
	public CompoundNBT toTag() {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("TestInt", this.testInt);
		return compound;
	}

	@Override
	public void fromTag(CompoundNBT tag) {
		int loadedTestInt = tag.getInt("TestInt");
		System.out.println(String.format("Loaded Test Integer with value: %o", loadedTestInt));
		this.testInt = loadedTestInt;
	}
}
