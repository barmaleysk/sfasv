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
        this.messageHedler = new MessageHedler(dbService,this);
        this.dbService = dbService;
    }


    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        SendMessage sendMessage=null;
        if (update.hasMessage()&update.getMessage().hasText()){
            long userId = update.getMessage().getChat().getId();
            if (dbService.checkUserinDb(userId)){
                sendMessage = messageHedler.startContext(update.getMessage());
            } else {

            }
        }
        return sendMessage;
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    @Override
    public void messageSend(SendMessage message) {
        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            System.out.println("Не смог отправить сообщение в чат ID= "+message.getChatId());
            e.printStackTrace();
        }
    }
}
