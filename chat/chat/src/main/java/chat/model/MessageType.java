package chat.model;

public enum MessageType {
    AUTH_REQUEST,   // Запрос на вход (логин + пароль)
    REG_REQUEST,    // Запрос на регистрацию
    AUTH_SUCCESS,   // Ответ: вход разрешен
    AUTH_ERROR,     // Ответ: ошибка (неверный пароль или логин занят)
    TEXT,           // Обычное сообщение
    FILE,           // Передача файла (с байтами)
    JOIN_ROOM,      // Запрос на вход в комнату
    LEAVE_ROOM,     // Выход из комнаты
    CREATE_ROOM,
    UPDATE_ROOMS
}