package common.blocks;

import com.minecraftabnormals.abnormals_core.common.capability.chunkloading.ChunkLoader;
import com.minecraftabnormals.abnormals_core.common.capability.chunkloading.ChunkLoaderCapability;
import com.minecraftabnormals.abnormals_core.core.annotations.Test;
import com.minecraftabnormals.abnormals_core.core.util.NetworkUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@Test
public class ChunkLoadTestBlock extends Block {

	public ChunkLoadTestBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!world.isClientSide) {
			world.getCapability(ChunkLoaderCapability.CHUNK_LOAD_CAP).ifPresent(loader -> {
				loader.addPos(pos);
			});
		}
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!world.isClientSide) {
			world.getCapability(ChunkLoaderCapability.CHUNK_LOAD_CAP).ifPresent(loader -> {
				loader.removePos(pos);
				((ChunkLoader) loader).scheduleChunkProcess(world.getChunk(pos), (chunk) -> {
					Random rand = new Random();
					for (int i = 0; i < 4; i++) {
						int x = pos.getX() + rand.nextInt(3);
						int z = pos.getZ() + rand.nextInt(3);
						ZombieEntity zombie = EntityType.ZOMBIE.create(world);
						zombie.setPos(x, pos.getY(), z);
						chunk.getWorldForge().addFreshEntity(zombie);

						for (int i2 = 0; i2 < 3; i2++) {
							NetworkUtil.spawnParticle(ParticleTypes.CLOUD.getRegistryName().toString(), x, pos.getY(), z, 0.0F, 0.25F, 0.0F);
						}
					}
				}, 20);
			});
		}
	}

}