package telegram_services;

import database_service.DbService;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by kuteynikov on 14.07.2017.
 */
public class WebhookService extends TelegramWebhookBot implements TelegramService {
    private MessageHedler messageHedler;
    private DbService dbService;

    public WebhookService(DbService dbService) {
        this.dbService = dbService;
        this.messageHedler = new MessageHedler(dbService, this);
    }


    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        System.out.println("Update recived");
        SendMessage sendMessage=null;
        if (update.hasMessage()&update.getMessage().hasText()){
            long userId = update.getMessage().getChat().getId();
            if (!dbService.dbHasUser(userId)){
                sendMessage = messageHedler.startContext(update.getMessage());
            } else {
                sendMessage = messageHedler.mainContext(update.getMessage());
            }
        }
        return sendMessage;
    }

    @Override
    public String getBotUsername() {
        return "Sl0wP0ke_Bot";
    }

    @Override
    public String getBotToken() {
        return "443613733:AAFzuEjry6R_kMxZyB-pILvU4-YchwONs9M";
    }

    @Override
    public String getBotPath() {
        return "Sl0wP0ke_Bot";
    }

    @Override
    public void messageSend(SendMessage message) {
        try {
            System.out.println(" messageSend()");
            sendMessage(new SendMessage().setChatId(245480645l).setText("отладка messageSend"));
            sendMessage(message);
        } catch (TelegramApiException e) {
            System.out.println("Не смог отправить сообщение в чат ID= "+message.getChatId());
            e.printStackTrace();
        }
    }
}
