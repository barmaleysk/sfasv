package telegram_services;

import database_service.DbService;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import telegram_services.WebhookService;

import java.util.List;
import java.util.TimerTask;

public class MyTimer extends TimerTask {
    private WebhookService webhookService;
    public MyTimer(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run() {
        List<Long> usersId = DbService.getInstance().getUnSubscriptionUsers();
        KickChatMember kickMainGroupMember = new KickChatMember();
    }
}

