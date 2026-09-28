<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>BookShelf — Каталог книг</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>

<header class="header">
    <div class="container header-inner">
        <a class="logo" href="${pageContext.request.contextPath}/">
            📚 BookShelf
        </a>

        <nav class="nav">
            <a href="${pageContext.request.contextPath}/books"
               class="nav-link active">
                Каталог
            </a>
        </nav>
    </div>
</header>

<main class="container">

    <section class="hero">
        <div>
            <p class="eyebrow">YOUR PERSONAL LIBRARY</p>
            <h1>Каталог книг</h1>
            <p class="hero-text">
                Знаходь улюблені книги, додавай нові та залишай свої коментарі.
            </p>
        </div>
    </section>

    <section class="toolbar">

        <form method="get"
              action="${pageContext.request.contextPath}/books"
              class="search-form">

            <input
                    type="text"
                    name="q"
                    value="<c:out value='${param.q}'/>"
                    placeholder="Пошук за назвою або автором..."
                    class="input">

            <button type="submit" class="button button-primary">
                🔎 Знайти
            </button>

        </form>

    </section>

    <section class="section">

        <div class="section-header">
            <div>
                <h2>Книги</h2>
                <p class="muted">
                    Обери книгу, щоб переглянути її коментарі.
                </p>
            </div>

            <span class="count">
                ${books.size()} книг
            </span>
        </div>

        <c:choose>

            <c:when test="${empty books}">
                <div class="empty-state">
                    <div class="empty-icon">📖</div>
                    <h3>Книг поки немає</h3>
                    <p>
                        Додай першу книгу до свого каталогу.
                    </p>
                </div>
            </c:when>

            <c:otherwise>

                <div class="book-grid">

                    <c:forEach var="book" items="${books}">

                        <article class="book-card">

                            <div class="book-cover">
                                📚
                            </div>

                            <div class="book-content">

                                <h3 class="book-title">
                                    <c:out value="${book.title}"/>
                                </h3>

                                <p class="book-author">
                                    <c:out value="${book.author}"/>
                                </p>

                                <p class="book-year">
                                    ${book.pubYear}
                                </p>

                                <a
                                    href="${pageContext.request.contextPath}/comments?bookId=${book.id}"
                                    class="button button-secondary button-full">
                                    💬 Коментарі
                                </a>

                            </div>

                        </article>

                    </c:forEach>

                </div>

            </c:otherwise>

        </c:choose>

    </section>

</main>

<footer class="footer">
    <div class="container">
        <p>BookShelf · Guestbook Application</p>
    </div>
</footer>

</body>
</html>