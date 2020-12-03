package com.teamabnormals.abnormals_core.core.util;

import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;

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

	public static void addVillagerTrade(VillagerTradesEvent event, int level, VillagerTrades.ITrade trade) {
		event.getTrades().get(level).add(trade);
	}

	public static void addVillagerTrade(VillagerTradesEvent event, int level, VillagerProfession profession, VillagerTrades.ITrade trade) {
		if (event.getType() == profession) addVillagerTrade(event, level, trade);
	}

	public static void addWandererTrade(WandererTradesEvent event, VillagerTrades.ITrade trade) {
		event.getGenericTrades().add(trade);
	}

	public static void addRareWandererTrade(WandererTradesEvent event, VillagerTrades.ITrade trade) {
		event.getRareTrades().add(trade);
	}
}
