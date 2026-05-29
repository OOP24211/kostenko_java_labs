package chat.model;

public enum MessageType {
    AUTH_REQUEST,
    REG_REQUEST,
    AUTH_SUCCESS,
    AUTH_ERROR,
    TEXT,
    FILE,
    FILE_REQUEST,
    JOIN_ROOM,
    LEAVE_ROOM,
    CREATE_ROOM,
    UPDATE_ROOMS
}