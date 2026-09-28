# Лабораторна робота №3: Мікрофреймворк Javalin

Переведення REST API вебмодуля з Jakarta Servlet API на мікрофреймворк Javalin.

## Що зроблено
* Налаштовано запуск застосунку через клас `app.web.JavalinBookApp` із вбудованим сервером Jetty[cite: 13].
* Реалізовано REST-маршрути в `BooksController` через об'єкт `Context`[cite: 13]:
  * `GET /api/books` — список книг[cite: 13]
  * `GET /api/books/{id}` — книга за ID[cite: 13]
  * `POST /api/books` — додавання книги
  * `GET /api/comments?bookId={id}` — коментарі до книги[cite: 13]
  * `POST /api/comments` — додавання коментаря[cite: 13]
  * `DELETE /api/books/{bookId}/comments/{id}` — видалення коментаря
* Додано middleware для логування запитів (`app.before()`)[cite: 13].
* Налаштовано централізовану обробку помилок (`app.exception()`)[cite: 13].
* Підключено `JavaTimeModule` для коректної JSON-серіалізації дат коментарів.

## Запуск

1. Скомпілюйте проєкт:
```powershell
mvn clean compile
```

2. Запустіть сервіс:
```powershell
mvn exec:java -pl web
```

Сервер доступний за адресою: `http://localhost:8080/`.
