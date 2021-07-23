package com.minecraftabnormals.abnormals_core.core.util.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiFunction;

/**
 * A basic {@link AbstractSubRegistryHelper} for entities. This contains some useful registering methods for entities.
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 */
public class EntitySubRegistryHelper extends AbstractSubRegistryHelper<EntityType<?>> {

	public EntitySubRegistryHelper(RegistryHelper parent, DeferredRegister<EntityType<?>> deferredRegister) {
		super(parent, deferredRegister);
	}

	public EntitySubRegistryHelper(RegistryHelper parent) {
		super(parent, DeferredRegister.create(ForgeRegistries.ENTITIES, parent.getModId()));
	}

	/**
	 * Creates and registers an {@link EntityType} with the type of a {@link LivingEntity}.
	 *
	 * @param name                 - The entity's name.
	 * @param factory              - The entity's factory.
	 * @param entityClassification - The entity's classification.
	 * @param width                - The width of the entity's bounding box.
	 * @param height               - The height of the entity's bounding box.
	 * @return A {@link RegistryObject} containing the created {@link EntityType}
	 */
	public <E extends LivingEntity> RegistryObject<EntityType<E>> createLivingEntity(String name, EntityType.IFactory<E> factory, EntityClassification entityClassification, float width, float height) {
		return this.deferredRegister.register(name, () -> createLivingEntity(factory, entityClassification, name, width, height));
	}

	/**
	 * Creates and registers an {@link EntityType} with the type of a {@link Entity}.
	 *
	 * @param name                 - The entity's name.
	 * @param factory              - The entity's factory.
	 * @param clientFactory        - The entity's client factory.
	 * @param entityClassification - The entity's classification.
	 * @param width                - The width of the entity's bounding box.
	 * @param height               - The height of the entity's bounding box.
	 * @return A {@link RegistryObject} containing the created {@link EntityType}
	 */
	public <E extends Entity> RegistryObject<EntityType<E>> createEntity(String name, EntityType.IFactory<E> factory, BiFunction<FMLPlayMessages.SpawnEntity, World, E> clientFactory, EntityClassification entityClassification, float width, float height) {
		return this.deferredRegister.register(name, () -> createEntity(factory, clientFactory, entityClassification, name, width, height));
	}

	/**
	 * Creates an {@link EntityType} with the type of a {@link LivingEntity}.
	 *
	 * @param name                 - The entity's name.
	 * @param factory              - The entity's factory.
	 * @param entityClassification - The entity's classification.
	 * @param width                - The width of the entity's bounding box.
	 * @param height               - The height of the entity's bounding box.
	 * @return The created {@link EntityType}.
	 */
	public <E extends LivingEntity> EntityType<E> createLivingEntity(EntityType.IFactory<E> factory, EntityClassification entityClassification, String name, float width, float height) {
		ResourceLocation location = this.parent.prefix(name);
		EntityType<E> entity = EntityType.Builder.of(factory, entityClassification)
				.sized(width, height)
				.setTrackingRange(64)
				.setShouldReceiveVelocityUpdates(true)
				.setUpdateInterval(3)
				.build(location.toString());
		return entity;
	}

	/**
	 * Creates an {@link EntityType} with the type of a {@link Entity}.
	 *
	 * @param name                 - The entity's name.
	 * @param factory              - The entity's factory.
	 * @param clientFactory        - The entity's client factory.
	 * @param entityClassification - The entity's classification.
	 * @param width                - The width of the entity's bounding box.
	 * @param height               - The height of the entity's bounding box.
	 * @return The created {@link EntityType}.
	 */
	public <E extends Entity> EntityType<E> createEntity(EntityType.IFactory<E> factory, BiFunction<FMLPlayMessages.SpawnEntity, World, E> clientFactory, EntityClassification entityClassification, String name, float width, float height) {
		ResourceLocation location = this.parent.prefix(name);
		EntityType<E> entity = EntityType.Builder.of(factory, entityClassification)
				.sized(width, height)
				.setTrackingRange(64)
				.setShouldReceiveVelocityUpdates(true)
				.setUpdateInterval(3)
				.setCustomClientFactory(clientFactory)
				.build(location.toString());
		return entity;
	}

}
