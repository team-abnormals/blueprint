package com.minecraftabnormals.abnormals_core.core.util;

import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.fml.ModList;

public final class TradeUtil {
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

	public static void addVillagerTrades(VillagerTradesEvent event, int level, VillagerTrades.ITrade... trades) {
		for (VillagerTrades.ITrade trade : trades) event.getTrades().get(level).add(trade);
	}

	public static void addVillagerTrades(VillagerTradesEvent event, VillagerProfession profession, int level, VillagerTrades.ITrade... trades) {
		if (event.getType() == profession) addVillagerTrades(event, level, trades);
	}

	public static void addWandererTrades(WandererTradesEvent event, VillagerTrades.ITrade... trades) {
		for (VillagerTrades.ITrade trade : trades) event.getGenericTrades().add(trade);
	}

	public static void addRareWandererTrades(WandererTradesEvent event, VillagerTrades.ITrade... trades) {
		for (VillagerTrades.ITrade trade : trades) event.getRareTrades().add(trade);
	}

	public static void addCompatVillagerTrades(VillagerTradesEvent event, String modid, int level, VillagerTrades.ITrade... trades) {
		if (ModList.get().isLoaded(modid)) addVillagerTrades(event, level, trades);
	}

	public static void addCompatVillagerTrades(VillagerTradesEvent event, String modid, VillagerProfession profession, int level, VillagerTrades.ITrade... trades) {
		if (ModList.get().isLoaded(modid)) addVillagerTrades(event, profession, level, trades);
	}

	public static void addCompatWandererTrades(WandererTradesEvent event, String modid, VillagerTrades.ITrade... trades) {
		if (ModList.get().isLoaded(modid)) addWandererTrades(event, trades);
	}

	public static void addCompatRareWandererTrades(WandererTradesEvent event, String modid, VillagerTrades.ITrade... trades) {
		if (ModList.get().isLoaded(modid)) addRareWandererTrades(event, trades);
	}
}
