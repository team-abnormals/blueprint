package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.events.FallingBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    public FallingBlockEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyVariable(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift = At.Shift.BEFORE))
    private static FallingBlockEntity fall(FallingBlockEntity fallingBlockEntity, Level level, BlockPos pos, BlockState state) {
        return FallingBlockEvent.onBlockFall(level, pos, state, fallingBlockEntity);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void tick(CallbackInfo info) {
        if (FallingBlockEvent.onFallingBlockTick((FallingBlockEntity) (Object) this)) {
            info.cancel();
        }
    }
}
