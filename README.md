# Лабораторна робота №4 - Spring Core & Boot

Цей проєкт є багатомодульним вебзастосунком для ведення каталогу книг (Guestbook), переведеним на використання фреймворку **Spring Boot**. Основна мета поточної версії — практична реалізація принципів **Inversion of Control (IoC)** та **Dependency Injection (DI)**, а також налаштування автоконфігурації Spring.

## 🏗 Структура проєкту

Проєкт побудовано за принципами чистої архітектури і розділено на три незалежні Maven-модулі:

*   **`core`** — доменний шар застосунку. Містить бізнес-моделі (`Book`, `Comment`), інтерфейси портів (`CatalogRepositoryPort`) та бізнес-сервіси (`BookService`). Цей модуль повністю ізольований і не має залежностей від Spring фреймворку.
*   **`persistence`** — шар доступу до даних. Містить реалізацію репозиторіїв (`JdbcBookRepository`, `JdbcCommentRepository`) за допомогою технології JDBC та логіку ініціалізації бази даних.
*   **`web`** — шар представлення та конфігурації. Містить залежності Spring Boot, головний клас `AppInit` із анотацією `@SpringBootApplication`, конфігурацію бінів (`ServletConfig`), сервлети (`BooksApiServlet`, `BooksServlet`) та сервіси (`AppStatsService`).

## 🚀 Основні реалізовані можливості (Spring Features)

1.  **Автоконфігурація Spring Boot:** Застосунок піднімається як Standalone-застосунок із вбудованим сервером **Tomcat** на порті 8080.
2.  **Inversion of Control (IoC):** Усі ключові об'єкти (репозиторії, сервіси, сервлети) керуються Spring Контекстом.
3.  **Dependency Injection (DI):** 
    *   **Constructor Injection:** Основний спосіб передачі залежностей у бізнес-сервіси (`BookService`) та сервлети.
    *   **Field Injection:** Реалізовано за допомогою анотації `@Autowired` для ін'єкції залежностей безпосередньо в поля класу (продемонстровано в `AppStatsService`).
4.  **Кастомна конфігурація бінів:** Реєстрація сторонніх класів та репозиторіїв реалізована через клас `ServletConfig` за допомогою анотацій `@Configuration` та `@Bean`.
5.  **Externalized Configuration:** Власні налаштування застосунку (наприклад, `app.catalog.default-page-size`, `app.catalog.welcome-banner`) винесено у файл `application.properties` та зчитуються через анотацію `@Value`.

## 🛠 Стек технологій

*   **Мова:** Java 17 / 21
*   **Фреймворк:** Spring Boot 3.3.4 (Spring Web)
*   **Збірка:** Apache Maven
*   **Сервер:** Embedded Apache Tomcat 10
*   **Представлення:** Jakarta Servlets, JSP, JSTL
*   **Формат обміну даними:** JSON (Jackson)

## ⚙️ Запуск застосунку

### Спосіб 1: Через IDE (IntelliJ IDEA)
1. Відкрийте проєкт у вашій IDE.
2. Знайдіть файл `AppInit.java` за шляхом `web/src/main/java/app/config/AppInit.java`.
3. Запустіть метод `main()` за допомогою зеленої стрілки **Run**.

### Спосіб 2: Через термінал (Maven)
Виконайте збірку проєкту в кореневій директорії, після чого запустіть модуль `web` за допомогою Spring Boot плагіна:

```powershell
mvn clean install
cd web
mvn spring-boot:run
```

## 📡 Доступні ендпоінти
Після успішного запуску, сервер буде доступний за адресою `http://localhost:8080/`.
* **Каталог книг (UI):** `GET /books` — відкриває JSP-сторінку з каталогом книг.
* **REST API для книг:** `GET /api/books/` — повертає список книг у форматі JSON. Підтримує параметри пагінації (?page=0&size=10).
* **Додавання книги (API):** `POST /api/books/` — приймає JSON із даними нової книги (title, author, pubYear) та зберігає її в базу даних.
