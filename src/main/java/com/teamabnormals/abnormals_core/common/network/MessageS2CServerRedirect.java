package com.teamabnormals.abnormals_core.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Allows servers to redirect clients to another server.
 * @author Jackson
 */
public final class MessageS2CServerRedirect {
	private static final Minecraft MINECRAFT = Minecraft.getInstance();
	private final String connectionAddress;

	public MessageS2CServerRedirect(String address) {
		this.connectionAddress = address;
	}

	public void serialize(PacketBuffer buf) {
		buf.writeString(this.connectionAddress);
	}

	public static MessageS2CServerRedirect deserialize(PacketBuffer buf) {
		return new MessageS2CServerRedirect(buf.readString());
	}

	public static void handle(MessageS2CServerRedirect message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(() -> {
				World world = MINECRAFT.world;
				Screen currentScreen = MINECRAFT.currentScreen;
				boolean integrated = MINECRAFT.isIntegratedServerRunning();

				if (world != null) {
					world.sendQuittingDisconnectingPacket();
					MINECRAFT.unloadWorld(new DirtMessageScreen(new TranslationTextComponent("abnormals_core.message.redirect")));

					MainMenuScreen menuScreen = new MainMenuScreen();
					MINECRAFT.displayGuiScreen(integrated ? menuScreen : new MultiplayerScreen(menuScreen));

					if (currentScreen != null)
						MINECRAFT.displayGuiScreen(new ConnectingScreen(currentScreen, MINECRAFT, new ServerData("Redirect", message.connectionAddress, false)));
				}
			});
			context.setPacketHandled(true);
		}
	}
}
