package client;

import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.client.screen.splash.Splash;
import net.minecraft.client.User;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;

public enum TestCustomSplash implements Splash {
	INSTANCE;

	public static final MapCodec<TestCustomSplash> CODEC = MapCodec.unit(INSTANCE);

	@Nullable
	@Override
	public String getText(User user, RandomSource random) {
		return random.nextBoolean() ? "Facts Brother, So True my friend" : null;
	}

	@Override
	public boolean isRandom() {
		return false;
	}

	@Override
	public MapCodec<? extends Splash> codec() {
		return CODEC;
	}
}
