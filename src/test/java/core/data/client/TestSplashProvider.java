package core.data.client;

import client.TestCustomSplash;
import com.teamabnormals.blueprint.client.screen.splash.SplashProvider;
import core.BlueprintTest;
import net.minecraft.data.DataGenerator;

public final class TestSplashProvider extends SplashProvider {

	public TestSplashProvider(DataGenerator dataGenerator) {
		super(BlueprintTest.MOD_ID, dataGenerator);
	}

	@Override
	protected void registerSplashes() {
		this.add("Blueprint Test!");
		this.add(TestCustomSplash.INSTANCE);
	}

}
