package com.minecraftabnormals.abnormals_core.core.endimator.instructions;

import com.minecraftabnormals.abnormals_core.core.util.registry.BasicRegistry;
import net.minecraft.resources.ResourceLocation;

/**
 * Registry class for {@link EndimationInstruction}s.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class EndimationInstructions {
	public static final BasicRegistry<EndimationInstruction<?>> REGISTRY = new BasicRegistry<>();

	static {
		register(new ResourceLocation("start_keyframe"), new StartKeyframeInstruction());
		register(new ResourceLocation("end_keyframe"), new EndKeyframeInstruction());
		register(new ResourceLocation("static_keyframe"), new StaticKeyframeInstruction());
		register(new ResourceLocation("reset_keyframe"), new ResetKeyframeInstruction());
		register(new ResourceLocation("move"), new MoveInstruction());
		register(new ResourceLocation("offset"), new OffsetInstruction());
		register(new ResourceLocation("rotate"), new RotateInstruction());
		register(new ResourceLocation("scale"), new ScaleInstruction());
	}

	/**
	 * Use this method to register an {@link EndimationInstruction} during the initialization of your mod.
	 *
	 * @param name        The registry name for this instruction.
	 * @param instruction The instruction to register.
	 */
	public static synchronized <E extends EndimationInstruction<?>> void register(ResourceLocation name, E instruction) {
		REGISTRY.register(name, instruction);
	}
}
