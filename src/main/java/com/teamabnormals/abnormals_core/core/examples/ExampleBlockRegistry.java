package com.teamabnormals.abnormals_core.core.examples;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsStandingSignBlock;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsWallSignBlock;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.utils.RegistryHelper;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExampleBlockRegistry {
	public static final RegistryHelper HELPER = AbnormalsCore.REGISTRY_HELPER;
	private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation(AbnormalsCore.MODID, "textures/tile/sign.png");
	
	public static final RegistryObject<Block> BLOCK = HELPER.createBlock("block", () -> new Block(Block.Properties.from(Blocks.DIRT)), ItemGroup.BUILDING_BLOCKS);
	public static final Pair<RegistryObject<AbnormalsStandingSignBlock>, RegistryObject<AbnormalsWallSignBlock>> SIGNS = HELPER.createSignBlock("example", MaterialColor.PINK, SIGN_TEXTURE);
}