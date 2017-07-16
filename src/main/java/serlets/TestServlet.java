package serlets;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import telegram_services.WebhookService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Dfyz on 16.07.2017.
 */
public class TestServlet extends HttpServlet {
    private WebhookService webhookService;

    public TestServlet(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print("Hello");
        SendMessage sendMessage = new SendMessage().setChatId(245480645l).setText("jetty is work!!!");
        try {
            System.out.println("попытка отправить....");
            webhookService.sendMessage(sendMessage);
            System.out.println("отправил....");
        } catch (TelegramApiException e) {
            System.out.println("хер, неичего не отправлю");
            e.printStackTrace();
        }
    }
}
