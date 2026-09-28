package app.web;

import app.core.domain.Book;
import app.core.domain.PageRequest;
import app.core.port.CatalogRepositoryPort;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class BooksServlet extends HttpServlet {

    private final CatalogRepositoryPort bookRepo;

    public BooksServlet(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println(">>> BooksServlet.doGet() CALLED");

        try {
            String query = req.getParameter("q");

            PageRequest pageRequest = new PageRequest(0, 20);

            List<Book> books = bookRepo
                    .search(query, pageRequest)
                    .getItems();

            req.setAttribute("books", books);

            req.getRequestDispatcher(
                    "/WEB-INF/views/books.jsp"
            ).forward(req, resp);

        } catch (Exception e) {
            throw new ServletException(
                    "Cannot load books",
                    e
            );
        }
    }
}