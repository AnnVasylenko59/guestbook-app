<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>
        <c:out value="${book.title}"/> — BookShelf
    </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>

<header class="header">
    <div class="container header-inner">

        <a class="logo"
           href="${pageContext.request.contextPath}/">
            📚 BookShelf
        </a>

        <nav class="nav">
            <a class="nav-link"
               href="${pageContext.request.contextPath}/books">
                Каталог
            </a>
        </nav>

    </div>
</header>

<main class="container">

    <section class="book-hero">

        <a class="back-link"
           href="${pageContext.request.contextPath}/books">
            ← Назад до каталогу
        </a>

        <div class="book-detail">

            <div class="book-detail-cover">
                📚
            </div>

            <div class="book-detail-info">

                <p class="eyebrow">BOOK DETAILS</p>

                <h1>
                    <c:out value="${book.title}"/>
                </h1>

                <p class="detail-author">
                    <c:out value="${book.author}"/>
                </p>

                <span class="year-badge">
                    ${book.pubYear}
                </span>

            </div>

        </div>

    </section>


    <section class="comments-layout">

        <div class="comments-main">

            <div class="section-header">
                <div>
                    <h2>Коментарі</h2>
                    <p class="muted">
                        Що думають читачі про цю книгу?
                    </p>
                </div>

                <span class="count">
                    ${comments.size()} коментарів
                </span>
            </div>


            <c:choose>

                <c:when test="${empty comments}">

                    <div class="empty-state">
                        <div class="empty-icon">💬</div>

                        <h3>Коментарів ще немає</h3>

                        <p>
                            Будь першим, хто залишить свою думку.
                        </p>
                    </div>

                </c:when>

                <c:otherwise>

                    <div class="comments-list">

                        <c:forEach var="comment"
                                   items="${comments}">

                            <article class="comment-card">

                                <div class="comment-header">

                                    <div class="comment-avatar">
                                        <c:out value="${comment.author.substring(0, 1).toUpperCase()}"/>
                                    </div>

                                    <div>
                                        <strong>
                                            <c:out value="${comment.author}"/>
                                        </strong>

                                        <span class="comment-date">
                                            ${comment.createdAt}
                                        </span>
                                    </div>

                                </div>

                                <p class="comment-text">
                                    <c:out value="${comment.text}"/>
                                </p>

                                <form
                                    method="post"
                                    action="${pageContext.request.contextPath}/comments"
                                    class="delete-form">

                                    <input
                                        type="hidden"
                                        name="_method"
                                        value="delete">

                                    <input
                                        type="hidden"
                                        name="bookId"
                                        value="${book.id}">

                                    <input
                                        type="hidden"
                                        name="commentId"
                                        value="${comment.id}">

                                    <button
                                        type="submit"
                                        class="delete-button"
                                        onclick="return confirm('Видалити цей коментар?');">
                                        🗑 Видалити
                                    </button>

                                </form>

                            </article>

                        </c:forEach>

                    </div>

                </c:otherwise>

            </c:choose>

        </div>


        <aside class="comment-form-card">

            <h2>Залишити коментар</h2>

            <p class="muted">
                Поділися своєю думкою про книгу.
            </p>

            <form
                method="post"
                action="${pageContext.request.contextPath}/comments"
                class="comment-form">

                <input
                    type="hidden"
                    name="bookId"
                    value="${book.id}">

                <label for="author">
                    Ім'я
                </label>

                <input
                    id="author"
                    name="author"
                    type="text"
                    class="input"
                    maxlength="64"
                    placeholder="Твоє ім'я"
                    required>


                <label for="text">
                    Коментар
                </label>

                <textarea
                    id="text"
                    name="text"
                    class="textarea"
                    maxlength="1000"
                    rows="6"
                    placeholder="Напиши, що думаєш про цю книгу..."
                    required></textarea>


                <button
                    type="submit"
                    class="button button-primary button-full">
                    💬 Опублікувати
                </button>

            </form>

        </aside>

    </section>

</main>


<footer class="footer">
    <div class="container">
        <p>BookShelf · Guestbook Application</p>
    </div>
</footer>

</body>
</html>