package com.minecraftabnormals.abnormals_core.core.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Item.class)
public interface ItemInvokerMixin {
	@Invoker
	boolean callAllowdedIn(ItemGroup group);
}
