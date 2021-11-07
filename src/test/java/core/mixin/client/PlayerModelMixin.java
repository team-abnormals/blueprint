package core.mixin.client;

import client.TestClientEvents;
import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.Endimator;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public final class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {

	private PlayerModelMixin(ModelPart part) {
		super(part);
	}

	@Inject(at = @At("HEAD"), method = "setupAnim")
	private void setupAnimPre(T entity, float p_103396_, float p_103397_, float p_103398_, float p_103399_, float p_103400_, CallbackInfo info) {
		//Reset any transformations we've done to the model. This make sure values that are not already reset by the player model get reset
		TestClientEvents.HUMANOID_ENDIMATORS.resetEndimator(this, Endimator.ResetMode.ALL);
	}

	@Inject(at = @At("RETURN"), method = "setupAnim")
	private void setupAnimPost(T entity, float p_103396_, float p_103397_, float ticksPlusPartialTicks, float p_103399_, float p_103400_, CallbackInfo info) {
		//Process transformations of the currently playing endimation on the model
		TestClientEvents.HUMANOID_ENDIMATORS.endimate(this, Endimator.ResetMode.NONE, (Endimatable) entity, (ticksPlusPartialTicks - entity.tickCount));
	}

}
