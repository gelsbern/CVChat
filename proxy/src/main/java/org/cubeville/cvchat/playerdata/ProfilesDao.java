package org.cubeville.cvchat.playerdata;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.cubeville.cvchat.DaoBase;

public class ProfilesDao extends DaoBase
{
    private static ProfilesDao instance;
    public static ProfilesDao getInstance() { return instance; }
    
    public ProfilesDao(String dbUser, String dbPassword, String dbDatabase) {
        super(dbUser, dbPassword, dbDatabase);
        instance = this;
    }

    public synchronized List<ProfileEntry> getProfileEntries(UUID player) {
        Connection connection = getConnection();
        List<ProfileEntry> ret = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT UNIX_TIMESTAMP(p.comment_time) as comment_time, p.comment as comment, pda.uuid as author FROM profile p, playerdata pd, playerdata pda WHERE pd.uuid = ? and pd.id = p.player_id and pda.id = p.author_id order by comment_time desc");
            statement.setString(1, player.toString());
            statement.execute();
            ResultSet rs = statement.getResultSet();
            while(rs.next()) {
                long commentTime = rs.getLong("comment_time") * 1000;
                String comment = rs.getString("comment");
                UUID author = UUID.fromString(rs.getString("author"));
                ret.add(new ProfileEntry(commentTime, comment, author));
            }
            statement.close();
        }
        catch(SQLException e) {
            System.out.println("Could not load profile entries!");
            throw new RuntimeException("Could not load profile entries!");
        }
        return ret;
    }

    public synchronized void addProfileEntry(UUID player, ProfileEntry entry) {
        Connection connection = getConnection();
        try {
            int authorId = PlayerDataManager.getInstance().getDatabaseIndex(entry.getAuthor());
            int playerId = PlayerDataManager.getInstance().getDatabaseIndex(player);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO profile (player_id, comment_time, comment, author_id) VALUES (?, NOW(), ?, ?)");
            statement.setInt(1, playerId);
            statement.setString(2, entry.getComment());
            statement.setInt(3, authorId);
            statement.executeUpdate();
            statement.close();
        }
        catch(SQLException e) {
            System.out.println("Could not save profile entry!");
            throw new RuntimeException("Could not save profile entry!");
        }
    }
}
