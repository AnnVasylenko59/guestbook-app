package app.web;

import app.core.domain.Book;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JavaMailSender mailSender;
    private final EmailTemplateProcessor templateProcessor;
    private final String to;
    private final String from;

    public MailService(JavaMailSender mailSender,
                       EmailTemplateProcessor templateProcessor,
                       @Value("${app.mail.to}") String to,
                       @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.templateProcessor = templateProcessor;
        this.to = to;
        this.from = from;
    }

    public void sendNewBookEmail(Book book) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", book.getTitle());
        model.put("author", book.getAuthor());
        model.put("year", book.getPubYear());
        // FreeMarker не друкує java.time напряму, тому форматуємо дату тут
        model.put("added", LocalDateTime.now().format(DATE_FORMAT));

        String html = templateProcessor.process("new_book.ftl", model);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("Нова книга в каталозі");
            helper.setText(html, true);

            mailSender.send(message);
            log.info("E-mail про книгу '{}' успішно надіслано на {}", book.getTitle(), to);
        } catch (Exception e) {
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
