package main;

import configs.GlobalConfigs;
import database_service.DbService;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegram_services.GroupChatBot;
import telegram_services.KickTimer;
import telegram_services.WebhookService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dfyz on 28.06.2017.
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        DbService dbService = DbService.getInstance();
        System.out.println("DbService запущен");
        ApiContextInitializer.init();
        TelegramLongPollingBot groupChatBot = new GroupChatBot();
        WebhookService webhookService = new WebhookService(groupChatBot);
        KickTimer kickTimer = new KickTimer(groupChatBot);

        //LocalDateTime dateTime = LocalDateTime.now();
       // timer.schedule();
        //taimer.start();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(GlobalConfigs.pathToCertificateStore,GlobalConfigs.certificateStorePassword,GlobalConfigs.EXTERNALWEBHOOKURL,GlobalConfigs.INTERNALWEBHOOKURL,GlobalConfigs.pathToCertificatePublicKey);
            telegramBotsApi.registerBot(webhookService);
            telegramBotsApi.registerBot(groupChatBot);
            System.out.println("TelegramService запущен");
            kickTimer.start();
            System.out.println("KickTimer запущен");
            log.info("*********Bot started********");
        } catch (TelegramApiRequestException e) {
            System.out.println("Не смог создать telegramBotsApi");
            e.printStackTrace();
        }
    }
}
