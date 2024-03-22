package com.teamabnormals.blueprint.core.other;

import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import com.teamabnormals.blueprint.core.util.DataUtil.CustomNoteBlockInstrument;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * @author abigailfails
 */
@Mod.EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class BlueprintEvents {
	public static final String NOTE_KEY = "minecraft:note";
	public static List<CustomNoteBlockInstrument> SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS = null;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onNoteBlockPlay(NoteBlockEvent.Play event) {
		if (SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS != null) {
			if (event.getLevel() instanceof ServerLevel level) {
				BlockPos pos = event.getPos();
				BlockSource source = new BlockSourceImpl(level, pos.relative(Direction.DOWN));
				BlockSource headSource = new BlockSourceImpl(level, pos.relative(Direction.UP));
				for (CustomNoteBlockInstrument instrument : SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS) {
					boolean isMobHead = instrument.isMobHead();
					if (instrument.test(isMobHead ? headSource : source)) {
						SoundEvent sound = instrument.getSound();
						int note = event.getVanillaNoteId();
						level.playSound(null, pos, sound, SoundSource.RECORDS, 3.0F, isMobHead ? 1.0F : NoteBlock.getPitchFromNote(note));
						if (!isMobHead) {
							NetworkUtil.spawnParticle(NOTE_KEY, level.dimension(), pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, (double) note / 24.0D, 0.0D, 0.0D);
						}
						event.setCanceled(true);
						break;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (event.getState().is(BlueprintBlockTags.LEAF_PILES) && event.getEntity().getMainHandItem().is(Tags.Items.SHEARS))
			event.setNewSpeed(15.0F);
	}
}
