package telegram_services;

import database_service.DbService;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import telegram_services.WebhookService;

import java.util.List;

public class Taimer extends Thread {
    private WebhookService webhookService;
    public Taimer(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(10000l);
                System.out.println("тик так");
                List<Long> usersId = DbService.getInstance().getUnSubscriptionUsers();
                System.out.println(usersId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
