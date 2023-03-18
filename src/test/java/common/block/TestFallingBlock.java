package common.block;

import com.teamabnormals.blueprint.common.block.BlueprintFallingBlock;
import com.teamabnormals.blueprint.common.entity.BlueprintFallingBlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class TestFallingBlock extends BlueprintFallingBlock {

	public TestFallingBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void fallingEntityTick(Level level, BlueprintFallingBlockEntity fallingEntity) {
		if (!level.isClientSide) {
			AABB aabb = fallingEntity.getBoundingBox().expandTowards(fallingEntity.getDeltaMovement()).inflate(1.0D);
			Vec3 vec3 = fallingEntity.position();
			Vec3 vec31 = vec3.add(fallingEntity.getDeltaMovement());

			for (Entity entity : level.getEntities(fallingEntity, aabb, (entity) -> {
				return entity.getType() == EntityType.PLAYER && ((Player) entity).getItemBySlot(EquipmentSlot.HEAD).isEmpty();
			})) {
				AABB aabb1 = entity.getBoundingBox().inflate(0.3D);
				Optional<Vec3> optional = aabb1.clip(vec3, vec31);
				if (optional.isPresent()) {
					entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(this.asItem()));
					fallingEntity.discard();
					break;
				}
			}
		}
	}
}