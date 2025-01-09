package com.teamabnormals.blueprint.core.other;

import com.teamabnormals.blueprint.common.network.particle.SpawnParticlesPayload;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import com.teamabnormals.blueprint.core.util.DataUtil.CustomNoteBlockInstrument;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.NoteBlockEvent;

import java.util.List;

/**
 * @author abigailfails
 */
@EventBusSubscriber(modid = Blueprint.MOD_ID)
public final class BlueprintEvents {
	public static final String NOTE_KEY = "minecraft:note";
	public static List<CustomNoteBlockInstrument> SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS = null;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onNoteBlockPlay(NoteBlockEvent.Play event) {
		if (SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS != null) {
			if (event.getLevel() instanceof ServerLevel level) {
				BlockPos pos = event.getPos();
				DispenserBlockEntity dispenserblockentity = level.getBlockEntity(pos, BlockEntityType.DISPENSER).orElse(null);
				if (dispenserblockentity == null) return;
				BlockSource source = new BlockSource(level, pos, level.getBlockState(pos.relative(Direction.DOWN)), dispenserblockentity);
				BlockSource headSource = new BlockSource(level, pos, level.getBlockState(pos.relative(Direction.UP)), dispenserblockentity);
				for (CustomNoteBlockInstrument instrument : SORTED_CUSTOM_NOTE_BLOCK_INSTRUMENTS) {
					boolean isMobHead = instrument.isMobHead();
					if (instrument.test(isMobHead ? headSource : source)) {
						SoundEvent sound = instrument.getSound();
						int note = event.getVanillaNoteId();
						level.playSound(null, pos, sound, SoundSource.RECORDS, 3.0F, isMobHead ? 1.0F : NoteBlock.getPitchFromNote(note));
						if (!isMobHead) {
							NetworkUtil.spawnParticle(level, ParticleTypes.NOTE, List.of(new SpawnParticlesPayload.ParticleInstance(pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, (double) note / 24.0D, 0.0D, 0.0D)));
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
		if (event.getState().is(BlueprintBlockTags.LEAF_PILES) && event.getEntity().getMainHandItem().is(Tags.Items.TOOLS_SHEAR))
			event.setNewSpeed(15.0F);
	}
}
