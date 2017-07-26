package main;

import database_service.DbService;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegram_services.WebhookService;

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

    public static void main(String[] args) throws Exception {
        DbService dbService = DbService.getInstance();
        System.out.println("DbService запущен");
        ApiContextInitializer.init();
        WebhookService webhookService = new WebhookService(dbService);
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(pathToCertificateStore,certificateStorePassword,EXTERNALWEBHOOKURL,INTERNALWEBHOOKURL,pathToCertificatePublicKey);
            telegramBotsApi.registerBot(webhookService);
            System.out.println("TelegramService запущен");
        } catch (TelegramApiRequestException e) {
            System.out.println("Не смог создать telegramBotsApi для Webhook");
            e.printStackTrace();
        }
       /* TestServlet testServlet = new TestServlet(webhookService);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(testServlet),"/*");
        Server server = new Server(8080);
        server.setHandler(context);
        server.start();
        System.out.println("Jetty started");
        server.join();
        */
    }
}
