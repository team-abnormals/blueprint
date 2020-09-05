package com.teamabnormals.abnormals_core.client;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A simple helper class for custom name entity skins.
 *
 * @auhor - SmellyModder (Luke Tonon)
 * @param <E> - The type of entity.
 */
public class EntitySkinHelper<E extends Entity> {
	private final Map<String, ResourceLocation> skins = Maps.newHashMap();
	private final String modId;
	private final String formattablePath;

	private EntitySkinHelper(String modId, String path, @Nullable String prefix) {
		this.modId = modId;
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		this.formattablePath = prefix != null ? path + prefix + "_%s.png" : path + "%s.png";
	}

	/**
	 * Creates a basic {@link EntitySkinHelper}.
	 * @param modId - The mod id.
	 * @param path - The path of the folder to lookup the textures in.
	 * @param prefix - The prefix for the textures to lookup (e.g. prefix = "booflo"; "booflo_snake").
	 * @param consumer - A consumer used to call the methods to put skins for names.
	 * @param <E> - The type of entity
	 * @return A {@link EntitySkinHelper} with a specified mod id, path, and prefix.
	 */
	public static <E extends Entity> EntitySkinHelper<E> create(String modId, String path, String prefix, Consumer<EntitySkinHelper<E>> consumer) {
		EntitySkinHelper<E> skinHelper = new EntitySkinHelper<>(modId, path, prefix);
		consumer.accept(skinHelper);
		return skinHelper;
	}

	/**
	 * Creates a {@link EntitySkinHelper} with no prefix.
	 * @param modId - The mod id.
	 * @param path - The path of the folder to lookup the textures in.
	 * @param consumer - A consumer used to call the methods to put skins for names.
	 * @param <E> - The type of entity
	 * @return A {@link EntitySkinHelper} with a specified mod id and path.
	 */
	public static <E extends Entity> EntitySkinHelper<E> createWithoutPrefix(String modId, String path, Consumer<EntitySkinHelper<E>> consumer) {
		EntitySkinHelper<E> skinHelper = new EntitySkinHelper<>(modId, path, null);
		consumer.accept(skinHelper);
		return skinHelper;
	}

	/**
	 * Puts multiple skin name keys onto the map for a skin texture.
	 * For putting one skin name key use {@link #putSkin(String, String)}.
	 * @param skinTexture - The name of the skin texture.
	 * @param skinNames - The name keys to be mapped for this skin texture.
	 */
	public void putSkins(String skinTexture, String... skinNames) {
		ResourceLocation skinTextureLocation = this.createTextureLocation(skinTexture);
		for (String skin : skinNames) {
			this.skins.put(skin, skinTextureLocation);
		}
	}

	/**
	 * Puts a skin name key onto the map for a skin texture.
	 * For multiple skin name keys use {@link #putSkins(String, String...)}.
	 * @param skinTexture - The name of the skin texture.
	 * @param skinName - The name key to be mapped for this skin texture.
	 */
	public void putSkin(String skinTexture, String skinName) {
		this.skins.put(skinName, this.createTextureLocation(skinTexture));
	}

	private ResourceLocation createTextureLocation(String skin) {
		return new ResourceLocation(this.modId, String.format(this.formattablePath, skin));
	}

	/**
	 * Gets the skin texture {@link ResourceLocation} for an entity.
	 * @param entity - The entity to get the skin for.
	 * @return The skin texture {@link ResourceLocation} for an entity or null if no skin could be found for its name.
	 */
	@Nullable
	public ResourceLocation getSkinForEntity(E entity) {
		if (entity.hasCustomName()) {
			return this.skins.get(entity.getCustomName().getString().toLowerCase().trim());
		}
		return null;
	}

	/**
	 * Gets the skin texture {@link ResourceLocation} for an entity or defaults to {@param otherSkinTexture} if skin couldn't be looked up.
	 * @param entity - The entity to get the skin for.
	 * @return The skin texture {@link ResourceLocation} for an entity or {@param otherSkinTexture} if null.
	 */
	@Nonnull
	public ResourceLocation getSkinForEntityOrElse(E entity, ResourceLocation otherSkinTexture) {
		ResourceLocation skin = this.getSkinForEntity(entity);
		return skin != null ? skin : otherSkinTexture;
	}
}
