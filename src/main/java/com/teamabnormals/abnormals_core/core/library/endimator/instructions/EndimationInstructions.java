package com.teamabnormals.abnormals_core.core.library.endimator.instructions;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class EndimationInstructions {
	public static final InstructionRegistry REGISTRY = new InstructionRegistry();
	
	static {
		register("start_keyframe", new StartKeyframeInstruction());
		register("end_keyframe", new EndKeyframeInstruction());
		register("static_keyframe", new StaticKeyframeInstruction());
		register("reset_keyframe", new ResetKeyframeInstruction());
		register("move", new MoveInstruction());
		register("offset", new OffsetInstruction());
		register("rotate", new RotateInstruction());
		register("scale", new ScaleInstruction());
	}
	
	/**
	 * Use this method to register an {@link EndimationInstruction} during the initialization of your mod.
	 * @param name - The registry name for this instruction.
	 * @param instruction - The instruction to register.
	 */
	public static synchronized <E extends EndimationInstruction<?>> void register(String name, E instruction) {
		REGISTRY.register(name, instruction);
	}

	public static class InstructionRegistry implements Codec<EndimationInstruction<?>> {
		private final Lifecycle lifecycle;
		private final BiMap<String, EndimationInstruction<?>> map = HashBiMap.create();
		
		InstructionRegistry() {
			this.lifecycle = Lifecycle.stable();
		}
		
		private <E extends EndimationInstruction<?>> void register(String instructionName, E instruction) {
			this.map.put(instructionName, instruction);
		}

		@Override
		public <T> DataResult<T> encode(EndimationInstruction<?> input, DynamicOps<T> ops, T prefix) {
			return ops.mergeToPrimitive(prefix, ops.createString(this.map.inverse().get(input))).setLifecycle(this.lifecycle);
		}

		@Override
		public <T> DataResult<Pair<EndimationInstruction<?>, T>> decode(DynamicOps<T> ops, T input) {
			return Codec.STRING.decode(ops, input).addLifecycle(this.lifecycle).flatMap(pair -> {
				String name = pair.getFirst();
				if (!this.map.containsKey(name)) {
					return DataResult.error("Unknown Endimation Instruction: " + name);
				}
				return DataResult.success(pair.mapFirst(string -> this.map.get(string)), this.lifecycle);
			});
		}
	}
}
