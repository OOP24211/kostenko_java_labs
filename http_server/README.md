# HTTP Server

## Описание
Ручная реализация HTTP-сервера и клиента на Java без использования сторонних фреймворков.

## Структура проекта
    http_server/

    ├── pom.xml # Maven конфигурация

    └── src/main/java/

        ├── client/
        │   └── HttpClient.java # HTTP клиент
        └── server/
            ├── HttpServer.java # Сервер
            ├── HttpParser.java # Парсер HTTP
            ├── HttpRequest.java # Модель запроса
            └── HttpResponse.java # Модель ответа


## Запуск

### Сервер
```bash
mvn exec:java -Dexec.mainClass="server.HttpServer"
```
### Клиент
```bash
mvn exec:java -Dexec.mainClass="client.HttpClient"
```

## Тестирование через curl
```bash
# GET запросы
curl http://localhost:8080/
curl http://localhost:8080/api/hello

# POST запросы
curl -X POST http://localhost:8080/api/data \
  -H "Content-Type: application/json" \
  -d "{\"test\":\"hello\"}"

curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"john\",\"email\":\"john@example.com\"}"
```