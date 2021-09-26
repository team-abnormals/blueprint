package com.minecraftabnormals.abnormals_core.core.api.banner;

import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.Locale;

/**
 * @author bageldotjpg
 */
public final class BannerManager {

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
}
