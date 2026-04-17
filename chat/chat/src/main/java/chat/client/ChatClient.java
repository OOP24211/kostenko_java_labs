package chat.client;

import com.google.gson.Gson;
import chat.model.Message;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.function.Consumer;

public class ChatClient extends WebSocketClient {
    private final Gson gson = new Gson();

    // Это "ссылка" на метод в твоем контроллере, который отрисует сообщение
    private Consumer<Message> onMessageReceived;

    public ChatClient(URI serverUri) {
        super(serverUri);
    }

    // Метод, чтобы привязать контроллер к клиенту
    public void setOnMessageReceived(Consumer<Message> callback) {
        this.onMessageReceived = callback;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Подключились к серверу!");
    }

    @Override
    public void onMessage(String messageJson) {
        Message msg = gson.fromJson(messageJson, Message.class);

        // Если кто-то "подписался" на получение сообщений, отправляем ему
        if (onMessageReceived != null) {
            onMessageReceived.accept(msg);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Отключились от сервера");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}