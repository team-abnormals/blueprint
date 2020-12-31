package com.minecraftabnormals.abnormals_core.core.api.banner;

import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author bageldotjpg
 */
public final class BannerManager {
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AbnormalsCore.MODID);
	public static final RegistryObject<IRecipeSerializer<?>> BANNER_PATTERN_APPLY = RECIPE_SERIALIZERS.register("banner_pattern_apply", () -> BannerRecipe.SERIALIZER);

	public static final Map<IRegistryDelegate<Item>, BannerPattern> PATTERNS = new LinkedHashMap<>();

	/**
	 * Adds a banner pattern, with one prefix for both the name and ID.
	 * Example: BannerManager.createPattern("mca", "test", "tst");
	 */
	public static BannerPattern createPattern(String prefix, String name, String id) {
		name = prefix + "_" + name;
		id = prefix + "_" + id;
		return BannerPattern.create(name.toUpperCase(Locale.ROOT), name, id, false);
	}

	/**
	 * Adds a banner pattern, with seperate name and ID prefixes, for shortening the ID and using full name for the name.
	 * Example: BannerManager.createPattern("abnormalscore", "test", "ac", "tst");
	 */
	public static BannerPattern createPattern(String namePrefix, String name, String idPrefix, String id) {
		name = namePrefix + "_" + name;
		id = idPrefix + "_" + id;
		return BannerPattern.create(name.toUpperCase(Locale.ROOT), name, id, false);
	}

	/**
	 * Adds a banner pattern, including the prefix in the name and ID.
	 * Example: BannerManager.createPattern("abnormalscore_test", "ac_tst");
	 */
	public static BannerPattern createPattern(String name, String id) {
		return BannerPattern.create(name.toUpperCase(Locale.ROOT), name, id, false);
	}

	/**
	 * Adds a banner crafting pattern for an item.
	 * @param pattern The pattern to add.
	 * @param craftingItem The item to craft the pattern with.
	 */
	public static synchronized void addPattern(BannerPattern pattern, IItemProvider craftingItem) {
		PATTERNS.put(craftingItem.asItem().delegate, pattern);
	}
}
