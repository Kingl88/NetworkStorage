package entity;
//Список команд передаваемых в сообщениях для общения сервера и клиента
public enum Command {
    CHANNEL_HAS_BEEN_ACTIVATED,
    DOWNLOADING_FROM_SERVER,
    DOWNLOADING_FROM_CLIENT,
    AUTHORIZATION_CONFIRMED,
    FILE_WAS_SENT;
}
