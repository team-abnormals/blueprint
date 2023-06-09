package com.teamabnormals.blueprint.core.api.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * A {@link SimpleUnbakedGeometry} subclass where its parts are full-bright.
 */
public class FullbrightModel extends SimpleUnbakedGeometry<FullbrightModel> {
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(FullbrightBlockPart.class, new FullbrightBlockPart.Deserializer())
			.registerTypeAdapter(FullbrightBlockPartFace.class, new FullbrightBlockPartFace.Deserializer())
			.registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer())
			.create();
	private static final IQuadTransformer MAX_LIGHTMAP_TRANSFORMER = QuadTransformers.applyingLightmap(0x00F000F0);

	private final List<FullbrightBlockPart> elements;

	private FullbrightModel(List<FullbrightBlockPart> list) {
		this.elements = list;
	}

	@Override
	protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
		for (FullbrightBlockPart part : this.elements) {
			for (Map.Entry<Direction, BlockElementFace> entry : part.faces.entrySet()) {
				FullbrightBlockPartFace face = (FullbrightBlockPartFace) entry.getValue();
				BakedQuad quad = BlockModel.bakeFace(part, face, spriteGetter.apply(owner.getMaterial(face.texture)), entry.getKey(), modelTransform, modelLocation);
				if ((!face.override && part.fullbright) || (face.override && face.fullbright))
					MAX_LIGHTMAP_TRANSFORMER.processInPlace(quad);

				if (face.cullForDirection == null) {
					modelBuilder.addUnculledFace(quad);
				} else {
					modelBuilder.addCulledFace(modelTransform.getRotation().rotateTransform(face.cullForDirection), quad);
				}
			}
		}
	}

	/**
	 * An {@link IGeometryLoader} implementation for {@link FullbrightModel}s.
	 */
	public enum Loader implements IGeometryLoader<FullbrightModel> {
		INSTANCE;

		@Override
		public FullbrightModel read(JsonObject model, JsonDeserializationContext context) {
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
