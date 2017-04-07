package org.cubeville.cvchat.channels;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;

import org.cubeville.cvchat.Util;

public class LocalChannel extends Channel
{
    Set<UUID> localMuted;

    public LocalChannel(String name, String viewPermission, String sendPermission, String colorPermission, String leavePermission, String format, boolean isDefault, boolean autojoin, boolean listable, boolean filtered, Collection<String> commands) {
        super(name, viewPermission, sendPermission, colorPermission, leavePermission, format, isDefault, autojoin, listable, filtered, commands);
        localMuted = new HashSet<>();
    }

    protected void doSendMessage(CommandSender sender, String formattedMessage) {
        if(!(sender instanceof ProxiedPlayer)) return; // Console can't chat locally
        ProxiedPlayer player = (ProxiedPlayer) sender;

        // 1) Send to all players who monitor local chat and are on different server
        String serverName = player.getServer().getInfo().getName();
        Collection<ProxiedPlayer> allPlayers = ProxyServer.getInstance().getPlayers();
        String greyMessage = "§7" + Util.removeColorCodes(formattedMessage);
        for(ProxiedPlayer p: allPlayers) {
            if(p.hasPermission("cvchat.monitor.local")) {
                if(!p.getServer().getInfo().getName().equals(serverName)) {
                    p.sendMessage(greyMessage);
                }
            }
        }

        // 2) Send message to player's server for further handling
        String msg = "locchat|" + player.getUniqueId() + "|" + formattedMessage;
        ChannelManager.getInstance().getIPC().sendMessage(serverName, msg);
    }
}
