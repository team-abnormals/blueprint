package com.teamabnormals.abnormals_core.core.util.registry;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * A basic {@link AbstractSubRegistryHelper} for tile entities. This contains a few registering methods for tile entities.
 *
 * @author SmellyModder (Luke Tonon)
 * @see AbstractSubRegistryHelper
 */
public class TileEntitySubRegistryHelper extends AbstractSubRegistryHelper<TileEntityType<?>> {

	public TileEntitySubRegistryHelper(RegistryHelper parent, DeferredRegister<TileEntityType<?>> deferredRegister) {
		super(parent, deferredRegister);
	}

	public TileEntitySubRegistryHelper(RegistryHelper parent) {
		super(parent, DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, parent.getModId()));
	}

	/**
	 * Creates and registers a {@link TileEntityType}.
	 *
	 * @param name        - The name for the {@link TileEntity}.
	 * @param tileEntity  - The {@link TileEntity}.
	 * @param validBlocks - The valid blocks for this {@link TileEntityType}.
	 * @return A {@link RegistryObject} containing the customized {@link TileEntityType}.
	 */
	public <T extends TileEntity> RegistryObject<TileEntityType<T>> createTileEntity(String name, Supplier<? extends T> tileEntity, Supplier<Block[]> validBlocks) {
		return this.deferredRegister.register(name, () -> new TileEntityType<>(tileEntity, Sets.newHashSet(validBlocks.get()), null));
	}

	/**
	 * Creates and registers a {@link TileEntityType} with valid blocks that are an instance of a {@link Block} class.
	 * Useful for dynamic valid blocks on tile entities.
	 *
	 * @param name       - The name for the {@link TileEntity}.
	 * @param tileEntity - The {@link TileEntity}.
	 * @param blockClass - The block class to filter registered blocks that are an instance of it.
	 * @return A {@link RegistryObject} containing the customized {@link TileEntityType}.
	 */
	public <T extends TileEntity> RegistryObject<TileEntityType<T>> createTileEntity(String name, Supplier<? extends T> tileEntity, Class<? extends Block> blockClass) {
		return this.deferredRegister.register(name, () -> new TileEntityType<>(tileEntity, Sets.newHashSet(collectBlocks(blockClass)), null));
	}

	/**
	 * Collects all registered {@link Block}s that are an instance of a {@link Block} class.
	 *
	 * @param blockClass - The instance of class to filter
	 * @return A filtered array of registered {@link Block}s that are an instance of a {@link Block} class
	 */
	public static Block[] collectBlocks(Class<? extends Block> blockClass) {
		return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> blockClass.isInstance(block)).toArray(Block[]::new);
	}

}
