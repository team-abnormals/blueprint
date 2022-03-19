package com.teamabnormals.blueprint.core.endimator;

import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The core class of Endimator used to apply {@link Endimation} instances to {@link EndimatablePart} instances. Endimator: Animator for {@link Endimation} instances.
 * <p>This class works by storing <b>additive</b> animation values for {@link EndimatablePart} instances mapped to strings.</p>
 * <p>Multiple instances of this class can be safely used on the same set of {@link EndimatablePart} instances in the same frame when {@link ResetMode} is properly used.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see Endimation
 * @see EndimatablePart
 */
public final class Endimator {
	private static final Vector3f ADD_VECTOR = new Vector3f();
	private final Map<String, PosedPart> poseMap;

	public Endimator(Map<String, PosedPart> poseMap) {
		this.poseMap = poseMap;
	}

	public Endimator() {
		this.poseMap = new HashMap<>();
	}

	/**
	 * Shortly compiles a new {@link Endimator} instance by recursively putting children from a given root part.
	 * <p>Named 'short' as names of children are not mapped out in a path representation.</p>
	 * <p>Full Path: body/head</p>
	 * <p>Short: head</p>
	 *
	 * @param root A root {@link ModelPart} to use.
	 * @return A new {@link Endimator} instance containing the recursive children of a given root part.
	 */
	public static Endimator compile(ModelPart root) {
		return new Endimator(compileMap(new HashMap<>(), root));
	}

	/**
	 * Compiles a new {@link Endimator} instance by recursively putting children from a given root part.
	 * <p>The children are mapped out in a path format. Use {@link #compile(ModelPart)} if this behavior is not desired.</p>
	 *
	 * @param root A root {@link ModelPart} to use.
	 * @return A new {@link Endimator} instance containing the recursive children of a given root part.
	 */
	public static Endimator treeCompile(ModelPart root) {
		return new Endimator(compileMap(new HashMap<>(), "", root));
	}

	private static Map<String, PosedPart> compileMap(Map<String, PosedPart> partMap, ModelPart root) {
		root.children.forEach((childName, childPart) -> {
			partMap.put(childName, PosedPart.part((EndimatablePart) childPart));
			if (!childPart.children.isEmpty()) {
				compileMap(partMap, childPart);
			}
		});
		return partMap;
	}

	private static Map<String, PosedPart> compileMap(Map<String, PosedPart> partMap, String prefix, ModelPart root) {
		root.children.forEach((childName, childPart) -> {
			String path = prefix + childName;
			partMap.put(path, PosedPart.part((EndimatablePart) childPart));
			if (!childPart.children.isEmpty()) {
				compileMap(partMap, path + "/", childPart);
			}
		});
		return partMap;
	}

	private static void applyType(PosedPart posedPart, KeyframeType type, EndimationKeyframe[] frames, float blendWeight, float time) {
		int length = frames.length;
		if (length <= 0) return;
		int fromIndex = Mth.binarySearch(0, length, i -> time <= frames[i].time) - 1;
		if (fromIndex < 0) fromIndex = 0;
		int toIndex = fromIndex + 1;
		if (toIndex > length - 1) toIndex = length - 1;
		EndimationKeyframe from = frames[fromIndex];
		float fromTime = from.time;
		EndimationKeyframe to = frames[toIndex];
		to.apply(ADD_VECTOR, frames, from, to, toIndex, length, Mth.clamp((time - fromTime) / (to.time - fromTime), 0.0F, 1.0F));
		ADD_VECTOR.mul(blendWeight);
		type.apply(posedPart, ADD_VECTOR);
	}

	/**
	 * Puts a {@link EndimatablePart} onto the {@link #poseMap}.
	 *
	 * @param name A name to use.
	 * @param part A {@link EndimatablePart} to put.
	 */
	public void put(String name, EndimatablePart part) {
		this.poseMap.put(name, PosedPart.part(part));
	}

	/**
	 * Puts a {@link ModelPart} and its children recursively onto the {@link #poseMap}.
	 *
	 * @param prefix  A name to use for the starting part.
	 * @param notRoot Used internally by the recursion algorithm. Always use false.
	 * @param part    A {@link ModelPart} to recursively put.
	 */
	public void putRecursive(String prefix, ModelPart part, boolean notRoot) {
		Map<String, PosedPart> poseMap = this.poseMap;
		poseMap.put(prefix, PosedPart.part((EndimatablePart) part));
		if (notRoot) {
			prefix = prefix + "/";
		}
		for (Map.Entry<String, ModelPart> entry : part.children.entrySet()) {
			this.putRecursive(prefix + entry.getKey(), entry.getValue(), true);
		}
	}

