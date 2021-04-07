package com.minecraftabnormals.abnormals_core.core.api.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;
import net.minecraftforge.client.model.pipeline.LightUtil;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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
    public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
        for (FullbrightBlockPart part : this.elements) {
            for (Map.Entry<Direction, BlockPartFace> entry : part.mapFaces.entrySet()) {
                FullbrightBlockPartFace face = (FullbrightBlockPartFace) entry.getValue();
                BakedQuad quad = BlockModel.makeBakedQuad(part, face, spriteGetter.apply(owner.resolveTexture(face.texture)), entry.getKey(), modelTransform, modelLocation);
                if ((!face.override && part.fullbright) || (face.override && face.fullbright))
                    LightUtil.setLightData(quad, 0xF000F0);

                if (face.cullFace == null) {
                    modelBuilder.addGeneralQuad(quad);
                } else {
                    modelBuilder.addFaceQuad(modelTransform.getRotation().rotateTransform(face.cullFace), quad);
                }
            }
        }
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Set<RenderMaterial> textures = Sets.newHashSet();
        for (FullbrightBlockPart part : this.elements) {
            for (BlockPartFace face : part.mapFaces.values()) {
                textures.add(owner.resolveTexture(face.texture));
            }
        }

        return textures;
    }

    public enum Loader implements IModelLoader<FullbrightModel> {
        INSTANCE;

        @Override
        public void onResourceManagerReload(IResourceManager manager) {}

        @Override
        public FullbrightModel read(JsonDeserializationContext context, JsonObject model) {
            List<FullbrightBlockPart> list = Lists.newArrayList();
            if (model.has("elements")) {
                for (JsonElement jsonelement : JSONUtils.getJsonArray(model, "elements")) {
                    list.add(GSON.fromJson(jsonelement, FullbrightBlockPart.class));
                }
            }
            return new FullbrightModel(list);
        }
    }

    private static class FullbrightBlockPartFace extends BlockPartFace {
        private final boolean fullbright;
        private final boolean override;

        private FullbrightBlockPartFace(@Nullable Direction cullFaceIn, int tintIndexIn, String textureIn, BlockFaceUV blockFaceUVIn, boolean fullbright, boolean override) {
            super(cullFaceIn, tintIndexIn, textureIn, blockFaceUVIn);
            this.fullbright = fullbright;
            this.override = override;
        }

        private static class Deserializer extends BlockPartFace.Deserializer {
            @Override
            public BlockPartFace deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
                BlockPartFace face = super.deserialize(element, type, context);
                JsonObject object = element.getAsJsonObject();

                if (object.has("fullbright") && !JSONUtils.isBoolean(object, "fullbright"))
                    throw new JsonParseException("Expected fullbright to be a Boolean");
                return new FullbrightBlockPartFace(
                        face.cullFace,
                        face.tintIndex,
                        face.texture,
                        face.blockFaceUV,
                        JSONUtils.getBoolean(object, "fullbright", false),
                        object.has("fullbright")
                );
            }
        }
    }

    private static class FullbrightBlockPart extends BlockPart {
        private final boolean fullbright;

        private FullbrightBlockPart(Vector3f positionFrom, Vector3f positionTo, Map<Direction, BlockPartFace> mapFaces, @Nullable BlockPartRotation partRotation, boolean shade, boolean fullbright) {
            super(positionFrom, positionTo, mapFaces, partRotation, shade);
            this.fullbright = fullbright;
        }

        private static class Deserializer implements JsonDeserializer<FullbrightBlockPart> {
            @Override
            public FullbrightBlockPart deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
                JsonObject object = element.getAsJsonObject();
                Vector3f from = this.validateVectorBounds(object, "from");
                Vector3f to = this.validateVectorBounds(object, "to");
                BlockPartRotation rotation = this.parseRotation(object);
                Map<Direction, BlockPartFace> faces = this.parseFacesCheck(object);

                if (object.has("shade") && !JSONUtils.isBoolean(object, "shade"))
                    throw new JsonParseException("Expected shade to be a Boolean");
                if (object.has("fullbright") && !JSONUtils.isBoolean(object, "fullbright"))
                    throw new JsonParseException("Expected fullbright to be a Boolean");

                return new FullbrightBlockPart(from, to, faces, rotation, JSONUtils.getBoolean(object, "shade", true), JSONUtils.getBoolean(object, "fullbright", false));
            }

            @Nullable
            private BlockPartRotation parseRotation(JsonObject object) {
                BlockPartRotation rotation = null;
                if (object.has("rotation")) {
                    JsonObject rotObject = JSONUtils.getJsonObject(object, "rotation");
                    Vector3f origin = this.deserializeVec3f(rotObject, "origin");
                    origin.mul(0.0625F);
                    rotation = new BlockPartRotation(origin, this.parseAxis(rotObject), this.parseAngle(rotObject), JSONUtils.getBoolean(rotObject, "rescale", false));
                }

                return rotation;
            }

            private float parseAngle(JsonObject object) {
                float angle = JSONUtils.getFloat(object, "angle");
                if (angle != 0.0F && MathHelper.abs(angle) != 22.5F && MathHelper.abs(angle) != 45.0F) {
                    throw new JsonParseException("Invalid rotation " + angle + " found, only -45/-22.5/0/22.5/45 allowed");
                }

                return angle;
            }

            private Direction.Axis parseAxis(JsonObject object) {
                String axisName = JSONUtils.getString(object, "axis");
                Direction.Axis axis = Direction.Axis.byName(axisName.toLowerCase(Locale.ROOT));
                if (axis == null) {
                    throw new JsonParseException("Invalid rotation axis: " + axisName);
                }

                return axis;
            }

            private Map<Direction, BlockPartFace> parseFacesCheck(JsonObject object) {
                Map<Direction, BlockPartFace> faces = this.parseFaces(object);
                if (faces.isEmpty()) {
                    throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
                }

                return faces;
            }

            private Map<Direction, BlockPartFace> parseFaces(JsonObject object) {
                Map<Direction, BlockPartFace> faces = Maps.newEnumMap(Direction.class);
                JsonObject facesObject = JSONUtils.getJsonObject(object, "faces");
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
                if (vector.getX() >= -16.0F && vector.getY() >= -16.0F && vector.getZ() >= -16.0F && vector.getX() <= 32.0F && vector.getY() <= 32.0F && vector.getZ() <= 32.0F) {
                    return vector;
                }

                throw new JsonParseException("'" + name + "' specifier exceeds the allowed boundaries: " + vector);
            }

            private Vector3f deserializeVec3f(JsonObject object, String name) {
                JsonArray vectorJson = JSONUtils.getJsonArray(object, name);
                if (vectorJson.size() != 3) {
                    throw new JsonParseException("Expected 3 " + name + " values, found: " + vectorJson.size());
                }

                float[] vector = new float[3];
                for (int i = 0; i < 3; ++i) {
                    vector[i] = JSONUtils.getFloat(vectorJson.get(i), name + "[" + i + "]");
                }

                return new Vector3f(vector[0], vector[1], vector[2]);
            }
        }
    }
}
