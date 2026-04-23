package chat.client.controllers;

import chat.client.ChatClient;
import chat.model.Message;
import chat.model.MessageType;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class ChatController {

    @FXML private ListView<String> roomListView;   // список комнат слева
    @FXML private Label currentRoomLabel;          // заголовок текущей комнаты
    @FXML private TextArea chatArea;               // область сообщений
    @FXML private TextField messageField;          // поле ввода
    @FXML private TextField newRoomField;

    private ChatClient client;
    private String nickname;
    private String currentRoom = "General";
    private final Gson gson = new Gson();

    public void setNickname(String name) {
        this.nickname = name;
    }

    public void setRooms(List<String> rooms) {
        roomListView.getItems().addAll(rooms);
        roomListView.getSelectionModel().select("General");

        roomListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldRoom, newRoom) -> {
                    if (newRoom != null && !newRoom.equals(currentRoom)) {
                        switchRoom(newRoom);
                    }
                });
    }

    public void setClient(ChatClient client) {
        this.client = client;
        this.client.setOnMessageReceived(msg -> {
            Platform.runLater(() -> displayMessage(msg));
        });
    }

    @FXML
    public void initialize() {

    }

    @FXML
    public void onSendMessage() {
        String text = messageField.getText().trim();
        if (client != null && !text.isEmpty()) {
            Message msg = new Message(MessageType.TEXT, nickname, text);
            client.send(gson.toJson(msg));
            messageField.clear();
        }
    }
    @FXML
    public void onCreateRoom() {
        String roomName = newRoomField.getText().trim();
        if (client != null && !roomName.isEmpty()) {
            Message createMsg = new Message(MessageType.CREATE_ROOM, nickname, roomName);
            client.send(gson.toJson(createMsg));
            newRoomField.clear();
        }
    }

    private void switchRoom(String roomName) {
        currentRoom = roomName;
        currentRoomLabel.setText("# " + roomName);
        chatArea.clear();
        chatArea.appendText("--- Вы вошли в комнату " + roomName + " ---\n");

        // Отправляем серверу запрос на смену комнаты
        Message joinMsg = new Message(MessageType.JOIN_ROOM, nickname, roomName);
        client.send(gson.toJson(joinMsg));
    }

    private void displayMessage(Message msg) {
        switch (msg.getType()) {
            case UPDATE_ROOMS:
                // очищаем старый список и загружаем новый из сообщения
                Platform.runLater(() -> {
                    java.util.List<String> rooms = java.util.Arrays.asList(msg.getContent().split(","));
                    roomListView.getItems().setAll(rooms);
                });
                break;
            case TEXT:
                chatArea.appendText(msg.getSender() + ": " + msg.getContent() + "\n");
                break;
            case FILE:
                chatArea.appendText(msg.getSender() + " прислал файл: " + msg.getFileName() + "\n");
                break;
            default:
                break;
        }
    }
}