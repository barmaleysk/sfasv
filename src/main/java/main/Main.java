package main;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegram_services.TelegramService;

/**
 * Created by Dfyz on 28.06.2017.
 */
public class Main {
    public static void main(String[] args){
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TelegramService());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
