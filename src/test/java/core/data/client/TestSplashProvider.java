package core.data.client;

import client.TestCustomSplash;
import com.teamabnormals.blueprint.client.screen.splash.SplashProvider;
import core.BlueprintTest;
import net.minecraft.data.PackOutput;

public final class TestSplashProvider extends SplashProvider {

	public TestSplashProvider(PackOutput packOutput) {
		super(BlueprintTest.MOD_ID, packOutput);
	}

	@Override
	protected void registerSplashes() {
		this.add("Blueprint Test!");
		this.add(TestCustomSplash.INSTANCE);
	}

}
