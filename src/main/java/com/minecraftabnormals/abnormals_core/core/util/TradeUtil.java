package com.minecraftabnormals.abnormals_core.core.util;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.fml.ModList;

/**
 * A utility class containing some useful stuff for village trades.
 *
 * @author bageldotjpg
 */
public final class TradeUtil {
	public static final int NOVICE = 1;
	public static final int APPRENTICE = 2;
	public static final int JOURNEYMAN = 3;
	public static final int EXPERT = 4;
	public static final int MASTER = 5;

	/**
	 * Adds an array of {@link VillagerTrades.ItemListing}s for a specific level.
	 *
	 * @param event  An event to add the trades to.
	 * @param level  The level for the trades.
	 * @param trades An array of {@link VillagerTrades.ItemListing}s.
	 */
	public static void addVillagerTrades(VillagerTradesEvent event, int level, VillagerTrades.ItemListing... trades) {
		for (VillagerTrades.ItemListing trade : trades) event.getTrades().get(level).add(trade);
	}

	/**
	 * Adds an array of {@link VillagerTrades.ItemListing}s for a specific profession and level.
	 *
	 * @param event      An event to add the trades to.
	 * @param profession A {@link VillagerProfession} to target.
	 * @param level      The level for the trades.
	 * @param trades     An array of {@link VillagerTrades.ItemListing}s.
	 */
	public static void addVillagerTrades(VillagerTradesEvent event, VillagerProfession profession, int level, VillagerTrades.ItemListing... trades) {
		if (event.getType() == profession) addVillagerTrades(event, level, trades);
	}

	/**
	 * Adds an array of {@link VillagerTrades.ItemListing}s to a {@link WandererTradesEvent} instance.
	 *
	 * @param event  A {@link WandererTradesEvent} instance to use.
	 * @param trades An array of {@link VillagerTrades.ItemListing}s to add.
	 */
	public static void addWandererTrades(WandererTradesEvent event, VillagerTrades.ItemListing... trades) {
		for (VillagerTrades.ItemListing trade : trades) event.getGenericTrades().add(trade);
	}

	/**
	 * Adds an array of {@link VillagerTrades.ItemListing}s to the rare trades in a {@link WandererTradesEvent} instance.
	 *
	 * @param event  A {@link WandererTradesEvent} instance to use.
	 * @param trades An array of {@link VillagerTrades.ItemListing}s to add.
	 */
	public static void addRareWandererTrades(WandererTradesEvent event, VillagerTrades.ItemListing... trades) {
		for (VillagerTrades.ItemListing trade : trades) event.getRareTrades().add(trade);
	}

	/**
	 * Adds an array of {@link VillagerTrades.ItemListing}s for a specific level only if a given Mod ID is loaded.
	 *
	 * @param event  An event to add the trades to.
	 * @param modid  A Mod ID to check.
	 * @param level  The level for the trades.
	 * @param trades An array of {@link VillagerTrades.ItemListing}s.
	 */
	public static void addCompatVillagerTrades(VillagerTradesEvent event, String modid, int level, VillagerTrades.ItemListing... trades) {
		if (ModList.get().isLoaded(modid)) addVillagerTrades(event, level, trades);
	}

	/**
	 * Adds an array of {@link VillagerTrades.ItemListing}s for a specific profession and level only if a given Mod ID is loaded.
	 *
	 * @param event      An event to add the trades to.
	 * @param modid      A Mod ID to check.
	 * @param profession A {@link VillagerProfession} to target.
	 * @param level      The level for the trades.
	 * @param trades     An array of {@link VillagerTrades.ItemListing}s.
	 */
	public static void addCompatVillagerTrades(VillagerTradesEvent event, String modid, VillagerProfession profession, int level, VillagerTrades.ItemListing... trades) {
		if (ModList.get().isLoaded(modid)) addVillagerTrades(event, profession, level, trades);
	}

	/**
	 * Adds an array of {@link VillagerTrades.ItemListing}s to a {@link WandererTradesEvent} instance only if a given Mod ID is loaded.
	 *
	 * @param event  A {@link WandererTradesEvent} instance to use.
	 * @param modid  A Mod ID to check.
	 * @param trades An array of {@link VillagerTrades.ItemListing}s to add.
	 */
	public static void addCompatWandererTrades(WandererTradesEvent event, String modid, VillagerTrades.ItemListing... trades) {
		if (ModList.get().isLoaded(modid)) addWandererTrades(event, trades);
	}

	/**
	 * Adds an array of {@link VillagerTrades.ItemListing}s to the rare trades in a {@link WandererTradesEvent} instance only if a given Mod ID is loaded.
	 *
	 * @param event  A {@link WandererTradesEvent} instance to use.
	 * @param modid  A Mod ID to check.
	 * @param trades An array of {@link VillagerTrades.ItemListing}s to add.
	 */
	public static void addCompatRareWandererTrades(WandererTradesEvent event, String modid, VillagerTrades.ItemListing... trades) {
		if (ModList.get().isLoaded(modid)) addRareWandererTrades(event, trades);
	}

	/**
	 * A {@link BasicTrade} extension offering more constructors.
	 *
	 * @author bageldotjpg
	 */
	public static class AbnormalsTrade extends BasicTrade {
		public AbnormalsTrade(ItemStack input, ItemStack input2, ItemStack output, int maxTrades, int xp, float priceMult) {
			super(input, input2, output, maxTrades, xp, priceMult);
		}

		public AbnormalsTrade(Item input, int inputCount, Item output, int outputCount, int maxTrades, int xp, float priceMult) {
			this(new ItemStack(input, inputCount), ItemStack.EMPTY, new ItemStack(output, outputCount), maxTrades, xp, priceMult);
		}

		public AbnormalsTrade(Item input, int inputCount, Item output, int outputCount, int maxTrades, int xp) {
			this(input, inputCount, output, outputCount, maxTrades, xp, 0.15F);
		}

		public AbnormalsTrade(Item input, int inputCount, int emeraldCount, int maxTrades, int xp, float priceMult) {
			this(new ItemStack(input, inputCount), ItemStack.EMPTY, new ItemStack(Items.EMERALD, emeraldCount), maxTrades, xp, priceMult);
		}

		public AbnormalsTrade(Item input, int inputCount, int emeraldCount, int maxTrades, int xp) {
			this(input, inputCount, emeraldCount, maxTrades, xp, 0.15F);
		}

		public AbnormalsTrade(int emeraldCount, Item output, int outputCount, int maxTrades, int xp, float priceMult) {
			this(new ItemStack(Items.EMERALD, emeraldCount), ItemStack.EMPTY, new ItemStack(output, outputCount), maxTrades, xp, priceMult);
		}

		public AbnormalsTrade(int emeraldCount, Item output, int outputCount, int maxTrades, int xp) {
			this(emeraldCount, output, outputCount, maxTrades, xp, 0.15F);
		}
	}
}
