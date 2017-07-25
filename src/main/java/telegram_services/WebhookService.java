package telegram_services;

import database_service.DbService;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by kuteynikov on 14.07.2017.
 */
public class WebhookService extends TelegramWebhookBot implements TelegramService {
    private MessageHandler messageHedler;
    private DbService dbService;
    private int count=0;

    public WebhookService(DbService dbService) {
        this.dbService = dbService;
        this.messageHedler = new MessageHandler(dbService);
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        //++count;
        //System.out.println("reciv update"+count);
        if (update.hasCallbackQuery()) {
            System.out.println("пришел CallbackQuery: " + update.getCallbackQuery());
            EditMessageText editMessageText = messageHedler.callBackContext(update.getCallbackQuery());
            return editMessageText;
        } else if (update.hasMessage()&update.getMessage().hasText()){
            long userId = update.getMessage().getChat().getId();
            SendMessage sendMessage= new SendMessage(update.getMessage().getChatId(),"Ошибка 101");
            if (!dbService.dbHasUser(userId)){
                sendMessage = messageHedler.startContext(update.getMessage());
            } else {
                sendMessage = messageHedler.mainContext(update.getMessage());
            }
            return sendMessage;
        } else {
            return null;
        }
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
