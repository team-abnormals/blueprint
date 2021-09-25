package com.minecraftabnormals.abnormals_core.common.world.storage.tracking;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import java.util.UUID;

/**
 * This class contains the built-in {@link IDataProcessor}s.
 * Use these fields for some primitive types or basic types. Feel free to make PRs to add more of these!
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class DataProcessors {
	public static final IDataProcessor<Boolean> BOOLEAN = new IDataProcessor<Boolean>() {

		@Override
		public CompoundTag write(Boolean bool) {
			CompoundTag compound = new CompoundTag();
			compound.putBoolean("Boolean", bool);
			return compound;
		}

		@Override
		public Boolean read(CompoundTag nbt) {
			return nbt.getBoolean("Boolean");
		}

	};

	public static final IDataProcessor<Byte> BYTE = new IDataProcessor<Byte>() {

		@Override
		public CompoundTag write(Byte abyte) {
			CompoundTag compound = new CompoundTag();
			compound.putByte("Byte", abyte);
			return compound;
		}

		@Override
		public Byte read(CompoundTag nbt) {
			return nbt.getByte("Byte");
		}

	};

	public static final IDataProcessor<Short> SHORT = new IDataProcessor<Short>() {

		@Override
		public CompoundTag write(Short ashort) {
			CompoundTag compound = new CompoundTag();
			compound.putShort("Short", ashort);
			return compound;
		}

		@Override
		public Short read(CompoundTag nbt) {
			return nbt.getShort("Short");
		}

	};

	public static final IDataProcessor<Integer> INT = new IDataProcessor<Integer>() {

		@Override
		public CompoundTag write(Integer integer) {
			CompoundTag compound = new CompoundTag();
			compound.putInt("Integer", integer);
			return compound;
		}

		@Override
		public Integer read(CompoundTag nbt) {
			return nbt.getInt("Integer");
		}

	};

	public static final IDataProcessor<Long> LONG = new IDataProcessor<Long>() {

		@Override
		public CompoundTag write(Long along) {
			CompoundTag compound = new CompoundTag();
			compound.putLong("Long", along);
			return compound;
		}

		@Override
		public Long read(CompoundTag nbt) {
			return nbt.getLong("Long");
		}

	};

	public static final IDataProcessor<Float> FLOAT = new IDataProcessor<Float>() {

		@Override
		public CompoundTag write(Float afloat) {
			CompoundTag compound = new CompoundTag();
			compound.putFloat("Float", afloat);
			return compound;
		}

		@Override
		public Float read(CompoundTag nbt) {
			return nbt.getFloat("Float");
		}

	};

	public static final IDataProcessor<Double> DOUBLE = new IDataProcessor<Double>() {

		@Override
		public CompoundTag write(Double aDouble) {
			CompoundTag compound = new CompoundTag();
			compound.putDouble("Double", aDouble);
			return compound;
		}

		@Override
		public Double read(CompoundTag nbt) {
			return nbt.getDouble("Double");
		}

	};

	public static final IDataProcessor<String> STRING = new IDataProcessor<String>() {

		@Override
		public CompoundTag write(String aString) {
			CompoundTag compound = new CompoundTag();
			compound.putString("String", aString);
			return compound;
		}

		@Override
		public String read(CompoundTag nbt) {
			return nbt.getString("String");
		}

	};

	public static final IDataProcessor<BlockPos> POS = new IDataProcessor<BlockPos>() {

		@Override
		public CompoundTag write(BlockPos pos) {
			CompoundTag compound = new CompoundTag();
			compound.putLong("Pos", pos.asLong());
			return compound;
		}

		@Override
		public BlockPos read(CompoundTag compound) {
			return BlockPos.of(compound.getLong("Pos"));
		}

	};

	public static final IDataProcessor<UUID> UUID = new IDataProcessor<UUID>() {

		@Override
		public CompoundTag write(UUID uuid) {
			CompoundTag compound = new CompoundTag();
			compound.putUUID("UUID", uuid);
			return compound;
		}

		@Override
		public UUID read(CompoundTag compound) {
			return compound.getUUID("UUID");
		}

	};

	public static final IDataProcessor<CompoundTag> COMPOUND = new IDataProcessor<CompoundTag>() {

		@Override
		public CompoundTag write(CompoundTag compound) {
			return compound;
		}

		@Override
		public CompoundTag read(CompoundTag compound) {
			return compound;
		}

	};

	public static final IDataProcessor<ItemStack> STACK = new IDataProcessor<ItemStack>() {

		@Override
		public CompoundTag write(ItemStack stack) {
			return stack.save(new CompoundTag());
		}

		@Override
		public ItemStack read(CompoundTag compound) {
			return ItemStack.of(compound);
		}

	};

	public static final IDataProcessor<ResourceLocation> RESOURCE_LOCATION = new IDataProcessor<ResourceLocation>() {

		@Override
		public CompoundTag write(ResourceLocation resourceLocation) {
			CompoundTag compound = new CompoundTag();
			compound.putString("ResourceLocation", resourceLocation.toString());
			return compound;
		}

		@Override
		public ResourceLocation read(CompoundTag compound) {
			return new ResourceLocation(compound.getString("ResourceLocation"));
		}

	};
}
