package org.cubeville.cvchat.tickets;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import org.cubeville.cvchat.DaoBase;

public class TicketDao extends DaoBase
{
    public TicketDao(String dbUser, String dbPassword, String dbDatabase) {
        super(dbUser, dbPassword, dbDatabase);
    }

    protected synchronized List<Ticket> loadTickets() {
        Connection connection = getConnection();
        List<Ticket> ret = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            statement.execute("SELECT id, player, player_name, text, server, world, x, y, z, UNIX_TIMESTAMP(creation_timestamp) AS 'creation_timestamp', is_closed, is_held, is_claimed, player_notified, moderator, moderator_name, moderator_text, UNIX_TIMESTAMP(moderator_timestamp) AS 'moderator_timestamp' FROM tickets order by id");
            ResultSet rs = statement.getResultSet();
            while(rs.next()) {
                String moderatorId = rs.getString("moderator");
                Ticket ticket = new Ticket(rs.getInt("id"),
                                           UUID.fromString(rs.getString("player")),
                                           rs.getString("player_name"),
                                           rs.getString("text"),
                                           rs.getString("server"),
                                           rs.getString("world"),
                                           rs.getInt("x"),
                                           rs.getInt("y"),
                                           rs.getInt("z"),
                                           rs.getLong("creation_timestamp") * 1000,
                                           rs.getBoolean("is_closed"),
                                           rs.getBoolean("is_held"),
                                           rs.getBoolean("is_claimed"),
                                           rs.getBoolean("player_notified"),
                                           moderatorId == null ? null : UUID.fromString(moderatorId),
                                           rs.getString("moderator_name"),
                                           rs.getString("moderator_text"),
                                           rs.getLong("moderator_timestamp"));
                ret.add(ticket);
            }
            statement.close();
        }
        catch(SQLException e) {
            System.out.println("Could not load tickets!");
        }
        return ret;
    }

    protected synchronized int createTicket(Ticket ticket) {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO tickets (player, player_name, text, server, world, x, y, z, creation_timestamp) values(?, ?, ?, ?, ?, ?, ?, ?, from_unixtime(?))", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, ticket.getPlayer().toString());
            statement.setString(2, ticket.getPlayerName());
            statement.setString(3, ticket.getText());
            statement.setString(4, ticket.getServer());
            statement.setString(5, ticket.getWorld());
            statement.setInt(6, ticket.getX());
            statement.setInt(7, ticket.getY());
            statement.setInt(8, ticket.getZ());
            statement.setLong(9, ticket.getCreationTimestamp() / 1000);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            ticket.setId(id);
            statement.close();
            return id;
        }
        catch(SQLException e) {
            System.out.println("Creating ticket in database failed!");
            throw new RuntimeException("Ticket creation failed.");
        }
    }

    protected synchronized void updateTicket(Ticket ticket) {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE tickets SET is_closed = ?, is_held = ?, is_claimed = ?, player_notified = ?, moderator = ?, moderator_name = ?, moderator_text = ?, moderator_timestamp = FROM_UNIXTIME(?) WHERE id = ?");
            statement.setInt(9, ticket.getId());
            statement.setBoolean(1, ticket.isClosed());
            statement.setBoolean(2, ticket.isHeld());
            statement.setBoolean(3, ticket.isClaimed());
            statement.setBoolean(4, ticket.playerNotified());
            statement.setString(5, ticket.getModerator() == null ? null : ticket.getModerator().toString());
            statement.setString(6, ticket.getModeratorName());
            statement.setString(7, ticket.getModeratorText());
            statement.setLong(8, ticket.getModeratorTimestamp() / 1000);
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException e) {
            System.out.println("Updating ticket failed.");
            throw new RuntimeException("Updating the ticket failed.");
        }
    }
    
}
