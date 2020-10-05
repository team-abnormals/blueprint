package com.teamabnormals.abnormals_core.common.world.storage.tracking;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

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
		public CompoundNBT write(Boolean bool) {
			CompoundNBT compound = new CompoundNBT();
			compound.putBoolean("Boolean", bool);
			return compound;
		}

		@Override
		public Boolean read(CompoundNBT nbt) {
			return nbt.getBoolean("Boolean");
		}

	};

	public static final IDataProcessor<Byte> BYTE = new IDataProcessor<Byte>() {

		@Override
		public CompoundNBT write(Byte abyte) {
			CompoundNBT compound = new CompoundNBT();
			compound.putByte("Byte", abyte);
			return compound;
		}

		@Override
		public Byte read(CompoundNBT nbt) {
			return nbt.getByte("Byte");
		}

	};

	public static final IDataProcessor<Short> SHORT = new IDataProcessor<Short>() {

		@Override
		public CompoundNBT write(Short ashort) {
			CompoundNBT compound = new CompoundNBT();
			compound.putShort("Short", ashort);
			return compound;
		}

		@Override
		public Short read(CompoundNBT nbt) {
			return nbt.getShort("Short");
		}

	};

	public static final IDataProcessor<Integer> INT = new IDataProcessor<Integer>() {

		@Override
		public CompoundNBT write(Integer integer) {
			CompoundNBT compound = new CompoundNBT();
			compound.putInt("Integer", integer);
			return compound;
		}

		@Override
		public Integer read(CompoundNBT nbt) {
			return nbt.getInt("Integer");
		}

	};

	public static final IDataProcessor<Long> LONG = new IDataProcessor<Long>() {

		@Override
		public CompoundNBT write(Long along) {
			CompoundNBT compound = new CompoundNBT();
			compound.putLong("Long", along);
			return compound;
		}

		@Override
		public Long read(CompoundNBT nbt) {
			return nbt.getLong("Long");
		}

	};

	public static final IDataProcessor<Float> FLOAT = new IDataProcessor<Float>() {

		@Override
		public CompoundNBT write(Float afloat) {
			CompoundNBT compound = new CompoundNBT();
			compound.putFloat("Float", afloat);
			return compound;
		}

		@Override
		public Float read(CompoundNBT nbt) {
			return nbt.getFloat("Float");
		}

	};

	public static final IDataProcessor<Double> DOUBLE = new IDataProcessor<Double>() {

		@Override
		public CompoundNBT write(Double aDouble) {
			CompoundNBT compound = new CompoundNBT();
			compound.putDouble("Double", aDouble);
			return compound;
		}

		@Override
		public Double read(CompoundNBT nbt) {
			return nbt.getDouble("Double");
		}

	};

	public static final IDataProcessor<String> STRING = new IDataProcessor<String>() {

		@Override
		public CompoundNBT write(String aString) {
			CompoundNBT compound = new CompoundNBT();
			compound.putString("String", aString);
			return compound;
		}

		@Override
		public String read(CompoundNBT nbt) {
			return nbt.getString("String");
		}

	};

	public static final IDataProcessor<BlockPos> POS = new IDataProcessor<BlockPos>() {

		@Override
		public CompoundNBT write(BlockPos pos) {
			CompoundNBT compound = new CompoundNBT();
			compound.putLong("Pos", pos.toLong());
			return compound;
		}

		@Override
		public BlockPos read(CompoundNBT compound) {
			return BlockPos.fromLong(compound.getLong("Pos"));
		}

	};

	public static final IDataProcessor<UUID> UUID = new IDataProcessor<UUID>() {

		@Override
		public CompoundNBT write(UUID uuid) {
			CompoundNBT compound = new CompoundNBT();
			compound.putUniqueId("UUID", uuid);
			return compound;
		}

		@Override
		public UUID read(CompoundNBT compound) {
			return compound.getUniqueId("UUID");
		}

	};

	public static final IDataProcessor<CompoundNBT> COMPOUND = new IDataProcessor<CompoundNBT>() {

		@Override
		public CompoundNBT write(CompoundNBT compound) {
			return compound;
		}

		@Override
		public CompoundNBT read(CompoundNBT compound) {
			return compound;
		}

	};

	public static final IDataProcessor<ItemStack> STACK = new IDataProcessor<ItemStack>() {

		@Override
		public CompoundNBT write(ItemStack stack) {
			return stack.write(new CompoundNBT());
		}

		@Override
		public ItemStack read(CompoundNBT compound) {
			return ItemStack.read(compound);
		}

	};

	public static final IDataProcessor<ResourceLocation> RESOURCE_LOCATION = new IDataProcessor<ResourceLocation>() {

		@Override
		public CompoundNBT write(ResourceLocation resourceLocation) {
			CompoundNBT compound = new CompoundNBT();
			compound.putString("ResourceLocation", resourceLocation.toString());
			return compound;
		}

		@Override
		public ResourceLocation read(CompoundNBT compound) {
			return new ResourceLocation(compound.getString("ResourceLocation"));
		}

	};
}
