package com.teamabnormals.abnormals_core.core.examples;

import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.library.Test;
import com.teamabnormals.abnormals_core.core.util.RegistryHelper;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Test
@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExampleItemRegistry {
	public static final RegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER;
	
	public static final RegistryObject<Item> ITEM = HELPER.createItem("example", () -> RegistryHelper.createSimpleItem(ItemGroup.FOOD));
	public static final RegistryObject<Item> COW_SPAWN_EGG = HELPER.createSpawnEggItem("example", () -> EntityType.COW, 100, 200);
	//public static final RegistryObject<Item> BOAT = HELPER.createBoatItem("example", ExampleBlockRegistry.BLOCK);
}