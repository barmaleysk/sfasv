package telegram_services;

import database_service.DbService;
import entitys.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuteynikov on 29.06.2017.
 */
public class TelegramService extends TelegramLongPollingBot {
    ReplyKeyboardMarkup mainKeyboard;

    private DbService dbService;

    public TelegramService(DbService dbService) {
        this.dbService = dbService;
        createMainMenu();
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getText());
        if (update.hasMessage()&&update.getMessage().isCommand()){
            Message updateMessage = update.getMessage();
            String command = updateMessage.getText();
            switch (command){
                case "/start":
                    start(updateMessage);
                    break;
                default:
                    senFailCommand(updateMessage);
                    break;
            }
        } else if (update.hasMessage()&&update.getMessage().hasText()){
            String s = update.getMessage().getText();
            switch (s){
                case "Оформить подписку" :
                    sendPayMenu(update.getMessage());
                    break;
                default:
                    senFailCommand(update.getMessage());
            }
        }

    }

    private void sendPayMenu(Message message) {
        SendMessage sendMessage = new SendMessage() // Create a message object object
                .setChatId(message.getChatId())
                .setText("Выберите подписку:");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText("1 месяц").setCallbackData("1month"));
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(new InlineKeyboardButton().setText("2 месяца").setCallbackData("2month"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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

    private void start(Message updateMessage) {
        String firstName = updateMessage.getChat().getFirstName();
        String lastName = updateMessage.getChat().getLastName();
        String userName = updateMessage.getChat().getUserName();
        long userID = updateMessage.getChat().getId();
        long chatID = updateMessage.getChatId();
        String textMessage = "Привет, "+firstName;

        User user = dbService.getUserFromDb(userID);
        if (user!=null){
           user.setChatID(chatID);
           dbService.addUserInDb(user);
           textMessage = textMessage + "! ваша подписка истекает: " +user.getEndDate();
        } else {
            user = new User(userID);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUserName(userName);
            user.setEndDate("29.07.2017");
            dbService.addUserInDb(user);
            System.out.println("В БД добавлен новый пользователь:\n"+user);
            textMessage = textMessage + "\n Вам необходимо оплатить подписку!";
        }
        SendMessage sendMessage = new SendMessage(updateMessage.getChatId(),textMessage);

        sendMessage.setReplyMarkup(mainKeyboard);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void createMainMenu(){
        mainKeyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Оформить подписку"));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton("Информация о боте"));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        mainKeyboard.setKeyboard(keyboardRows);
        mainKeyboard.setOneTimeKeyboard(true);
        mainKeyboard.setResizeKeyboard(true);
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
