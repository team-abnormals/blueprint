package com.teamabnormals.abnormals_core.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Allows servers to redirect clients to another server.
 *
 * @author Jackson
 */
public class MessageS2CServerRedirect {

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
                Minecraft minecraft = Minecraft.getInstance();
                boolean integrated = minecraft.isIntegratedServerRunning();
                if (minecraft.world != null) {
                    minecraft.world.sendQuittingDisconnectingPacket();
                    minecraft.unloadWorld(new DirtMessageScreen(new StringTextComponent("Redirecting...")));

                    if (integrated) minecraft.displayGuiScreen(new MainMenuScreen());
                    else minecraft.displayGuiScreen(new MultiplayerScreen(new MainMenuScreen()));

                    if (minecraft.currentScreen != null)
                        minecraft.displayGuiScreen(new ConnectingScreen(minecraft.currentScreen, minecraft, new ServerData("Redirect", message.connectionAddress, false)));
                }
            });
            context.setPacketHandled(true);
        }
    }
}
