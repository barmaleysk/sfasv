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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuteynikov on 29.06.2017.
 */
public class TelegramService extends TelegramLongPollingBot {
    ReplyKeyboardMarkup mainKeyboardMarkup;
    ReplyKeyboardMarkup subscripMenuMarkup;
    ReplyKeyboardMarkup infoMenuMarkup;

    private DbService dbService;

    public TelegramService(DbService dbService) {
        this.dbService = dbService;
        createMainKeyboardMarkup();
        createSubscripMenuMarkup();
        createInfoMenuMarkup();
    }





    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getText());
        //проверим пользователя: 1-впервые зашёл, 2-кончилась подписка, 3-есть подписка
        long userID = update.getMessage().getChat().getId();
        User userFromDb = dbService.getUserFromDb(userID);
        if (userFromDb==null){
            startContext(update);
        } else {
            withSubscriptionContext(update);
        }
        /*else if (userFromDb.getEndDate()==null && userFromDb.getEndDate().isAfter(LocalDate.now())){
            mainContext(update);
        }else if (userFromDb.getEndDate()!=null && userFromDb.getEndDate().isBefore(LocalDate.now())){
            withSubscriptionContext(update);
        } */


       /* if (update.hasMessage()&&update.getMessage().isCommand()){
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
        } */

    }



    private void mainContext(Update update) {

    }

    private void withSubscriptionContext(Update update) {
        if (update.hasMessage()&&update.getMessage().hasText()){
            String texOfMessage = update.getMessage().getText();
            SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());
            switch (texOfMessage){
                case "/start":
                    message.setText("v");
                    message.setReplyMarkup(mainKeyboardMarkup);
                    break;
                case "Оформить подписку" :
                    message.setText("v");
                    message.setReplyMarkup(subscripMenuMarkup);
                    break;
                case "Информация о боте" :
                    message.setText("v");
                    message.setReplyMarkup(infoMenuMarkup);
                    break;
                case "Общее описание" :
                    message.setText("Здесь будет общее описание, что это, как это, ссылки на ютуб....");
                    break;
                case "FAQ":
                    message.setText("Здесь будет общее описание");
                    break;
                case "Как обменять криптовалюту":
                    message.setText("фигась фигась и поменяли");
                    break;
                case "Тех поддержк":
                    message.setText("Меню для связи с тех поддержкой");
                    break;
                case "Вернутся в главное меню":
                    message.setText("v");
                    message.setReplyMarkup(mainKeyboardMarkup);
                    break;
                case "1 месяц = 100р":
                    message.setText("Сообщение с кнопкой оплаты");
                    break;
                case "2 месяца = 180р":
                    message.setText("Сообщение с кнопкой оплаты");
                    break;
                case "3 месяца = 240р":
                    message.setText("Сообщение с кнопкой оплаты");
                    break;
                case "Проверить подписку":
                    message.setText("Ваша подписка истекает...");
                    break;
                default:
                    message.setText("Я пока не знаю что на это отвеить");
            }
            try {
                sendMessage(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void startContext(Update update) {
        long userID = update.getMessage().getChat().getId();
        String userName = update.getMessage().getChat().getUserName();
        String firstName = update.getMessage().getChat().getFirstName();
        String lastName = update.getMessage().getChat().getLastName();
        long chatID = update.getMessage().getChatId();
        User newUser = new User(userID,userName,firstName,lastName,chatID);
        dbService.addUserInDb(newUser);
        System.out.println("в базу добавлен пользователь: "+newUser);
        SendMessage message = new SendMessage().setChatId(chatID);
        if (update.hasMessage()&&update.getMessage().getText().equals("/start")){
            message.setText("Добро пожаловать, "+firstName+"!"
                    +"\n Это платный бот и должен оформить подписку.....бла бла бла бла" +
                    "\n бла бал бла бла бла");
            message.setReplyMarkup(mainKeyboardMarkup);
        } else {
            message.setText("Отправь  /start");
        }
        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void createMainKeyboardMarkup(){
        mainKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Оформить подписку"));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton("Информация о боте"));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        mainKeyboardMarkup.setKeyboard(keyboardRows);
       // mainKeyboardMarkup.setOneTimeKeyboard(true);
        mainKeyboardMarkup.setResizeKeyboard(true);
    }

    private void createSubscripMenuMarkup() {
        subscripMenuMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("1 месяц = 100р"));
        keyboardRow1.add(new KeyboardButton("2 месяца = 180р"));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton("3 месяца = 240р"));
        keyboardRow2.add(new KeyboardButton("Проверить подписку"));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton("Вернутся в главное меню"));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        subscripMenuMarkup.setKeyboard(keyboardRows);
        //subscripMenuMarkup.setOneTimeKeyboard(true);
        subscripMenuMarkup.setResizeKeyboard(true);
    }

    private void createInfoMenuMarkup(){
        infoMenuMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Общее описание"));
        keyboardRow1.add(new KeyboardButton("FAQ"));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton("Как обменять криптовалюту"));
        keyboardRow2.add(new KeyboardButton("Тех поддержка"));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton("Вернутся в главное меню"));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        infoMenuMarkup.setKeyboard(keyboardRows);
        //infoMenuMarkup.setOneTimeKeyboard(true);
        infoMenuMarkup.setResizeKeyboard(true);
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

       /* private void sendPayMenu(Message message) {
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

    } */

  /*  private void start(Message updateMessage) {
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

        sendMessage.setReplyMarkup(mainKeyboardMarkup);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    } */
}
