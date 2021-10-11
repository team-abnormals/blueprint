package com.minecraftabnormals.abnormals_core.core.api.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;
import net.minecraftforge.client.model.pipeline.LightUtil;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/**
 * An {@link ISimpleModelGeometry} implementation where its parts are full-bright.
 */
public class FullbrightModel implements ISimpleModelGeometry<FullbrightModel> {
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(FullbrightBlockPart.class, new FullbrightBlockPart.Deserializer())
			.registerTypeAdapter(FullbrightBlockPartFace.class, new FullbrightBlockPartFace.Deserializer())
			.registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer())
			.create();

	private final List<FullbrightBlockPart> elements;

	private FullbrightModel(List<FullbrightBlockPart> list) {
		this.elements = list;
	}

	@Override
	public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
		for (FullbrightBlockPart part : this.elements) {
			for (Map.Entry<Direction, BlockElementFace> entry : part.faces.entrySet()) {
				FullbrightBlockPartFace face = (FullbrightBlockPartFace) entry.getValue();
				BakedQuad quad = BlockModel.makeBakedQuad(part, face, spriteGetter.apply(owner.resolveTexture(face.texture)), entry.getKey(), modelTransform, modelLocation);
				if ((!face.override && part.fullbright) || (face.override && face.fullbright))
					LightUtil.setLightData(quad, 0xF000F0);

				if (face.cullForDirection == null) {
					modelBuilder.addGeneralQuad(quad);
				} else {
					modelBuilder.addFaceQuad(modelTransform.getRotation().rotateTransform(face.cullForDirection), quad);
				}
			}
		}
	}

	@Override
	public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		Set<Material> textures = Sets.newHashSet();
		for (FullbrightBlockPart part : this.elements) {
			for (BlockElementFace face : part.faces.values()) {
				textures.add(owner.resolveTexture(face.texture));
			}
		}

		return textures;
	}

	/**
	 * An {@link IModelLoader} implementation for {@link FullbrightModel}s.
	 */
	public enum Loader implements IModelLoader<FullbrightModel> {
		INSTANCE;

		@Override
		public void onResourceManagerReload(ResourceManager manager) {
		}

		@Override
		public FullbrightModel read(JsonDeserializationContext context, JsonObject model) {
			List<FullbrightBlockPart> list = Lists.newArrayList();
			if (model.has("elements")) {
				for (JsonElement jsonelement : GsonHelper.getAsJsonArray(model, "elements")) {
					list.add(GSON.fromJson(jsonelement, FullbrightBlockPart.class));
				}
			}
			return new FullbrightModel(list);
		}
	}

	/**
	 * A {@link BlockElementFace} extension that is full-bright.
	 */
	private static class FullbrightBlockPartFace extends BlockElementFace {
		private final boolean fullbright;
		private final boolean override;

		private FullbrightBlockPartFace(@Nullable Direction cullFaceIn, int tintIndexIn, String textureIn, BlockFaceUV blockFaceUVIn, boolean fullbright, boolean override) {
			super(cullFaceIn, tintIndexIn, textureIn, blockFaceUVIn);
			this.fullbright = fullbright;
			this.override = override;
		}

		private static class Deserializer extends BlockElementFace.Deserializer {
			@Override
			public BlockElementFace deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
				BlockElementFace face = super.deserialize(element, type, context);
				JsonObject object = element.getAsJsonObject();

				if (object.has("fullbright") && !GsonHelper.isBooleanValue(object, "fullbright"))
					throw new JsonParseException("Expected fullbright to be a Boolean");
				return new FullbrightBlockPartFace(
						face.cullForDirection,
						face.tintIndex,
						face.texture,
						face.uv,
						GsonHelper.getAsBoolean(object, "fullbright", false),
						object.has("fullbright")
				);
			}
		}
	}

	/**
	 * A {@link BlockElement} extension that is full-bright.
	 */
	private static class FullbrightBlockPart extends BlockElement {
		private final boolean fullbright;

		private FullbrightBlockPart(Vector3f positionFrom, Vector3f positionTo, Map<Direction, BlockElementFace> mapFaces, @Nullable BlockElementRotation partRotation, boolean shade, boolean fullbright) {
			super(positionFrom, positionTo, mapFaces, partRotation, shade);
			this.fullbright = fullbright;
		}

		private static class Deserializer implements JsonDeserializer<FullbrightBlockPart> {
			@Override
			public FullbrightBlockPart deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
				JsonObject object = element.getAsJsonObject();
				Vector3f from = this.validateVectorBounds(object, "from");
				Vector3f to = this.validateVectorBounds(object, "to");
				BlockElementRotation rotation = this.parseRotation(object);
				Map<Direction, BlockElementFace> faces = this.parseFacesCheck(object);

				if (object.has("shade") && !GsonHelper.isBooleanValue(object, "shade"))
					throw new JsonParseException("Expected shade to be a Boolean");
				if (object.has("fullbright") && !GsonHelper.isBooleanValue(object, "fullbright"))
					throw new JsonParseException("Expected fullbright to be a Boolean");

				return new FullbrightBlockPart(from, to, faces, rotation, GsonHelper.getAsBoolean(object, "shade", true), GsonHelper.getAsBoolean(object, "fullbright", false));
			}

			@Nullable
			private BlockElementRotation parseRotation(JsonObject object) {
				BlockElementRotation rotation = null;
				if (object.has("rotation")) {
					JsonObject rotObject = GsonHelper.getAsJsonObject(object, "rotation");
					Vector3f origin = this.deserializeVec3f(rotObject, "origin");
					origin.mul(0.0625F);
					rotation = new BlockElementRotation(origin, this.parseAxis(rotObject), this.parseAngle(rotObject), GsonHelper.getAsBoolean(rotObject, "rescale", false));
				}

				return rotation;
			}

			private float parseAngle(JsonObject object) {
				float angle = GsonHelper.getAsFloat(object, "angle");
				if (angle != 0.0F && Mth.abs(angle) != 22.5F && Mth.abs(angle) != 45.0F) {
					throw new JsonParseException("Invalid rotation " + angle + " found, only -45/-22.5/0/22.5/45 allowed");
				}

				return angle;
			}

			private Direction.Axis parseAxis(JsonObject object) {
				String axisName = GsonHelper.getAsString(object, "axis");
				Direction.Axis axis = Direction.Axis.byName(axisName.toLowerCase(Locale.ROOT));
				if (axis == null) {
					throw new JsonParseException("Invalid rotation axis: " + axisName);
				}

				return axis;
			}

			private Map<Direction, BlockElementFace> parseFacesCheck(JsonObject object) {
				Map<Direction, BlockElementFace> faces = this.parseFaces(object);
				if (faces.isEmpty()) {
					throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
				}

				return faces;
			}

			private Map<Direction, BlockElementFace> parseFaces(JsonObject object) {
				Map<Direction, BlockElementFace> faces = Maps.newEnumMap(Direction.class);
				JsonObject facesObject = GsonHelper.getAsJsonObject(object, "faces");
				for (Map.Entry<String, JsonElement> entry : facesObject.entrySet()) {
					Direction direction = this.parseEnumFacing(entry.getKey());
					faces.put(direction, GSON.fromJson(entry.getValue(), FullbrightBlockPartFace.class));
				}

				return faces;
			}

			private Direction parseEnumFacing(String name) {
				Direction direction = Direction.byName(name);
				if (direction == null) {
					throw new JsonParseException("Unknown facing: " + name);
				}

				return direction;
			}

			private Vector3f validateVectorBounds(JsonObject object, String name) {
				Vector3f vector = this.deserializeVec3f(object, name);
				if (vector.x() >= -16.0F && vector.y() >= -16.0F && vector.z() >= -16.0F && vector.x() <= 32.0F && vector.y() <= 32.0F && vector.z() <= 32.0F) {
					return vector;
				}

				throw new JsonParseException("'" + name + "' specifier exceeds the allowed boundaries: " + vector);
			}

			private Vector3f deserializeVec3f(JsonObject object, String name) {
				JsonArray vectorJson = GsonHelper.getAsJsonArray(object, name);
				if (vectorJson.size() != 3) {
					throw new JsonParseException("Expected 3 " + name + " values, found: " + vectorJson.size());
				}

				float[] vector = new float[3];
				for (int i = 0; i < 3; ++i) {
					vector[i] = GsonHelper.convertToFloat(vectorJson.get(i), name + "[" + i + "]");
				}

				return new Vector3f(vector[0], vector[1], vector[2]);
			}
		}
	}
}
