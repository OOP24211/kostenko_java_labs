package chat.server;

import java.sql.*;

public class DatabaseManager {
    // Твои настройки PostgreSQL
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "NKQAZ22";

    static {
        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();

            // 1. Создаем таблицу пользователей
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL)");

            // 2. Создаем таблицу сообщений
            stmt.execute("CREATE TABLE IF NOT EXISTS messages (" +
                    "id SERIAL PRIMARY KEY, " +
                    "room TEXT NOT NULL, " +
                    "sender TEXT NOT NULL, " +
                    "content TEXT, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // 3. Создаем таблицу комнат (ТЕПЕРЬ ВНУТРИ БЛОКА)
            stmt.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name TEXT UNIQUE NOT NULL)");

            System.out.println("[DB] Подключено к PostgreSQL. Все таблицы готовы.");

        } catch (SQLException e) {
            System.err.println("[DB] Ошибка инициализации БД: " + e.getMessage());
        }
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    public static boolean authUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static java.util.List<String> getRooms() {
        java.util.List<String> rooms = new java.util.ArrayList<>();
        String sql = "SELECT name FROM rooms";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static void saveRoom(String name) {
        String sql = "INSERT INTO rooms (name) VALUES (?) ON CONFLICT (name) DO NOTHING";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Добавим метод для сохранения истории
    public static void saveMessage(String room, String sender, String content) {
        String sql = "INSERT INTO messages (room, sender, content) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room);
            pstmt.setString(2, sender);
            pstmt.setString(3, content);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getRecentMessages(String room, int limit) {
        // Выбираем последние N сообщений для конкретной комнаты
        String sql = "SELECT sender, content FROM (SELECT * FROM messages WHERE room = ? ORDER BY timestamp DESC LIMIT ?) sub ORDER BY timestamp ASC";
        StringBuilder history = new StringBuilder();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                history.append(rs.getString("sender")).append(": ")
                       .append(rs.getString("content")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history.toString();
    }
}