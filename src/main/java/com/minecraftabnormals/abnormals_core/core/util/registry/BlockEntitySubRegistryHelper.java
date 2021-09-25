package com.minecraftabnormals.abnormals_core.core.util.registry;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * A basic {@link AbstractSubRegistryHelper} for tile entities. This contains a few registering methods for tile entities.
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 */
public class BlockEntitySubRegistryHelper extends AbstractSubRegistryHelper<BlockEntityType<?>> {

	public BlockEntitySubRegistryHelper(RegistryHelper parent, DeferredRegister<BlockEntityType<?>> deferredRegister) {
		super(parent, deferredRegister);
	}

	public BlockEntitySubRegistryHelper(RegistryHelper parent) {
		super(parent, DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, parent.getModId()));
	}

	/**
	 * Creates and registers a {@link BlockEntityType}.
	 *
	 * @param name        - The name for the {@link BlockEntity}.
	 * @param tileEntity  - The {@link BlockEntity}.
	 * @param validBlocks - The valid blocks for this {@link BlockEntityType}.
	 * @return A {@link RegistryObject} containing the customized {@link BlockEntityType}.
	 */
	public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> createBlockEntity(String name, BlockEntitySupplier<? extends T> tileEntity, Supplier<Block[]> validBlocks) {
		return this.deferredRegister.register(name, () -> new BlockEntityType<>(tileEntity, Sets.newHashSet(validBlocks.get()), null));
	}

	/**
	 * Creates and registers a {@link BlockEntityType} with valid blocks that are an instance of a {@link Block} class.
	 * Useful for dynamic valid blocks on tile entities.
	 *
	 * @param name       - The name for the {@link BlockEntity}.
	 * @param tileEntity - The {@link BlockEntity}.
	 * @param blockClass - The block class to filter registered blocks that are an instance of it.
	 * @return A {@link RegistryObject} containing the customized {@link BlockEntityType}.
	 */
	public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> createBlockEntity(String name, BlockEntitySupplier<? extends T> tileEntity, Class<? extends Block> blockClass) {
		return this.deferredRegister.register(name, () -> new BlockEntityType<>(tileEntity, Sets.newHashSet(collectBlocks(blockClass)), null));
	}

	/**
	 * Collects all registered {@link Block}s that are an instance of a {@link Block} class.
	 *
	 * @param blockClass - The instance of class to filter
	 * @return A filtered array of registered {@link Block}s that are an instance of a {@link Block} class
	 */
	public static Block[] collectBlocks(Class<?> blockClass) {
		return ForgeRegistries.BLOCKS.getValues().stream().filter(blockClass::isInstance).toArray(Block[]::new);
	}

}
