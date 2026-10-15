# Лабораторна робота №7: Шаблонізація у Spring MVC з використанням FreeMarker та формування HTML-листів (Gmail SMTP)

## 📌 Мета роботи
Ознайомитися з принципами серверної шаблонізації у Spring MVC на основі шаблонізатора FreeMarker. Навчитися створювати динамічні HTML-шаблони для веб-сторінок та e-mail повідомлень, використовувати змінні, колекції, умовні конструкції та підключати зовнішні ресурси. Освоїти інтеграцію Spring Boot із SMTP Gmail, створення HTML-листів через FreeMarker та надсилання їх через сервіс `JavaMailSender`.

---

## 🛠 Технології та стек
* **Java 21**
* **Spring Boot / Spring MVC 3.3.4**
* **FreeMarker** (серверний шаблонізатор для листів)
* **Thymeleaf** (шаблонізатор для веб-інтерфейсу)
* **Spring Mail (`JavaMailSender` & `jakarta.mail`)**
* **Gmail SMTP (App Password)**
* **Maven** (багатомодульний проєкт: `core`, `persistence`, `web`)

---

## 📂 Структура проєкту
Архітектура побудована за принципами чистої архітектури та поділена на три Maven-модулі. Зміни цієї лабораторної роботи зосереджені у модулі `web`:

| Модуль | Призначення | Зміни в ЛР №7 |
| :--- | :--- | :--- |
| **`core`** | Бізнес-логіка, доменні моделі (`Book`), порти та сервіси. | *Без змін* |
| **`persistence`** | JDBC-реалізація репозиторіїв та робота з БД. | *Без змін* |
| **`web`** | Контролери, конфігурація та веб-інтерфейс. | Додано FreeMarker, SMTP Gmail, `EmailFreemarkerConfig`, `EmailTemplateProcessor`, `MailService` та шаблон листа `new_book.ftl`. |

---

## 🚀 Виконані завдання та розширення (+20%)

1. **Підготовка середовища та конфігурація SMTP:**
    * Додано залежності `spring-boot-starter-freemarker` та `spring-boot-starter-mail` у файл `web/pom.xml`.
    * Налаштовано підключення до Gmail SMTP через `App Password` у `application.properties`:
      ```properties
      spring.mail.host=smtp.gmail.com
      spring.mail.port=587
      spring.mail.username=${MAIL_USERNAME}
      spring.mail.password=${MAIL_PASSWORD}
      spring.mail.properties.mail.smtp.auth=true
      spring.mail.properties.mail.smtp.starttls.enable=true
      spring.mail.default-encoding=UTF-8
      ```

2. **Конфігурація FreeMarker для листів:**
    * Створено клас **`EmailFreemarkerConfig`**, який налаштовує окремий конфіг завантаження шаблонів виключно з каталогу `classpath:/mail-templates/`, ізолюючи їх від Thymeleaf.

3. **Створення HTML-шаблону листа (`new_book.ftl`):**
    * Розроблено стилізований HTML-шаблон листа із застосуванням inline-CSS.
    * Використано динамічні змінні (`${title}`, `${author}`, `${year}`, `${added}`).
    * **Розширення (+20%):**
        * Інтегровано логотип застосунку через raw-URL з GitHub (`<img src="https://raw.githubusercontent.com/.../logo.png">`).
        * Додано кастомний футер із поточним роком (`BookApp © 2026`).
        * Реалізовано умовний блок для виділення раритетних видань (`<#if year < 2000>`).
        * Налаштовано форматування дати додавання (`yyyy-MM-dd HH:mm`).

4. **Сервісна логіка розсилки:**
    * Створено **`EmailTemplateProcessor`** для рендерингу шаблонів FreeMarker у стрічку HTML.
    * Реалізовано **`MailService`**, який формує `MimeMessage` та безпечно надсилає лист через `JavaMailSender` (із блоком `try-catch`, щоб збій поштового сервера не переривав процес збереження книги).

5. **Інтеграція у контролер:**
    * Оновлено метод POST-запиту в `BooksController`, який після успішного збереження нової книги викликає `mailService.sendNewBookEmail(book)`.

---

## 🖥 Перевірка роботи
1. **Перегляд каталогу:** `http://localhost:8080/books`
2. **Додавання нової книги (з тригером відправки листа):** `http://localhost:8080/books/new`
3. **Перевірка пошти:** У поштовій скриньці (Gmail) з'являється стилізований HTML-лист про успішне додавання книги.