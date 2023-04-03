package core;

import com.teamabnormals.blueprint.client.screen.shaking.EmanatingShakeSource;
import com.teamabnormals.blueprint.client.screen.shaking.ScreenShakeHandler;
import com.teamabnormals.blueprint.common.entity.BlueprintFallingBlockEntity;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.events.EntityStepEvent;
import com.teamabnormals.blueprint.core.events.FallingBlockEvent.BlockFallEvent;
import com.teamabnormals.blueprint.core.events.FallingBlockEvent.FallingBlockTickEvent;
import com.teamabnormals.blueprint.core.util.TradeUtil;
import com.teamabnormals.blueprint.core.util.TradeUtil.BlueprintTrade;
import core.registry.TestItems;
import core.registry.TestTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = BlueprintTest.MOD_ID)
public final class TestEvents {

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		Entity entity = event.getTarget();
		if (entity instanceof Cow || entity instanceof Player) {
			if (!entity.level.isClientSide) {
				Player player = event.getEntity();
				if (player instanceof ServerPlayer) {
					TestTriggers.EMPTY_TEST.trigger((ServerPlayer) player);
				}
				TrackedDataManager.INSTANCE.setValue(entity, BlueprintTest.TEST_TRACKED_DATA, true);
			} else {
				ScreenShakeHandler.INSTANCE.addShakeSource(new EmanatingShakeSource(entity, 100, 0.1F, 0.1F, 0.02F, 0.2F, 0.2F, 0.04F, 0.98F, 0.98F, 0.99F));
			}
		}
	}

	@SubscribeEvent
	public static void onLivingTick(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.level.isClientSide && (entity instanceof Cow || entity instanceof Player) && TrackedDataManager.INSTANCE.getValue(entity, BlueprintTest.TEST_TRACKED_DATA)) {
			RandomSource rand = entity.getRandom();
			for (int i = 0; i < 2; ++i) {
				entity.level.addParticle(ParticleTypes.PORTAL, entity.getRandomX(0.5D), entity.getRandomY() - 0.25D, entity.getRandomZ(0.5D), (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
			}
		}
	}

	@SubscribeEvent
	public static void onTrades(VillagerTradesEvent event) {
		TradeUtil.addVillagerTrades(event, 1,
				new BlueprintTrade(TestItems.ITEM.get(), 5, 2, 6, 15),
				new BlueprintTrade(TestItems.ITEM.get(), 5, 2, 6, 55),
				new BlueprintTrade(TestItems.ITEM.get(), 5, 9, 6, 75)
		);
		TradeUtil.addVillagerTrades(event, VillagerProfession.CARTOGRAPHER, 2, new BlueprintTrade(TestItems.BOAT.getFirst().get(), 5, 10, 6, 100));
	}

	@SubscribeEvent
	public static void onWandererTrades(WandererTradesEvent event) {
		TradeUtil.addWandererTrades(event, new BlueprintTrade(Items.EMERALD_ORE, 5, 2, 6, 15));
		TradeUtil.addRareWandererTrades(event,
				new BlueprintTrade(TestItems.COW_SPAWN_EGG.get(), 5, 2, 6, 15),
				new BlueprintTrade(TestItems.COW_SPAWN_EGG.get(), 5, 2, 6, 55)
		);
	}

	@SubscribeEvent
	public static void onEntityStep(EntityStepEvent event) {
		if (event.getState().getBlock() == Blocks.MAGMA_BLOCK) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onBlockFall(BlockFallEvent event) {
		if (event.getState().getBlock() == Blocks.LIGHT_BLUE_CONCRETE_POWDER) {
			FallingBlockEntity fallingblockentity = new BlueprintFallingBlockEntity(event.getLevel(), event.getPos().getX() + 0.5D, event.getPos().getY(), event.getPos().getZ() + 0.5D, Blocks.DIAMOND_BLOCK.defaultBlockState());
			event.setEntity(fallingblockentity);
		}
	}

	@SubscribeEvent
	public static void onFallingBlockTick(FallingBlockTickEvent event) {
		FallingBlockEntity entity = event.getEntity();
		Level level = entity.getLevel();

		if (!level.isClientSide()) {
			if (entity.getBlockState().is(Blocks.GREEN_CONCRETE_POWDER)) {
				AABB aabb = entity.getBoundingBox().expandTowards(entity.getDeltaMovement()).inflate(1.0D);
				Vec3 vec3 = entity.position();
				Vec3 vec31 = vec3.add(entity.getDeltaMovement());

				for (Entity entity1 : level.getEntities(entity, aabb, (entity1) -> {
					return entity1.getType() == EntityType.PLAYER && ((Player) entity1).getItemBySlot(EquipmentSlot.HEAD).isEmpty();
				})) {
					AABB aabb1 = entity1.getBoundingBox().inflate(0.3D);
					Optional<Vec3> optional = aabb1.clip(vec3, vec31);
					if (optional.isPresent()) {
						entity1.setItemSlot(EquipmentSlot.HEAD, new ItemStack(entity.getBlockState().getBlock().asItem()));
						entity.discard();
						event.setCanceled(true);
						break;
					}
				}
			} else if (entity.getBlockState().is(Blocks.BLACK_CONCRETE_POWDER)) {
				Bat bat = EntityType.BAT.create(entity.getLevel());
				bat.moveTo(entity.getX(), entity.getY(), entity.getZ(), 0.0F, 0.0F);
				level.addFreshEntity(bat);
			}
		}
	}

	public static boolean onAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
		return state.getBlock() != Blocks.REDSTONE_ORE;
	}

	public static boolean onFluidAnimateTick(FluidState state, Level level, BlockPos pos, RandomSource randomSource) {
		return !state.is(FluidTags.LAVA);
	}

}
