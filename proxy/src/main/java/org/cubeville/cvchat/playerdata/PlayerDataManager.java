package org.cubeville.cvchat.playerdata;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class PlayerDataManager
{
    Map<UUID, PlayerData> playerData;
    Map<String, UUID> playerNameMap;

    PlayerDataDao dao;

    private static PlayerDataManager instance;
    public static PlayerDataManager getInstance() {
        return instance;
    }
    
    public PlayerDataManager(PlayerDataDao dao) {
        instance = this;
        this.dao = dao;
        playerData = dao.loadPlayerData();
        playerNameMap = new ConcurrentHashMap<>();
        for(UUID playerId: playerData.keySet()) {
            playerNameMap.put(playerData.get(playerId).getName().toLowerCase(), playerId);
        }
    }

    public boolean isBanned(UUID playerId, boolean startTempban) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return false;
        if(pd.getBanIssued() != null) {
            // Perm ban
            if(pd.getBanDuration() == null) return true;
            // TempBan
            if(startTempban == false) return true;
            if(pd.getBanStart() == null) {
                pd.setBanStart(System.currentTimeMillis());
                dao.updatePlayerData(pd);
                return true;
            }
            if(System.currentTimeMillis() < pd.getBanStart() + pd.getBanDuration()) {
                return true;
            }
            pd.setBanStart(null);
            pd.setBanDuration(null);
            pd.setBanIssued(null);
            pd.setBanReason(null);
            pd.setBannedBy(null);
            dao.updatePlayerData(pd);
        }
        return false;
    }

    public long getEndOfTempban(UUID playerId) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return System.currentTimeMillis();
        return pd.getBanStart() + pd.getBanDuration();
    }
    
    public boolean isPermanentlyBanned(UUID playerId) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return false;
        return pd.getBanDuration() == null;
    }
    
    public String getBanReason(UUID playerId) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return "";
        return pd.getBanReason();
    }

    public UUID getPlayerId(String playerName) {
        return playerNameMap.get(playerName.toLowerCase());
    }

    public List<String> getMatchingPlayerNames(String search) {
        List<String> ret = new ArrayList<>();
        for(String name: playerNameMap.keySet()) {
            if(name.contains(search.toLowerCase())) {
                ret.add(playerData.get(playerNameMap.get(name)).getName());
            }
        }
        return ret;
    }
    
    public void banPlayer(UUID playerId, UUID bannedBy, String banReason, long duration) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return;
        pd.setBanStart(null);
        pd.setBanIssued(System.currentTimeMillis());
        pd.setBanDuration(duration == 0 ? null : duration);
        pd.setBannedBy(bannedBy);
        pd.setBanReason(banReason);
        dao.updatePlayerData(pd);
    }

    public void unbanPlayer(UUID playerId) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return;
        pd.setBanStart(null);
        pd.setBanIssued(null);
        pd.setBanDuration(null);
        pd.setBannedBy(null);
        pd.setBanReason(null);
        dao.updatePlayerData(pd);
    }
    
    public String getPlayerName(UUID playerId) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return null;
        return pd.getName();
    }

    public void changePlayerName(UUID playerId, String playerName) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return;
        playerNameMap.remove(pd.getName().toLowerCase());
        pd.setName(playerName);
        playerNameMap.put(playerName.toLowerCase(), pd.getPlayerId());
        dao.updatePlayerData(pd);
    }

    public boolean isPlayerKnown(UUID playerId) {
        return playerData.containsKey(playerId);
    }

    public void addPlayer(UUID playerId, String playerName, String ipAddress) {
        PlayerData pd = new PlayerData(playerId, playerName, ipAddress);
        playerData.put(playerId, pd);
        playerNameMap.put(playerName.toLowerCase(), playerId);
        dao.updatePlayerData(pd);
    }

    public void playerLogin(UUID playerId, String ipAddress, int priority) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return;
        pd.setLastLogin(System.currentTimeMillis());
        pd.setIpAddress(ipAddress);
        pd.setPriority(priority);
        dao.updatePlayerData(pd);
    }

    public void playerLogout(UUID playerId) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return;
        pd.setLastLogout(System.currentTimeMillis());
        dao.updatePlayerData(pd);
    }

    public long getLastOnline(UUID playerId) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return 0;
        return pd.getLastLogout();
    }

    public long getFirstLogin(UUID playerId) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return 0;
        return pd.getFirstLogin();
    }
    
    public int getPriority(UUID playerId) {
        PlayerData pd = playerData.get(playerId);
        if(pd == null) return 0;
        return pd.getPriority();
    }

    public boolean outranks(UUID sender, UUID player) {
        PlayerData sd = playerData.get(sender);
        if(sd == null) return false;
        PlayerData pd = playerData.get(player);
        if(pd == null) return false;
        return sd.getPriority() > pd.getPriority();
    }

    public boolean outranksOrEqual(UUID sender, UUID player) {
        PlayerData sd = playerData.get(sender);
        if(sd == null) return false;
        PlayerData pd = playerData.get(player);
        if(pd == null) return false;
        return sd.getPriority() >= pd.getPriority();
    }
    
    public String getIPAddress(UUID player) {
        PlayerData pd = playerData.get(player);
        if(pd == null) return null;
        return pd.getIpAddress();
    }

    protected int getDatabaseIndex(UUID player) {
        PlayerData pd = playerData.get(player);
        if(pd == null) return 0;
        return pd.getId();
    }

    public boolean finishedTutorial(UUID player) {
        PlayerData pd = playerData.get(player);
        if(pd == null) return false;
        return pd.getTutorialFinished();
    }

    public void setFinishedTutorial(UUID player) {
        PlayerData pd = playerData.get(player);
        if(pd == null) return;
        pd.setTutorialFinished(true);
        dao.updatePlayerData(pd);
    }

    public String getPrefix(UUID player) {
        PlayerData pd = playerData.get(player);
        if(pd == null) return "";
        return pd.getPrefix();
    }

    public void changePrefix(UUID player, String prefix) {
        PlayerData pd = playerData.get(player);
        if(pd == null) return;
        pd.setPrefix(prefix);
        dao.updatePlayerData(pd);
    }
}
