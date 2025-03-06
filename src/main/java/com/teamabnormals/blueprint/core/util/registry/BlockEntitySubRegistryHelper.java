package com.teamabnormals.blueprint.core.util.registry;

import com.google.common.collect.Sets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

/**
 * A basic {@link AbstractSubRegistryHelper} for block entities.
 * <p>This contains a few registering methods for block entities.</p>
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 */
public class BlockEntitySubRegistryHelper extends AbstractSubRegistryHelper<BlockEntityType<?>, DeferredRegister<BlockEntityType<?>>> {

	public BlockEntitySubRegistryHelper(RegistryHelper parent, DeferredRegister<BlockEntityType<?>> deferredRegister) {
		super(parent, deferredRegister);
	}

	public BlockEntitySubRegistryHelper(RegistryHelper parent) {
		super(parent, DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, parent.getModId()));
	}

	/**
	 * Collects all registered {@link Block}s that are an instance of a {@link Block} class.
	 *
	 * @param blockClass The instance of class to filter
	 * @return A filtered array of registered {@link Block}s that are an instance of a {@link Block} class
	 */
	public static Block[] collectBlocks(Class<?> blockClass) {
		return BuiltInRegistries.BLOCK.stream().filter(blockClass::isInstance).toArray(Block[]::new);
	}

	/**
	 * Creates and registers a {@link BlockEntityType}.
	 *
	 * @param name        The name for the {@link BlockEntity}.
	 * @param blockEntity The {@link BlockEntity}.
	 * @param validBlocks The valid blocks for this {@link BlockEntityType}.
	 * @return A {@link DeferredHolder} containing the customized {@link BlockEntityType}.
	 */
	public <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> createBlockEntity(String name, BlockEntitySupplier<? extends T> blockEntity, Supplier<Set<Block>> validBlocks) {
		return this.deferredRegister.register(name, () -> new BlockEntityType<>(blockEntity, validBlocks.get(), null));
	}

	/**
	 * Creates and registers a {@link BlockEntityType} with valid blocks that are an instance of a {@link Block} class.
	 * <p>Useful for dynamic valid blocks on block entities.</p>
	 *
	 * @param name        The name for the {@link BlockEntity}.
	 * @param blockEntity The {@link BlockEntity}.
	 * @param blockClass  The block class to filter registered blocks that are an instance of it.
	 * @return A {@link DeferredHolder} containing the customized {@link BlockEntityType}.
	 */
	public <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> createBlockEntity(String name, BlockEntitySupplier<? extends T> blockEntity, Class<? extends Block> blockClass) {
		return this.deferredRegister.register(name, () -> new BlockEntityType<>(blockEntity, Sets.newHashSet(collectBlocks(blockClass)), null));
	}

}
