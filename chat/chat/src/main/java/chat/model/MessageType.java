package chat.model;

public enum MessageType {
    AUTH_REQUEST,   // запрос на вход (логин + пароль)
    REG_REQUEST,    // запрос на регистрацию
    AUTH_SUCCESS,   // ответ: вход разрешен
    AUTH_ERROR,     // ответ: ошибка (неверный пароль или логин занят)
    TEXT,           // обычное сообщение
    FILE,           // передача файла (с байтами)
    JOIN_ROOM,      // запрос на вход в комнату
    LEAVE_ROOM,
    CREATE_ROOM,
    UPDATE_ROOMS
}