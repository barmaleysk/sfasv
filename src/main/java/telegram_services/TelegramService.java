package telegram_services;

import database_service.DbService;
import entitys.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by kuteynikov on 29.06.2017.
 */
public class TelegramService extends TelegramLongPollingBot {

    private DbService dbService;

    public void setDbService(DbService dbService) {
        this.dbService = dbService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()&&update.getMessage().isCommand()){
            Message updateMessage = update.getMessage();
            String command = updateMessage.getText();
            switch (command){
                case "/start":
                    welcome(updateMessage);
                    break;
                default:
                    senFailCommand(updateMessage);
                    break;
            }
        }

    }

    private void senFailCommand(Message updateMessage) {
        SendMessage sendMessage = new SendMessage(updateMessage.getChatId(),"неизвестная команда");
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void welcome(Message updateMessage) {
        String firstName = updateMessage.getChat().getFirstName();
        String lastName = updateMessage.getChat().getLastName();
        String userName = updateMessage.getChat().getUserName();
        long userID = updateMessage.getChat().getId();

        System.out.println("Имя: "+firstName);
        System.out.println("Фамилия: "+lastName);
        System.out.println("username: " + userName);
        System.out.println("userID: " + userID);
        String textMessage = "Привет, "+firstName
                +"\n Имя: "+firstName
                +"\n Фамилия: "+lastName
                +"\n username: " + userName
                +"\n userID: " + userID;

        User user = new User(userID);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(userName);

        dbService.addUserInDb(user);

        SendMessage sendMessage = new SendMessage(updateMessage.getChatId(),textMessage);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
    public void onClosing() {

    }
}
