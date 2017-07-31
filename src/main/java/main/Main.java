package main;

import configs.GlobalConfigs;
import database_service.DbService;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegram_services.WebhookService;

/**
 * Created by Dfyz on 28.06.2017.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        DbService dbService = DbService.getInstance();
        System.out.println("DbService запущен");
        ApiContextInitializer.init();
        WebhookService webhookService = new WebhookService(dbService);
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(GlobalConfigs.pathToCertificateStore,GlobalConfigs.certificateStorePassword,GlobalConfigs.EXTERNALWEBHOOKURL,GlobalConfigs.INTERNALWEBHOOKURL,GlobalConfigs.pathToCertificatePublicKey);
            telegramBotsApi.registerBot(webhookService);
            System.out.println("TelegramService запущен");
        } catch (TelegramApiRequestException e) {
            System.out.println("Не смог создать telegramBotsApi для Webhook");
            e.printStackTrace();
        }
    }
}
