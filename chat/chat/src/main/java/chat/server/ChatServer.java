package chat.server;

import com.google.gson.Gson;
import chat.model.Message;
import chat.model.MessageType;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set; // Добавьте импорт

public class ChatServer extends WebSocketServer {
    private final Gson gson = new Gson();

    private final Set<String> activeRooms = ConcurrentHashMap.newKeySet();

    private final Map<WebSocket, String> clientRooms = new ConcurrentHashMap<>();
    private final Map<WebSocket, String> clientNames = new ConcurrentHashMap<>();


    public ChatServer(int port) {
        super(new InetSocketAddress(port));

        // загружаем комнаты из базы данных при старте
        java.util.List<String> savedRooms = DatabaseManager.getRooms();
        if (savedRooms.isEmpty()) {
            activeRooms.add("General");
            DatabaseManager.saveRoom("General");
        } else {
            activeRooms.addAll(savedRooms);
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Новое соединение: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String rawJson) {
        try {
            Message msg = gson.fromJson(rawJson, Message.class);
            if (msg == null) return;

            switch (msg.getType()) {
                case CREATE_ROOM:
                    handleCreateRoom(conn, msg);
                    break;
                case AUTH_REQUEST:
                    handleAuth(conn, msg, false);
                    break;
                case REG_REQUEST:
                    handleAuth(conn, msg, true);
                    break;
                case TEXT:
                case FILE:
                    handleMessageInRoom(conn, msg);
                    break;
                case JOIN_ROOM:
                    handleJoinRoom(conn, msg);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAuth(WebSocket conn, Message msg, boolean isRegistration) {
        String username = msg.getSender();
        String password = msg.getContent();
        boolean success;

        if (isRegistration) {
            success = DatabaseManager.registerUser(username, password);
        } else {
            success = DatabaseManager.authUser(username, password);
        }

        if (success) {
            clientNames.put(conn, username);

            // отправляем текущий список активных комнат
            String roomList = String.join(",", activeRooms);
            conn.send(gson.toJson(new Message(MessageType.AUTH_SUCCESS, "SERVER", roomList)));

            // автоматически заходим в General
            handleJoinRoom(conn, new Message(MessageType.JOIN_ROOM, username, "General"));

            System.out.println("[SERVER] " + (isRegistration ? "Регистрация" : "Вход") + ": " + username);
        } else {
            String errorMsg = isRegistration ? "Логин уже занят" : "Неверный логин/пароль";
            conn.send(gson.toJson(new Message(MessageType.AUTH_ERROR, "SERVER", errorMsg)));
        }
    }

    private void handleJoinRoom(WebSocket conn, Message msg) {
        String roomName = msg.getContent();
        if (roomName == null || !activeRooms.contains(roomName)) return;

        String oldRoom = clientRooms.put(conn, roomName);

        if (oldRoom != null && !oldRoom.equals(roomName)) {
            String username = clientNames.get(conn);
            broadcastToRoom(oldRoom, new Message(MessageType.TEXT, "SERVER", "👤 " + username + " покинул комнату"));
        }

        String username = clientNames.get(conn);
        if (username != null) {
            broadcastToRoom(roomName, new Message(MessageType.TEXT, "SERVER", "👤 " + username + " вошёл в комнату"));
        }

        String history = DatabaseManager.getRecentMessages(roomName, 20);
        if (!history.isEmpty()) {
            conn.send(gson.toJson(new Message(MessageType.TEXT, "АРХИВ", history)));
        }
    }

    private void handleCreateRoom(WebSocket conn, Message msg) {
        String newRoomName = msg.getContent();
        if (newRoomName != null && !newRoomName.isEmpty()) {
            if (!activeRooms.contains(newRoomName)) {
                activeRooms.add(newRoomName);
                DatabaseManager.saveRoom(newRoomName); // Сохраняем в БД!
                System.out.println("[SERVER] Создана комната: " + newRoomName);
                broadcastFullRoomList();
            }
        }
    }

    private void broadcastFullRoomList() {
        String allRooms = String.join(",", activeRooms);
        Message updateMsg = new Message(MessageType.UPDATE_ROOMS, "SERVER", allRooms);
        String json = gson.toJson(updateMsg);

        for (WebSocket client : getConnections()) {
            client.send(json);
        }
    }

    private void handleMessageInRoom(WebSocket conn, Message msg) {
        String currentRoom = clientRooms.get(conn);
        if (currentRoom == null) return;

        DatabaseManager.saveMessage(currentRoom, msg.getSender(), msg.getContent());
        broadcastToRoom(currentRoom, msg);
    }

    private void broadcastToRoom(String room, Message msg) {
        String json = gson.toJson(msg);
        for (WebSocket client : getConnections()) {
            if (room.equals(clientRooms.get(client))) {
                client.send(json);
            }
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String name = clientNames.remove(conn);
        String room = clientRooms.remove(conn);
        if (name != null && room != null) {
            broadcastToRoom(room, new Message(MessageType.TEXT, "SERVER", "👤 " + name + " отключился"));
        }
        System.out.println("Клиент отключился: " + (name != null ? name : "Неизвестный"));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Сервер запущен на порту " + getPort());
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(8887);
        server.start();
    }
}