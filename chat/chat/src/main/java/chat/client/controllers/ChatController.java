package chat.client.controllers;

import chat.client.ChatClient;
import chat.model.Message;
import chat.model.MessageType;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ChatController {

    @FXML private ListView<String> roomListView;
    @FXML private Label currentRoomLabel;
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML private TextField newRoomField;

    private ChatClient client;
    private String nickname;
    private String currentRoom = "General";
    private final Gson gson = new Gson();
    private final Map<String, byte[]> imageCache = new java.util.concurrent.ConcurrentHashMap<>();

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
    public void initialize() {}

    @FXML
    public void onSendMessage() {
        String text = messageField.getText().trim();

        if (text.startsWith("/open ")) {
            String fileName = text.substring(6).trim();
            byte[] imageData = imageCache.get(fileName);
            if (imageData != null) {
                showImagePreview(imageData, fileName);
                chatArea.appendText("🖼️ Открыто: " + fileName + "\n");
            } else {
                Message req = new Message(MessageType.FILE_REQUEST, nickname, fileName);
                client.send(gson.toJson(req));
                chatArea.appendText(" Запрошено: " + fileName + "\n");
            }
            messageField.clear();
            return;
        }

        if (text.startsWith("/save ")) {
            String fileName = text.substring(6).trim();
            byte[] imageData = imageCache.get(fileName);
            if (imageData != null) {
                saveFile(imageData, fileName);
                chatArea.appendText(" Сохранено: " + fileName + "\n");
            } else {
                chatArea.appendText(" Не найдено: " + fileName + "\n");
            }
            messageField.clear();
            return;
        }

        if (text.startsWith("/download ")) {
            String fileName = text.substring(10).trim();
            Message req = new Message(MessageType.FILE_REQUEST, nickname, fileName);
            client.send(gson.toJson(req));
            chatArea.appendText(" Запрошен файл: " + fileName + "\n");
            messageField.clear();
            return;
        }

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

    @FXML
    public void onSendFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        File file = fileChooser.showOpenDialog(messageField.getScene().getWindow());

        if (file != null && client != null) {
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                if (data.length > 5 * 1024 * 1024) {
                    chatArea.appendText(" Файл больше 5 МБ\n");
                    return;
                }
                Message msg = new Message(MessageType.FILE, nickname, file.getName(), data);
                client.send(gson.toJson(msg));
                chatArea.appendText(" Вы отправили: " + file.getName() + "\n");
            } catch (Exception e) {
                chatArea.appendText(" Ошибка: " + e.getMessage() + "\n");
            }
        }
    }

    private void switchRoom(String roomName) {
        currentRoom = roomName;
        currentRoomLabel.setText("# " + roomName);
        chatArea.clear();
        chatArea.appendText(" Вы вошли в комнату " + roomName + " \n");
        Message joinMsg = new Message(MessageType.JOIN_ROOM, nickname, roomName);
        client.send(gson.toJson(joinMsg));
    }

    private void displayMessage(Message msg) {
        switch (msg.getType()) {
            case UPDATE_ROOMS:
                Platform.runLater(() -> {
                    List<String> rooms = java.util.Arrays.asList(msg.getContent().split(","));
                    roomListView.getItems().setAll(rooms);
                });
                break;
            case TEXT:
                chatArea.appendText(msg.getSender() + ": " + msg.getContent() + "\n");
                break;
            case FILE:
                if (msg.getFileData() != null && msg.getFileData().length > 0) {
                    boolean isImage = msg.getFileName().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|bmp)$");
                    if (isImage) {
                        imageCache.put(msg.getFileName(), msg.getFileData());
                        showImagePreview(msg.getFileData(), msg.getFileName());
                        chatArea.appendText("🖼️ " + msg.getSender() + " отправил: " + msg.getFileName() + "\n");
                        chatArea.appendText("    /open " + msg.getFileName() + " - открыть снова\n");
                        chatArea.appendText("    /save " + msg.getFileName() + " - сохранить\n");
                    } else {
                        saveFile(msg.getFileData(), msg.getFileName());
                        chatArea.appendText("📎 " + msg.getSender() + " отправил: " + msg.getFileName() + "\n");
                    }
                    break;
                }

                boolean isImage = msg.getFileName().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|bmp)$");
                String emoji = isImage ? "🖼️" : "📎";
                chatArea.appendText(emoji + " " + msg.getSender() + ": " + msg.getFileName() + "\n");

                if (isImage) {
                    chatArea.appendText("    /open " + msg.getFileName() + " - открыть\n");
                    chatArea.appendText("    /save " + msg.getFileName() + " - сохранить\n");
                } else {
                    chatArea.appendText("    /download " + msg.getFileName() + " - скачать\n");
                }
                break;
            default:
                break;
        }
    }

    private void showImagePreview(byte[] imageData, String fileName) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Изображение: " + fileName);

        Image image = new Image(new java.io.ByteArrayInputStream(imageData));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(500);
        imageView.setFitHeight(400);
        imageView.setPreserveRatio(true);

        Button saveButton = new Button(" Сохранить на диск");
        saveButton.setStyle("-fx-background-color: #5865f2; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            saveFile(imageData, fileName);
            dialog.close();
        });

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10, imageView, saveButton);
        vbox.setStyle("-fx-padding: 10; -fx-alignment: center;");

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    private void saveFile(byte[] data, String fileName) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(fileName);
        File saveFile = chooser.showSaveDialog(messageField.getScene().getWindow());

        if (saveFile != null) {
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                fos.write(data);
                chatArea.appendText(" Сохранено: " + saveFile.getName() + "\n");
            } catch (Exception e) {
                chatArea.appendText(" Ошибка сохранения\n");
            }
        }
    }
}