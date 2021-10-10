package common.blocks;

import com.minecraftabnormals.abnormals_core.common.capability.chunkloading.ChunkLoader;
import com.minecraftabnormals.abnormals_core.common.capability.chunkloading.ChunkLoaderCapability;
import com.minecraftabnormals.abnormals_core.core.util.NetworkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class ChunkLoadTestBlock extends Block {

	public ChunkLoadTestBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!level.isClientSide) {
			level.getCapability(ChunkLoaderCapability.CHUNK_LOAD_CAP).ifPresent(loader -> loader.addPos(pos));
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!level.isClientSide) {
			level.getCapability(ChunkLoaderCapability.CHUNK_LOAD_CAP).ifPresent(loader -> {
				loader.removePos(pos);
				((ChunkLoader) loader).scheduleChunkProcess(level.getChunk(pos), (chunk) -> {
					Random rand = new Random();
					for (int i = 0; i < 4; i++) {
						int x = pos.getX() + rand.nextInt(3);
						int z = pos.getZ() + rand.nextInt(3);
						Zombie zombie = EntityType.ZOMBIE.create(level);
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