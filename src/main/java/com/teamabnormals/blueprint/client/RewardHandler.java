package com.teamabnormals.blueprint.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.teamabnormals.blueprint.client.renderer.SlabfishHatRenderLayer;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.BlueprintConfig;
import com.teamabnormals.blueprint.core.sonar.OnlineRequest;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Handles the fetching and setup of Team Abnormals rewards.
 *
 * @author Jackson
 */
@EventBusSubscriber(modid = Blueprint.MOD_ID, value = Dist.CLIENT)
public final class RewardHandler {
	public static final Map<UUID, RewardData> REWARDS = new HashMap<>();

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new Gson();
	private static final String REWARDS_URL = "https://api.minecraftabnormals.com/rewards.json";
	public static final ResourceLocation CAPE_TEXTURE = Blueprint.location("textures/abnormals_cape.png");

	private static RewardProperties rewardProperties;

	public static void clientSetup(FMLClientSetupEvent event) {
		OnlineRequest.request(REWARDS_URL, Util.backgroundExecutor()).thenAcceptAsync(stream -> {
			if (stream == null)
				return;

			try (InputStreamReader reader = new InputStreamReader(stream)) {
				JsonObject object = GsonHelper.parse(reader);
				for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
					if (entry.getKey().equals("properties")) {
						rewardProperties = GSON.fromJson(entry.getValue(), RewardProperties.class);
						continue;
					}
					REWARDS.put(UUID.fromString(entry.getKey()), GSON.fromJson(entry.getValue(), RewardData.class));
				}
			} catch (Exception e) {
				LOGGER.error("Failed to parse rewards.", e);
			}
		}, Minecraft.getInstance());
	}

	public static void addLayers(EntityRenderersEvent.AddLayers event) {
		event.getSkins().forEach(skin -> {
			PlayerRenderer renderer = event.getSkin(skin);
			if (renderer != null) renderer.addLayer(new SlabfishHatRenderLayer(renderer));
		});
	}

	@Nullable
	public static RewardProperties getRewardProperties() {
		return rewardProperties;
	}

	@SubscribeEvent
	public static void onEvent(ClientPlayerNetworkEvent.LoggingIn event) {
		NetworkUtil.updateSlabfish(SlabfishSetting.getConfig());
	}

	public enum SlabfishSetting {
		ENABLED(() -> BlueprintConfig.CLIENT.slabfishSettings.enabled),
		SWEATER(() -> BlueprintConfig.CLIENT.slabfishSettings.sweaterEnabled),
		BACKPACK(() -> BlueprintConfig.CLIENT.slabfishSettings.backpackEnabled),
		TYPE(() -> BlueprintConfig.CLIENT.slabfishSettings.typeEnabled);

		private final Supplier<ModConfigSpec.ConfigValue<Boolean>> configValue;

		SlabfishSetting(Supplier<ModConfigSpec.ConfigValue<Boolean>> configValue) {
			this.configValue = configValue;
		}

		public ModConfigSpec.ConfigValue<Boolean> getConfigValue() {
			return this.configValue.get();
		}

		public static byte getConfig() {
			int value = 0;
			for (SlabfishSetting setting : values())
				if (setting.getConfigValue().get())
					value |= 1 << setting.ordinal();
			return (byte) value;
		}

		public static boolean getSetting(IDataManager data, SlabfishSetting flag) {
			return ((data.getValue(Blueprint.SLABFISH_SETTINGS) >> flag.ordinal()) & 1) > 0;
		}
	}

	public static final class RewardProperties {
		private final SlabfishProperties slabfish;

		public RewardProperties(SlabfishProperties slabfish) {
			this.slabfish = slabfish;
		}

		public SlabfishProperties getSlabfishProperties() {
			return this.slabfish;
		}

		public static final class SlabfishProperties {
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
				return this.defaultTypeUrl;
			}

			public String getTypeUrl() {
				return this.typeUrl;
			}

			public String getSweaterUrl() {
				return this.sweaterUrl;
			}

			public String getBackpackUrl() {
				return this.backpackUrl;
			}
		}
	}

	public static final class RewardData {
		private final String username;
		private final int tier;
		private final SlabfishData slabfish;

		public RewardData(String username, int tier, SlabfishData slabfish) {
			this.username = username;
			this.tier = tier;
			this.slabfish = slabfish;
		}

		public String getUsername() {
			return this.username;
		}

		public int getTier() {
			return this.tier;
		}

		public SlabfishData getSlabfish() {
			return this.slabfish;
		}

		public static final class SlabfishData {
			private final boolean translucent;
			@SerializedName("type")
			private final String typeUrl;
			@SerializedName("sweater")
			private final String sweaterUrl;
			@SerializedName("backpack")
			private final String backpackUrl;

			private String typeUrlCache;
			private String sweaterUrlCache;
			private String backpackUrlCache;

			public SlabfishData(boolean translucent, String typeUrl, String sweaterUrl, String backpackUrl) {
				this.translucent = translucent;
				this.typeUrl = typeUrl;
				this.sweaterUrl = sweaterUrl;
				this.backpackUrl = backpackUrl;
			}

			public boolean isTranslucent() {
				return this.translucent;
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

				if (url.get() == null)
					return null;

				if (appliedUrl == null || url.get().startsWith("http"))
					return url.get();

				return String.format(appliedUrl, url.get());
			}
		}
	}
}
