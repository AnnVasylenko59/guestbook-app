# Guestbook Application — Lab 5 (Spring MVC)

Лабораторна робота №5 з дисципліни "Сучасні фреймворки програмування". 
Мета роботи: міграція веб-застосунку з класичних сервлетів на архітектуру **Spring MVC** із використанням `DispatcherServlet`, контролерів (`@Controller`, `@RestController`) та механізму маршрутизації.

## 🛠 Технології та стек
* **Java 21**
* **Spring Boot 3.3.4** (Spring MVC, Tomcat)
* **JDBC / H2 Database**
* **JSP / JSTL** (для веб-інтерфейсу)
* **Jackson** (для серіалізації JSON)

## 📂 Структура проєкту
Проєкт зберігає багатомодульну архітектуру:
* **`core`** — бізнес-логіка, моделі (`Book`, `Comment`), сервіси (`BookService`, `CommentService`) та порти.
* **`persistence`** — робота з базою даних через JDBC (`JdbcBookRepository`, `JdbcCommentRepository`).
* **`web`** — контролери Spring MVC, конфігурація (`WebConfig`, `AppConfig`), JSP-сторінки та статичні файли (`/css`).

## 🚀 Основні ендпоінти

### Веб-інтерфейс (JSP)
* `GET /books` — перегляд каталогу книг та відгуків через графічний інтерфейс.

### REST API (JSON)
* `GET /api/books` — отримати список усіх книг у форматі JSON.
* `GET /api/books/{id}` — отримати інформацію про конкретну книгу за ID.
* `POST /api/comments` — додати новий відгук (приймає JSON-об'єкт із полями `bookId`, `author`, `text`, повертає статус `201 Created`).

## ⚙️ Конфігурація
* Активовано анотаційний режим MVC за допомогою анотації `@EnableWebMvc` у класі `WebConfig`.
* Налаштовано `ViewResolver` для пошуку JSP-файлів у `/WEB-INF/views/`.
* Налаштовано роздачу статичних ресурсів (`ResourceHandlerRegistry`).

## 💻 Як запустити проєкт
1. Клонувати репозиторій та переключитись на гілку `lab-5`:
   ```bash
   git checkout lab-5
   ```
1. Відкрити проєкт в IntelliJ IDEA.
2. У конфігурації запуску (AppInit.java) переконатися, що Working directory вказана правильно (папка web).
3. Запустити головний клас AppInit.
4. Перевірити роботу в браузері за адресою: http://localhost:8080/books або протестувати API через http://localhost:8080/api/books.
