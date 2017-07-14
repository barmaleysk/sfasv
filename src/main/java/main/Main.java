package main;

import database_service.DbService;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegram_services.MessageHedler;
import telegram_services.WebhookService;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * Created by Dfyz on 28.06.2017.
 */
public class Main {
    private static final int PORT = 443;
    private static final String EXTERNALWEBHOOKURL = "https://31.148.99.14:" + PORT; // https://(xyz.)externaldomain.tld
    private static final String INTERNALWEBHOOKURL = "https://31.148.99.14:" + PORT; // https://(xyz.)localip/domain(.tld)
    private static final String pathToCertificatePublicKey = "./public_cert.pem"; //only for self-signed webhooks
    private static final String pathToCertificateStore = "./keystore.jks"; //self-signed and non-self-signed.
    private static final String certificateStorePassword = "megapokemon"; //password for your certificate-store

    public static void main(String[] args){
        System.out.println(LocalDateTime.now()+ " "+TimeZone.getDefault() );
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
        DbService dbService = new DbService();
        System.out.println("DbService запущен");
        ApiContextInitializer.init();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(pathToCertificateStore,certificateStorePassword,EXTERNALWEBHOOKURL,INTERNALWEBHOOKURL,pathToCertificatePublicKey);
            telegramBotsApi.registerBot(new WebhookService(dbService));
            System.out.println("TelegramService запущен");
        } catch (TelegramApiRequestException e) {
            System.out.println("Не смог создать telegramBotsApi для Webhook");
            e.printStackTrace();
        }
    }
}
