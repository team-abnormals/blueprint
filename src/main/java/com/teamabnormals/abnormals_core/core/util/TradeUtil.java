package com.teamabnormals.abnormals_core.core.util;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;

public final class TradeUtil {
	public static class ItemsForEmeraldsTrade implements ITrade {
	    private final ItemStack itemstack;
	    private final int stackSize;
	    private final int recievedSize;
	    private final int maxUses;
	    private final int givenExp;
	    private final float priceMultiplier;
	    
	    public ItemsForEmeraldsTrade(Block block, int stackSize, int recievedSize, int maxUses, int givenExp) {
	    	this(new ItemStack(block), stackSize, recievedSize, maxUses, givenExp);
	    }

	    public ItemsForEmeraldsTrade(Item item, int stackSize, int recievedSize, int givenExp) {
	    	this(new ItemStack(item), stackSize, recievedSize, 12, givenExp);
	    }

	    public ItemsForEmeraldsTrade(Item item, int stackSize, int recievedSize, int maxUses, int givenExp) {
	    	this(new ItemStack(item), stackSize, recievedSize, maxUses, givenExp);
	    }

	    public ItemsForEmeraldsTrade(ItemStack stack, int stackSize, int recievedSize, int maxUses, int givenExp) {
	    	this(stack, stackSize, recievedSize, maxUses, givenExp, 0.05F);
	    }

	    public ItemsForEmeraldsTrade(ItemStack stack, int stackSize, int recievedSize, int maxUses, int givenExp, float priceMultiplier) {
	    	this.itemstack = stack;
	    	this.stackSize = stackSize;
	    	this.recievedSize = recievedSize;
	    	this.maxUses = maxUses;
	    	this.givenExp = givenExp;
	    	this.priceMultiplier = priceMultiplier;
	    }

	    public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
	    	return new MerchantOffer(new ItemStack(Items.EMERALD, this.stackSize), new ItemStack(this.itemstack.getItem(), this.recievedSize), this.maxUses, this.givenExp, this.priceMultiplier);
	    }
	}
	
	public static class EmeraldsForItemsTrade implements ITrade {
	    private final ItemStack itemstack;
	    private final int stackSize;
	    private final int recievedSize;
	    private final int maxUses;
	    private final int givenExp;
	    private final float priceMultiplier;
		
	    public EmeraldsForItemsTrade(Block block, int stackSize, int recievedSize, int maxUses, int givenExp) {
	    	this(new ItemStack(block), stackSize, recievedSize, maxUses, givenExp);
	    }

	    public EmeraldsForItemsTrade(Item item, int stackSize, int recievedSize, int givenExp) {
	    	this(new ItemStack(item), stackSize, recievedSize, 12, givenExp);
	    }

	    public EmeraldsForItemsTrade(Item item, int stackSize, int recievedSize, int maxUses, int givenExp) {
	    	this(new ItemStack(item), stackSize, recievedSize, maxUses, givenExp);
	    }

	    public EmeraldsForItemsTrade(ItemStack stack, int stackSize, int recievedSize, int maxUses, int givenExp) {
	    	this(stack, stackSize, recievedSize, maxUses, givenExp, 0.05F);
	    }
	    
	    public EmeraldsForItemsTrade(ItemStack stack, int stackSize, int recievedSize, int maxUses, int givenExp, float priceMultiplier) {
	    	this.itemstack = stack;
	        this.stackSize = stackSize;
	        this.recievedSize = recievedSize;
	        this.maxUses = maxUses;
	        this.givenExp = givenExp;
	        this.priceMultiplier = priceMultiplier;
	    }

	    public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
	    	return new MerchantOffer(new ItemStack(this.itemstack.getItem(), this.stackSize), new ItemStack(Items.EMERALD, this.recievedSize), this.maxUses, this.givenExp, this.priceMultiplier);
	    }
	}
	
	public static class ItemsForEmeraldsAndItemsTrade implements ITrade {
		private final ItemStack buyingItem;
	    private final int buyingItemCount;
	    private final int emeraldCount;
	    private final ItemStack sellingItem;
	    private final int sellingItemCount;
	    private final int maxUses;
	    private final int givenExp;
	    private final float priceMultiplier;
	    
	    public ItemsForEmeraldsAndItemsTrade(Item buyingItem, int buyingItemCount, int emeraldCount, Item sellingItem, int sellingItemCount, int maxUses, int givenExp) {
	    	this(new ItemStack(buyingItem), buyingItemCount, emeraldCount, new ItemStack(sellingItem), sellingItemCount, maxUses, givenExp, 0.05F);
	    }
	    
	    public ItemsForEmeraldsAndItemsTrade(Block buyingItem, int buyingItemCount, int emeraldCount, Block sellingItem, int sellingItemCount, int maxUses, int givenExp) {
	    	this(new ItemStack(buyingItem), buyingItemCount, emeraldCount, new ItemStack(sellingItem), sellingItemCount, maxUses, givenExp, 0.05F);
	    }
	    
	    public ItemsForEmeraldsAndItemsTrade(Block buyingItem) {
	    	this(new ItemStack(buyingItem), 1, 5, new ItemStack(buyingItem), 2, 6, 30, 0.05F);
	    }

	    public ItemsForEmeraldsAndItemsTrade(ItemStack buyingItem, int buyingItemCount, int emeraldCount, ItemStack sellingItem, int sellingItemCount, int maxUses, int givenExp, float priceMultiplier) {
	    	this.buyingItem = buyingItem;
	    	this.buyingItemCount = buyingItemCount;
	    	this.emeraldCount = emeraldCount;
	    	this.sellingItem = sellingItem;
	    	this.sellingItemCount = sellingItemCount;
	    	this.maxUses = maxUses;
	    	this.givenExp = givenExp;
	    	this.priceMultiplier = priceMultiplier;
	    }
		
	    public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
	    	return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCount), new ItemStack(this.buyingItem.getItem(), this.buyingItemCount), new ItemStack(this.sellingItem.getItem(), this.sellingItemCount), this.maxUses, this.givenExp, this.priceMultiplier);
	    }
	}
}
