package main;

import configs.GlobalConfigs;
import database_service.DbService;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegram_services.MyTimer;
import telegram_services.WebhookService;

/**
 * Created by Dfyz on 28.06.2017.
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        DbService dbService = DbService.getInstance();
        System.out.println("DbService запущен");
        ApiContextInitializer.init();
        WebhookService webhookService = new WebhookService();
        MyTimer taimer = new MyTimer(webhookService);
        //taimer.start();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(GlobalConfigs.pathToCertificateStore,GlobalConfigs.certificateStorePassword,GlobalConfigs.EXTERNALWEBHOOKURL,GlobalConfigs.INTERNALWEBHOOKURL,GlobalConfigs.pathToCertificatePublicKey);
            telegramBotsApi.registerBot(webhookService);
            System.out.println("TelegramService запущен");
            log.info("*********Bot started********");
        } catch (TelegramApiRequestException e) {
            System.out.println("Не смог создать telegramBotsApi для Webhook");
            e.printStackTrace();
        }
    }
}
