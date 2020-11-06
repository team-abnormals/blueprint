package com.teamabnormals.abnormals_core.client;

import io.github.ocelot.sonar.client.util.OnlineImageCache;
import io.github.ocelot.sonar.common.util.OnlineRequest;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.teamabnormals.abnormals_core.client.model.SlabfishHatModel;
import com.teamabnormals.abnormals_core.client.renderer.SlabfishHatLayerRenderer;
import com.teamabnormals.abnormals_core.core.AbnormalsCore;
import com.teamabnormals.abnormals_core.core.config.ACConfig;
import com.teamabnormals.abnormals_core.core.util.NetworkUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = AbnormalsCore.MODID, value = Dist.CLIENT)
public final class RewardHandler {
	public static final Map<UUID, RewardData> REWARDS = new HashMap<>();
	public static final OnlineImageCache REWARD_CACHE = new OnlineImageCache(AbnormalsCore.MODID, 1, TimeUnit.DAYS);

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new Gson();
	private static final String REWARDS_URL = "https://api.minecraftabnormals.com/rewards.json";
	private static final ResourceLocation CAPE_TEXTURE = new ResourceLocation(AbnormalsCore.MODID, "textures/abnormals_cape.png");
	private static final Set<UUID> RENDERED_CAPES = new HashSet<>();

	private static RewardProperties rewardProperties;

	public static void clientSetup(FMLClientSetupEvent event) {
		OnlineRequest.request(REWARDS_URL, Util.getServerExecutor()).thenAcceptAsync(stream -> {
			if(stream == null)
				return;

			try (InputStreamReader reader = new InputStreamReader(stream)) {
				JsonObject object = JSONUtils.fromJson(reader);
				for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
					if(entry.getKey().equals("properties")) {
						rewardProperties = GSON.fromJson(entry.getValue(), RewardProperties.class);
						continue;
					}
					REWARDS.put(UUID.fromString(entry.getKey()), GSON.fromJson(entry.getValue(), RewardData.class));
				}
			} catch (Exception e) {
				LOGGER.error("Failed to parse rewards.", e);
			}
		}, Minecraft.getInstance());

		for(PlayerRenderer renderer : Minecraft.getInstance().getRenderManager().getSkinMap().values())
			renderer.addLayer(new SlabfishHatLayerRenderer(renderer, new SlabfishHatModel()));
	}

	public static RewardProperties getRewardProperties() {
		return rewardProperties;
	}

	@SubscribeEvent
	public static void onEvent(RenderPlayerEvent.Post event) {
		PlayerEntity player = event.getPlayer();
		UUID uuid = PlayerEntity.getUUID(player.getGameProfile());

		if (player instanceof AbstractClientPlayerEntity && !RENDERED_CAPES.contains(uuid) && REWARDS.containsKey(uuid) && REWARDS.get(uuid).getTier() >= 99) {
			AbstractClientPlayerEntity clientPlayer = (AbstractClientPlayerEntity) player;
			if (clientPlayer.hasPlayerInfo()) {
				Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = clientPlayer.playerInfo.playerTextures;
				if(!playerTextures.containsKey(MinecraftProfileTexture.Type.CAPE))
				{
					playerTextures.put(MinecraftProfileTexture.Type.CAPE, CAPE_TEXTURE);
					playerTextures.put(MinecraftProfileTexture.Type.ELYTRA, CAPE_TEXTURE);
				}
				RENDERED_CAPES.add(uuid);
			}
		}
	}
	
	@SubscribeEvent
	public static void onEvent(ClientPlayerNetworkEvent.LoggedInEvent event) {
		NetworkUtil.updateSlabfish(ACConfig.CLIENT.slabfishHat.get());
	}

	public static class RewardProperties {
		private final SlabfishProperties slabfish;

		public RewardProperties(SlabfishProperties slabfish) {
			this.slabfish = slabfish;
		}

		public SlabfishProperties getSlabfishProperties() {
			return slabfish;
		}

		public static class SlabfishProperties {
			private final String defaultTypeUrl;
			private final String typeUrl;
			private final String sweaterUrl;
			private final String backpackUrl;

			public SlabfishProperties(String defaultTypeUrl, String typeUrl, String sweaterUrl, String backpackUrl) {
				this.defaultTypeUrl = defaultTypeUrl;
				this.typeUrl = typeUrl;
				this.sweaterUrl = sweaterUrl;
				this.backpackUrl = backpackUrl;
			}

			public String getDefaultTypeUrl() {
				return defaultTypeUrl;
			}

			public String getTypeUrl() {
				return typeUrl;
			}

			public String getSweaterUrl() {
				return sweaterUrl;
			}

			public String getBackpackUrl() {
				return backpackUrl;
			}
		}
	}

	public static class RewardData {

		private final String username;
		private final int tier;
		private final SlabfishData slabfish;

		public RewardData(String username, int tier, SlabfishData slabfish) {
			this.username = username;
			this.tier = tier;
			this.slabfish = slabfish;
		}

		public String getUsername() {
			return username;
		}

		public int getTier() {
			return tier;
		}

		public SlabfishData getSlabfish() {
			return slabfish;
		}

		public static class SlabfishData {
			@SerializedName("base")
			private final String typeUrl;
			@SerializedName("sweater")
			private final String sweaterUrl;
			@SerializedName("backpack")
			private final String backpackUrl;

			private String typeUrlCache;
			private String sweaterUrlCache;
			private String backpackUrlCache;

			public SlabfishData(String typeUrl, String sweaterUrl, String backpackUrl) {
				this.typeUrl = typeUrl;
				this.sweaterUrl = sweaterUrl;
				this.backpackUrl = backpackUrl;
			}

			public String getTypeUrl() {
				return this.typeUrlCache == null ? this.typeUrlCache = resolveUrl(RewardProperties.SlabfishProperties::getTypeUrl, () -> this.typeUrl) : this.typeUrlCache;
			}

			public String getSweaterUrl() {
				return this.sweaterUrlCache == null ? this.sweaterUrlCache = resolveUrl(RewardProperties.SlabfishProperties::getSweaterUrl, () -> this.sweaterUrl) : this.sweaterUrlCache;
			}

			public String getBackpackUrl() {
				return this.backpackUrlCache == null ? this.backpackUrlCache = resolveUrl(RewardProperties.SlabfishProperties::getBackpackUrl, () -> this.backpackUrl) : this.backpackUrlCache;
			}

			private static String resolveUrl(Function<RewardProperties.SlabfishProperties, String> baseUrl, Supplier<String> url) {
				String appliedUrl = baseUrl.apply(rewardProperties.getSlabfishProperties());

				if(url.get() == null)
					return null;

				if(appliedUrl == null || url.get().startsWith("http"))
					return url.get();

				return String.format(appliedUrl, url.get());
			}
		}
	}
}
