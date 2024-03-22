package com.teamabnormals.blueprint.core.mixin;

import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin {

	@Inject(at = @At("HEAD"), method = "playNote", cancellable = true)
	private void playNote(Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
		if (level.getBlockState(pos.above()).is(BlueprintBlockTags.NOTE_BLOCK_TOP_INSTRUMENTS)) {
			level.blockEvent(pos, (NoteBlock) (Object) this, 0, 0);
			level.gameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
			ci.cancel();
		}
	}
}
