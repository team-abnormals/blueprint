package com.teamabnormals.blueprint.core.util.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

/**
 * A basic {@link AbstractSubRegistryHelper} for entities.
 * <p>This contains some useful registering methods for entities.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 */
public class EntitySubRegistryHelper extends AbstractSubRegistryHelper<EntityType<?>> {

	public EntitySubRegistryHelper(RegistryHelper parent, DeferredRegister<EntityType<?>> deferredRegister) {
		super(parent, deferredRegister);
	}

	public EntitySubRegistryHelper(RegistryHelper parent) {
		super(parent, DeferredRegister.create(Registries.ENTITY_TYPE, parent.getModId()));
	}

	public <E extends Entity> DeferredHolder<EntityType<?>, EntityType<E>> createEntity(String name, EntityType.EntityFactory<E> factory, MobCategory entityClassification, Consumer<EntityType.Builder<E>> builderConsumer) {
		return this.deferredRegister.register(name, () -> {
			var builder = EntityType.Builder.of(factory, entityClassification);
			builderConsumer.accept(builder);
			return builder.build(this.getParent().modId + ":" + name);
		});
	}

	/**
	 * Creates and registers an {@link EntityType} with the type of {@link Entity}.
	 *
	 * @param name                 The entity's name.
	 * @param factory              The entity's factory.
	 * @param entityClassification The entity's classification.
	 * @param width                The width of the entity's bounding box.
	 * @param height               The height of the entity's bounding box.
	 * @return A {@link DeferredHolder} containing the created {@link EntityType}.
	 */
	public <E extends Entity> DeferredHolder<EntityType<?>, EntityType<E>> createEntity(String name, EntityType.EntityFactory<E> factory, MobCategory entityClassification, float width, float height) {
		return this.deferredRegister.register(name, () -> createEntity(factory, entityClassification, name, width, height));
	}

	/**
	 * Creates an {@link EntityType} with the type of {@link Entity}.
	 *
	 * @param name                 The entity's name.
	 * @param factory              The entity's factory.
	 * @param entityClassification The entity's classification.
	 * @param width                The width of the entity's bounding box.
	 * @param height               The height of the entity's bounding box.
	 * @return The created {@link EntityType}.
	 */
	public <E extends Entity> EntityType<E> createEntity(EntityType.EntityFactory<E> factory, MobCategory entityClassification, String name, float width, float height) {
		ResourceLocation location = this.parent.prefix(name);
		return EntityType.Builder.of(factory, entityClassification)
				.sized(width, height)
				.setTrackingRange(64)
				.setShouldReceiveVelocityUpdates(true)
				.setUpdateInterval(3)
				.build(location.toString());
	}

}
