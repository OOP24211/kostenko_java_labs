package chat.model;

public class Message {
    private MessageType type;
    private String sender;   // Кто отправил (логин)
    private String receiver; // Кому/В какую комнату
    private String content;  // Текст сообщения / Пароль / Статус

    // Для файлов
    private String fileName;
    private byte[] fileData;

    // Конструктор для текста/авторизации
    public Message(MessageType type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    // Конструктор для файлов
    public Message(MessageType type, String sender, String fileName, byte[] fileData) {
        this.type = type;
        this.sender = sender;
        this.fileName = fileName;
        this.fileData = fileData;
    }

    // Геттеры (нужны GSON-у и тебе для логики)
    public MessageType getType() { return type; }
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public String getReceiver() { return receiver; }
    public String getFileName() { return fileName; }
    public byte[] getFileData() { return fileData; }

    // Сеттер для receiver (чтобы указывать комнату перед отправкой)
    public void setReceiver(String receiver) { this.receiver = receiver; }
}