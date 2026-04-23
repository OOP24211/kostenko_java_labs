package chat.model;

public class Message {
    private MessageType type;
    private String sender;   //кто отправил (логин)
    private String receiver; //кому/в какую комнату
    private String content;  //текст сообщения /пароль /статус

    private String fileName;
    private byte[] fileData;

    public Message(MessageType type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    //конструктор для файлов
    public Message(MessageType type, String sender, String fileName, byte[] fileData) {
        this.type = type;
        this.sender = sender;
        this.fileName = fileName;
        this.fileData = fileData;
    }

    public MessageType getType() { return type; }
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public String getReceiver() { return receiver; }
    public String getFileName() { return fileName; }
    public byte[] getFileData() { return fileData; }

    // сеттер для receiver
    public void setReceiver(String receiver) { this.receiver = receiver; }
}