	/**
	 * Shortly puts a {@link ModelPart} and its children recursively onto the {@link #poseMap}.
	 * <p>Named 'short' as names of children are not mapped out in a path representation.</p>
	 * <p>Full Path: body/head</p>
	 * <p>Short: head</p>
	 *
	 * @param name A name to use for the starting part.
	 * @param part A {@link ModelPart} to recursively put.
	 */
	public void putShortRecursive(String name, ModelPart part) {
		this.poseMap.put(name, PosedPart.part((EndimatablePart) part));
		part.children.forEach(this::putShortRecursive);
	}

	/**
	 * Removes a {@link PosedPart} from the {@link #poseMap}.
	 *
	 * @param name A name to remove its corresponding {@link PosedPart}.
	 * @return The previous {@link PosedPart} associated with the name, or null if there was no mapping for name.
	 */
	@Nullable
	public PosedPart remove(String name) {
		return this.poseMap.remove(name);
	}

	/**
	 * Shortly removes a {@link ModelPart} and its children recursively from the {@link #poseMap}.
	 * <p>Named 'short' as names of children are not mapped out in a path representation.</p>
	 * <p>Full Path: body/head</p>
	 * <p>Short: head</p>
	 *
	 * @param name A name to remove its corresponding {@link PosedPart}.
	 * @param part A {@link ModelPart} to recursively remove.
	 */
	public void removeShortRecursive(String name, ModelPart part) {
		this.poseMap.remove(name);
		for (Map.Entry<String, ModelPart> entry : part.children.entrySet()) {
			this.removeShortRecursive(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Removes a {@link PosedPart} and its children recursively from the {@link #poseMap}.
	 *
	 * @param prefix  A name to use for the starting part.
	 * @param part    A {@link ModelPart} to recursively remove.
	 * @param notRoot Used internally by the recursion algorithm. Always use false.
	 */
	public void removeRecursive(String prefix, ModelPart part, boolean notRoot) {
		Map<String, PosedPart> poseMap = this.poseMap;
		poseMap.remove(prefix);
		if (notRoot) {
			prefix = prefix + "/";
		}
		for (Map.Entry<String, ModelPart> entry : part.children.entrySet()) {
			this.removeRecursive(prefix + entry.getKey(), entry.getValue(), true);
		}
	}

	/**
	 * Gets the {@link PosedPart} for a given name.
	 *
	 * @param name A name to lookup.
	 * @return The {@link PosedPart} for a given name, or null if no {@link PosedPart} exists for the given name.
	 */
	@Nullable
	public PosedPart getPosedPart(String name) {
		return this.poseMap.get(name);
	}

	/**
	 * Applies a {@link ResetMode} to every {@link PosedPart} in the {@link #poseMap}.
	 *
	 * @param resetMode A {@link ResetMode} to apply to the {@link #poseMap}.
	 */
	public void reset(ResetMode resetMode) {
		if (resetMode != ResetMode.NONE) {
			this.poseMap.values().forEach(resetMode.consumer);
		}
	}

	/**
	 * Clears this instance's {@link #poseMap}.
	 */
	public void clear() {
		this.poseMap.clear();
	}

	/**
	 * Applies an {@link Endimation} at a given time.
	 *
	 * @param endimation An {@link Endimation} to apply.
	 * @param time       The time passed since the start of the {@link Endimation}, measured in seconds.
	 * @param resetMode  A {@link ResetMode} to use for preparing the {@link #poseMap} for application.
	 */
	public void apply(Endimation endimation, float time, ResetMode resetMode) {
		this.apply(endimation, time, 1.0F, resetMode);
	}

	/**
	 * Applies an {@link Endimation} with a weight multiplier at a given time.
	 *
	 * @param endimation An {@link Endimation} to apply.
	 * @param time       The time passed since the start of the {@link Endimation}, measured in seconds.
	 * @param weight     The weight multiplier to use.
	 * @param resetMode  A {@link ResetMode} to use for preparing the {@link #poseMap} for application.
	 */
	public void apply(Endimation endimation, float time, float weight, ResetMode resetMode) {
		this.reset(resetMode);
		var partKeyframesIterator = Object2ObjectMaps.fastIterator(endimation.getPartKeyframes());
		var poseMap = this.poseMap;
		float blendWeight = endimation.getBlendWeight() * weight;
		while (partKeyframesIterator.hasNext()) {
			var entry = partKeyframesIterator.next();
			PosedPart posedPart = poseMap.get(entry.getKey());
			if (posedPart != null) {
				Endimation.PartKeyframes partKeyframes = entry.getValue();
				applyType(posedPart, KeyframeType.POSITION, partKeyframes.getPosFrames(), blendWeight, time);
				applyType(posedPart, KeyframeType.ROTATION, partKeyframes.getRotationFrames(), blendWeight, time);
				applyType(posedPart, KeyframeType.OFFSET, partKeyframes.getOffsetFrames(), blendWeight, time);
				applyType(posedPart, KeyframeType.SCALE, partKeyframes.getScaleFrames(), blendWeight, time);
				posedPart.apply();
			}
		}
	}

	/**
	 * The type of procedures to use when preparing {@link Endimator#poseMap} for the applying of an {@link Endimation}.
	 * <p>Named 'ResetMode' because the preparing is more like resetting the {@link Endimator#poseMap} to safely play an {@link Endimation}.</p>
	 * <p>{@link #RESET} will reset the values of a {@link PosedPart} to zero. This is useful for safely playing an {@link Endimation} after other manual animations have already played in the same frame.</p>
	 * <p>{@link #UNAPPLY} will revert the values added onto a {@link EndimatablePart} by the {@link PosedPart}. This is useful for safely playing an {@link Endimation} after other {@link Endimation}s have already played in the same frame.</p>
	 * <p>{@link #ALL} will combine {@link #RESET} and {@link #UNAPPLY}.</p>
	 * <p>{@link #NONE} will do nothing.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public enum ResetMode {
		NONE(posedPart -> {
		}),
		ALL(posedPart -> {
			posedPart.unapply();
			posedPart.reset();
		}),
		RESET(PosedPart::reset),
		UNAPPLY(PosedPart::unapply);

		private final Consumer<PosedPart> consumer;

		ResetMode(Consumer<PosedPart> consumer) {
			this.consumer = consumer;
		}

		public Consumer<PosedPart> getConsumer() {
			return this.consumer;
		}
	}

	/**
	 * A class containing <b>additive</b> values for all {@link KeyframeType}s for an {@link EndimatablePart}.
	 * <p>Used for storing animation values that can get reversed.</p>
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class PosedPart {
		public final EndimatablePart part;
		public float x, y, z;
		public float xRot, yRot, zRot;
		public float xOffset, yOffset, zOffset;
		public float xScale, yScale, zScale;

		public PosedPart(EndimatablePart part) {
			this.part = part;
		}

		/**
		 * A new {@link PosedPart} with default values for a given {@link EndimatablePart}.
		 *
		 * @param part A {@link EndimatablePart} to contain.
		 * @return A new {@link PosedPart} with default values for a given {@link EndimatablePart}.
		 */
		public static PosedPart part(EndimatablePart part) {
			return new PosedPart(part);
		}

		/**
		 * Applies the animation values.
		 */
		public void apply() {
			EndimatablePart part = this.part;
			part.addPos(this.x, this.y, this.z);
			part.addRotation(this.xRot, this.yRot, this.zRot);
			part.addOffset(this.xOffset, this.yOffset, this.zOffset);
			part.addScale(this.xScale, this.yScale, this.zScale);
		}

		/**
		 * Unapplies the animation values.
		 */
		public void unapply() {
			EndimatablePart part = this.part;
			part.addPos(-this.x, -this.y, -this.z);
			part.addRotation(-this.xRot, -this.yRot, -this.zRot);
			part.addOffset(-this.xOffset, -this.yOffset, -this.zOffset);
			part.addScale(-this.xScale, -this.yScale, -this.zScale);
		}

		/**
		 * Resets the animation values.
		 */
		public void reset() {
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 0.0F;
			this.xRot = 0.0F;
			this.yRot = 0.0F;
			this.zRot = 0.0F;
			this.xOffset = 0.0F;
			this.yOffset = 0.0F;
			this.zOffset = 0.0F;
			this.xScale = 0.0F;
			this.yScale = 0.0F;
			this.zScale = 0.0f;
		}

		/**
		 * Adds positional values.
		 *
		 * @param x The x pos to add.
		 * @param y The y pos to add.
		 * @param z The z pos to add.
		 */
		public void addPos(float x, float y, float z) {
			this.x += x;
			this.y += y;
			this.z += z;
		}

		/**
		 * Adds rotational values.
		 *
		 * @param x The x rotation to add.
		 * @param y The y rotation to add.
		 * @param z The z rotation to add.
		 */
		public void addRotation(float x, float y, float z) {
			this.xRot += x;
			this.yRot += y;
			this.zRot += z;
		}

		/**
		 * Adds offset values.
		 *
		 * @param x The x offset to add.
		 * @param y The y offset to add.
		 * @param z The z offset to add.
		 */
		public void addOffset(float x, float y, float z) {
			this.xOffset += x;
			this.yOffset += y;
			this.zOffset += z;
		}

		/**
		 * Adds scale values.
		 *
		 * @param x The x scale to add.
		 * @param y The y scale to add.
		 * @param z The z scale to add.
		 */
		public void addScale(float x, float y, float z) {
			this.xScale += x;
			this.yScale += y;
			this.zScale += z;
		}

		/**
		 * Additively applies a consumer onto this {@link PosedPart}.
		 * <p>This is here to provide a simple way to change a part's values outside of {@link Endimator#apply(Endimation, float, ResetMode)}.</p>
		 *
		 * @param consumer A {@link Consumer} to accept.
		 */
		public void applyAdd(Consumer<PosedPart> consumer) {
			this.unapply();
			consumer.accept(this);
			this.apply();
		}
	}
}
