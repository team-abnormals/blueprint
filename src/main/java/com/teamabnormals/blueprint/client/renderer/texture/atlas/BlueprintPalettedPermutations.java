package com.teamabnormals.blueprint.client.renderer.texture.atlas;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A better version of PalettedPermutations that uses other sources for its textures.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class BlueprintPalettedPermutations implements SpriteSource {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<BlueprintPalettedPermutations> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.list(SpriteSources.CODEC).fieldOf("sources").forGetter((permutations) -> {
			return permutations.sources;
		}), ResourceLocation.CODEC.fieldOf("palette_key").forGetter((permutations) -> {
			return permutations.paletteKey;
		}), Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).fieldOf("permutations").forGetter((permutations) -> {
			return permutations.permutations;
		})).apply(instance, BlueprintPalettedPermutations::new);
	});
	private final List<SpriteSource> sources;
	private final Map<String, ResourceLocation> permutations;
	private final ResourceLocation paletteKey;

	public BlueprintPalettedPermutations(List<SpriteSource> sources, ResourceLocation p_266681_, Map<String, ResourceLocation> p_266741_) {
		this.sources = sources;
		this.permutations = p_266741_;
		this.paletteKey = p_266681_;
	}

	public void run(ResourceManager manager, SpriteSource.Output output) {
		Supplier<int[]> supplier = Suppliers.memoize(() -> loadPaletteEntryFromImage(manager, this.paletteKey));
		Map<String, Supplier<IntUnaryOperator>> map = new HashMap<>();
		this.permutations.forEach((p_267108_, p_266969_) -> {
			map.put(p_267108_, Suppliers.memoize(() -> createPaletteMapping(supplier.get(), loadPaletteEntryFromImage(manager, p_266969_))));
		});
		HashMap<ResourceLocation, SpriteSource.SpriteSupplier> tempOutputMap = new HashMap<>();
		Output tempOutput = new Output() {
			@Override
			public void add(ResourceLocation location, SpriteSupplier spriteSupplier) {
				SpriteSource.SpriteSupplier oldSupplier = tempOutputMap.put(location, spriteSupplier);
				if (oldSupplier != null) oldSupplier.discard();
			}

			@Override
			public void removeAll(Predicate<ResourceLocation> predicate) {
				Iterator<Map.Entry<ResourceLocation, SpriteSupplier>> iterator = tempOutputMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<ResourceLocation, SpriteSource.SpriteSupplier> entry = iterator.next();
					if (predicate.test(entry.getKey())) {
						entry.getValue().discard();
						iterator.remove();
					}
				}
			}
		};

		for (SpriteSource source : this.sources) {
			source.run(manager, tempOutput);
		}

		for (var collectedEntry : tempOutputMap.entrySet()) {
			ResourceLocation resourcelocation = collectedEntry.getKey();
			ResourceLocation resourcelocation1 = TEXTURE_ID_CONVERTER.idToFile(resourcelocation);
			Optional<Resource> optional = manager.getResource(resourcelocation1);
			if (optional.isEmpty()) {
				LOGGER.warn("Unable to find texture {}", resourcelocation1);
			} else {
				LazyLoadedImage lazyloadedimage = new LazyLoadedImage(resourcelocation1, optional.get(), map.size());
				for (Map.Entry<String, Supplier<IntUnaryOperator>> entry : map.entrySet()) {
					ResourceLocation resourcelocation2 = resourcelocation.withSuffix("_" + entry.getKey());
					output.add(resourcelocation2, new BlueprintPalettedPermutations.PalettedSpriteSupplier(lazyloadedimage, entry.getValue(), resourcelocation2));
				}
			}
		}
	}

	private static IntUnaryOperator createPaletteMapping(int[] p_266839_, int[] p_266776_) {
		if (p_266776_.length != p_266839_.length) {
			LOGGER.warn("Palette mapping has different sizes: {} and {}", p_266839_.length, p_266776_.length);
			throw new IllegalArgumentException();
		} else {
			Int2IntMap int2intmap = new Int2IntOpenHashMap(p_266776_.length);
			for (int i = 0; i < p_266839_.length; ++i) {
				int j = p_266839_[i];
				if (FastColor.ABGR32.alpha(j) != 0) {
					int2intmap.put(FastColor.ABGR32.transparent(j), p_266776_[i]);
				}
			}
			return (p_267899_) -> {
				int k = FastColor.ABGR32.alpha(p_267899_);
				if (k == 0) {
					return p_267899_;
				} else {
					int l = FastColor.ABGR32.transparent(p_267899_);
					int i1 = int2intmap.getOrDefault(l, FastColor.ABGR32.opaque(l));
					int j1 = FastColor.ABGR32.alpha(i1);
					return FastColor.ABGR32.color(k * j1 / 255, i1);
				}
			};
		}
	}

	public static int[] loadPaletteEntryFromImage(ResourceManager p_267184_, ResourceLocation p_267059_) {
		Optional<Resource> optional = p_267184_.getResource(TEXTURE_ID_CONVERTER.idToFile(p_267059_));
		if (optional.isEmpty()) {
			LOGGER.error("Failed to load palette image {}", p_267059_);
			throw new IllegalArgumentException();
		} else {
			try (InputStream inputstream = optional.get().open(); NativeImage nativeimage = NativeImage.read(inputstream)) {
				return nativeimage.getPixelsRGBA();
			} catch (Exception exception) {
				LOGGER.error("Couldn't load texture {}", p_267059_, exception);
				throw new IllegalArgumentException();
			}
		}
	}

	public SpriteSourceType type() {
		return BlueprintSpriteSources.PALETTED_PERMUTATIONS;
	}

	@OnlyIn(Dist.CLIENT)
	private record PalettedSpriteSupplier(LazyLoadedImage baseImage, Supplier<IntUnaryOperator> palette, ResourceLocation permutationLocation) implements SpriteSource.SpriteSupplier {
		@SuppressWarnings("deprecation")
		@Nullable
		public SpriteContents get() {
			try {
				NativeImage nativeimage = this.baseImage.get().mappedCopy(this.palette.get());
				return new SpriteContents(this.permutationLocation, new FrameSize(nativeimage.getWidth(), nativeimage.getHeight()), nativeimage, AnimationMetadataSection.EMPTY);
			} catch (IllegalArgumentException | IOException ioexception) {
				BlueprintPalettedPermutations.LOGGER.error("unable to apply palette to {}", this.permutationLocation, ioexception);
			} finally {
				this.baseImage.release();
			}
			return null;
		}

		public void discard() {
			this.baseImage.release();
		}
	}
}
