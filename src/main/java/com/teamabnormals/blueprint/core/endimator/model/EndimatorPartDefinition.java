package com.teamabnormals.blueprint.core.endimator.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.teamabnormals.blueprint.core.endimator.EndimatorModelPart;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Similar to {@link net.minecraft.client.model.geom.builders.PartDefinition} but used to bake {@link EndimatorModelPart} instances.
 *
 * @author SmellyModder
 */
public final class EndimatorPartDefinition {
	private final List<CubeDefinition> cubes;
	private final EndimatorPartPose partPose;
	private final Map<String, EndimatorPartDefinition> children = Maps.newHashMap();

	private EndimatorPartDefinition(List<CubeDefinition> cubes, EndimatorPartPose partPose) {
		this.cubes = cubes;
		this.partPose = partPose;
	}

	/**
	 * Creates a new {@link EndimatorPartDefinition} instance that has no cubes and no children.
	 *
	 * @return A new {@link EndimatorPartDefinition} instance that has no cubes and no children.
	 */
	public static EndimatorPartDefinition root() {
		return new EndimatorPartDefinition(ImmutableList.of(), new EndimatorPartPose());
	}

	/**
	 * Essentially a tweaked version of {@link net.minecraft.client.model.geom.builders.PartDefinition#addOrReplaceChild(String, CubeListBuilder, PartPose)}.
	 *
	 * @param name            The name of the child to add.
	 * @param cubeListBuilder The {@link CubeListBuilder} instance for the cubes of the child.
	 * @param pose            An {@link EndimatorPartPose} instance to pose the child.
	 * @return The constructed child.
	 */
	public EndimatorPartDefinition addOrReplaceChild(String name, CubeListBuilder cubeListBuilder, EndimatorPartPose pose) {
		EndimatorPartDefinition partDefinition = new EndimatorPartDefinition(cubeListBuilder.getCubes(), pose);
		EndimatorPartDefinition previous = this.children.put(name, partDefinition);
		if (previous != null) previous.children.putAll(partDefinition.children);
		return partDefinition;
	}

	/**
	 * An alternative version of {@link #addOrReplaceChild(String, CubeListBuilder, EndimatorPartPose)} that takes in a {@link PartPose} instance.
	 *
	 * @param name            The name of the child to add.
	 * @param cubeListBuilder The {@link CubeListBuilder} instance for the cubes of the child.
	 * @param pose            An {@link PartPose} instance to pose the child.
	 * @return The constructed child.
	 */
	public EndimatorPartDefinition addOrReplaceChild(String name, CubeListBuilder cubeListBuilder, PartPose pose) {
		return this.addOrReplaceChild(name, cubeListBuilder, new EndimatorPartPose().setPartPose(pose));
	}

	/**
	 * Bakes this part and its children.
	 *
	 * @param xTexSize The x texture size.
	 * @param yTexSize The y texture size.
	 * @return A {@link EndimatorModelPart} instance representing a baked version of this part.
	 */
	public EndimatorModelPart bake(int xTexSize, int yTexSize) {
		Object2ObjectArrayMap<String, ModelPart> bakedChildren = this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> entry.getValue().bake(xTexSize, yTexSize), (modelPart, modelPart2) -> modelPart, Object2ObjectArrayMap::new));
		List<ModelPart.Cube> bakedCubes = this.cubes.stream().map((cubeDefinition) -> cubeDefinition.bake(xTexSize, yTexSize)).collect(ImmutableList.toImmutableList());
		EndimatorModelPart modelpart = new EndimatorModelPart(bakedCubes, bakedChildren);
		modelpart.loadPose(this.partPose);
		return modelpart;
	}

	/**
	 * Gets a child part by its name.
	 *
	 * @param name The name of the child part.
	 * @return The child part for the given name.
	 */
	public EndimatorPartDefinition getChild(String name) {
		return this.children.get(name);
	}
}
