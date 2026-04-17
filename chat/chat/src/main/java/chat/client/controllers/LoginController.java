package chat.client.controllers;

import chat.model.Message;
import chat.model.MessageType;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import chat.client.ChatClient;

public class LoginController {
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private ChatClient client;
    private final Gson gson = new Gson();

    @FXML
    public void onLogin() {
        connectAndSend(MessageType.AUTH_REQUEST);
    }

    @FXML
    public void onRegister() {
        connectAndSend(MessageType.REG_REQUEST);
    }

    private void connectAndSend(MessageType type) {
        String login = loginField.getText().trim();
        String pass = passwordField.getText().trim();

        if (login.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Заполните все поля!");
            return;
        }

        errorLabel.setText("Подключение...");

        try {
            // Создаём новый клиент при каждой попытке (на случай ошибок предыдущей)
            if (client != null && client.isOpen()) {
                client.close();
            }

            client = new ChatClient(new URI("ws://localhost:8887"));

            client.setOnMessageReceived(msg -> {
                Platform.runLater(() -> {
                    if (msg.getType() == MessageType.AUTH_SUCCESS) {
                        // content содержит список комнат через запятую
                        List<String> rooms = Arrays.asList(msg.getContent().split(","));
                        openChatWindow(login, rooms);
                    } else if (msg.getType() == MessageType.AUTH_ERROR) {
                        errorLabel.setText(msg.getContent());
                        // Закрываем соединение чтобы при следующей попытке создать новое
                        client.close();
                        client = null;
                    }
                });
            });

            client.connectBlocking();

            Message authMsg = new Message(type, login, pass);
            client.send(gson.toJson(authMsg));

        } catch (Exception e) {
            Platform.runLater(() -> errorLabel.setText("Нет связи с сервером"));
            e.printStackTrace();
        }
    }

    private void openChatWindow(String username, List<String> rooms) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat.fxml"));
            Parent root = loader.load();

            ChatController chatCtrl = loader.getController();
            chatCtrl.setNickname(username);
            chatCtrl.setRooms(rooms);
            chatCtrl.setClient(this.client);

            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("Чат — " + username);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Ошибка открытия окна чата");
        }
    }
}