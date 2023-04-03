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
}