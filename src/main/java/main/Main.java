package main;

import database_service.DbService;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegram_services.TelegramService;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * Created by Dfyz on 28.06.2017.
 */
public class Main {
    public static void main(String[] args){
        System.out.println(LocalDateTime.now()+ " "+TimeZone.getDefault() );
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
        DbService dbService = new DbService();
        System.out.println("DbService запущен");
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        TelegramService telegramService = new TelegramService(dbService);
        try {
            telegramBotsApi.registerBot(telegramService);
            System.out.println("TelegramService запущен");
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
