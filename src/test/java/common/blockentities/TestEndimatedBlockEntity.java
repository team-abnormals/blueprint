package common.blockentities;

import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimation;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimationManager;
import core.registry.TestBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Constants;

public final class TestEndimatedBlockEntity extends BlockEntity implements Endimatable {
	private final EndimatedState endimatedState = new EndimatedState(this);

	public TestEndimatedBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
		super(TestBlockEntities.TEST_ENDIMATED.get(), p_155229_, p_155230_);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		if (this.level != null) {
			this.load(pkt.getTag());
		}
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);
		tag.putInt("AnimationTick", this.getAnimationTick());
		PlayableEndimation playableEndimation = this.getPlayingEndimation();
		if (playableEndimation != PlayableEndimation.BLANK) {
			ResourceLocation endimation = PlayableEndimationManager.INSTANCE.getKey(this.getPlayingEndimation());
			tag.putString("Endimation", endimation != null ? endimation.toString() : PlayableEndimation.BLANK.location().toString());
		}
		return tag;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("Endimation", Tag.TAG_STRING)) {
			PlayableEndimation endimation = PlayableEndimationManager.INSTANCE.getEndimation(new ResourceLocation(tag.getString("Endimation")));
			if (endimation == PlayableEndimation.BLANK || endimation == null) {
				this.resetEndimation();
			} else {
				this.setPlayingEndimation(endimation);
				this.setAnimationTick(tag.getInt("AnimationTick"));
			}
		}
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return new ClientboundBlockEntityDataPacket(this.worldPosition, 100, this.getUpdateTag());
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.save(new CompoundTag());
	}

	@Override
	public EndimatedState getEndimatedState() {
		return this.endimatedState;
	}

	@Override
	public Position getPos() {
		return Vec3.atCenterOf(this.worldPosition);
	}

	@Override
	public boolean isActive() {
		return !this.isRemoved();
	}
}
