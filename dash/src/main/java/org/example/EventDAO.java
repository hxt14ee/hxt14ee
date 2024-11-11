package org.example;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    public static List<Event> getAllEvents(){
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setTitle(rs.getString("title"));
                event.setStart(rs.getTimestamp("start").toString());
                event.setEnd(rs.getTimestamp("end").toString());
                events.add(event);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return events;


}
    public static void addEvent(Event event){
        String sql = "INSERT INTO events (title, start, \"end\") VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1, event.getTitle());
            stmt.setTimestamp(2, Timestamp.valueOf(event.getStart()));
            stmt.setTimestamp(3, Timestamp.valueOf(event.getEnd()));

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                event.setId(keys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

public static void deleteEvent(int id) {
    String sql = "DELETE FROM events WHERE id = ?";

    try (Connection conn = Database.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        stmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
