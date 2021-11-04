package com.teamabnormals.blueprint.core.api.banner;

import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.Locale;

/**
 * Utility class for creating new {@link BannerPattern}s.
 *
 * @author bageldotjpg
 * @see BannerPattern
 */
public final class BannerManager {
	/**
	 * Adds a banner pattern, with one prefix for both the name and ID.
	 * Example: BannerManager.createPattern("bp", "test", "tst");
	 *
	 * @param prefix A prefix to use.
	 * @param name   A name for the {@link BannerPattern}.
	 * @param id     ID for the {@link BannerPattern}.
	 * @return A new {@link BannerPattern}.
	 * @see BannerPattern#create(String, String, String, boolean)
	 */
	public static BannerPattern createPattern(String prefix, String name, String id) {
		name = prefix + "_" + name;
		id = prefix + "_" + id;
		return BannerPattern.create(name.toUpperCase(Locale.ROOT), name, id, false);
	}

	/**
	 * Adds a banner pattern, with seperate name and ID prefixes, for shortening the ID and using full name for the name.
	 * Example: BannerManager.createPattern("blueprint", "test", "bp", "tst");
	 *
	 * @param namePrefix A prefix for the name.
	 * @param name       A name for the {@link BannerPattern}.
	 * @param idPrefix   A prefix for the ID.
	 * @param id         An ID for the {@link BannerPattern}.
	 * @return A new {@link BannerPattern}.
	 * @see BannerPattern#create(String, String, String, boolean)
	 */
	public static BannerPattern createPattern(String namePrefix, String name, String idPrefix, String id) {
		name = namePrefix + "_" + name;
		id = idPrefix + "_" + id;
		return BannerPattern.create(name.toUpperCase(Locale.ROOT), name, id, false);
	}

	/**
	 * Adds a banner pattern, including the prefix in the name and ID.
	 * Example: BannerManager.createPattern("blueprint_test", "bp_tst");
	 *
	 * @param name A name for the {@link BannerPattern}.
	 * @param id   An ID for the {@link BannerPattern}.
	 * @return A new {@link BannerPattern}.
	 * @see BannerPattern#create(String, String, String, boolean)
	 */
	public static BannerPattern createPattern(String name, String id) {
		return BannerPattern.create(name.toUpperCase(Locale.ROOT), name, id, false);
	}
}
