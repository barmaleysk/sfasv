package telegram_services;

import database_service.DbService;
import entitys.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
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
        //System.out.println(update.getMessage().getText());
        //проверим пользователя в базе и выберем контекст
        if (update.hasMessage()&&update.getMessage().hasText()) {
            long userID = update.getMessage().getChat().getId();
            User userFromDb = dbService.getUserFromDb(userID);
            if (userFromDb == null) {
                startContext(update);
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                mainContext(update);
            }
        }else if (update.hasCallbackQuery()) {
            callBackContext(update.getCallbackQuery());
        }
    }

    private void callBackContext(CallbackQuery callbackQuery) {
        String dataFromQuery = callbackQuery.getData();
        switch (dataFromQuery){
            case "settrial":
                User userFromDb = dbService.getUserFromDb(callbackQuery.getMessage().getChat().getId());
                userFromDb.setEndDate(LocalDate.now().plusDays(2));
                dbService.addUserInDb(userFromDb);
                System.out.println("Изменён пользователь: "+userFromDb);
                EditMessageText new_message = new EditMessageText()
                        .setChatId(callbackQuery.getMessage().getChatId())
                        .setMessageId(callbackQuery.getMessage().getMessageId())
                        .setText("2 дня активированы!");
                try {
                    editMessageText(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private void mainContext(Update update) {
        if (update.hasMessage()&&update.getMessage().hasText()){
            String texOfMessage = update.getMessage().getText();
            SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());
            CommandButtons button = CommandButtons.getTYPE(texOfMessage);
            switch (button){
                case START:
                    message.setText(BotMessages.MAIN_MENU.getText());
                    message.setReplyMarkup(mainKeyboardMarkup);
                    break;
                case OFORMIT_PODPISCU:
                    message.setText(BotMessages.SELECT_SUBSCRIPTION.getText());
                    message.setReplyMarkup(subscripMenuMarkup);
                    break;
                case INFO_BOT:
                    message.setText(BotMessages.SHORT_DESCRIPTION.getText());
                    message.setReplyMarkup(infoMenuMarkup);
                    break;
                case GENERAL_DESCRIPTION:
                    message.setText(BotMessages.GENERAL_DESCRIPTION.getText());
                    break;
                case FAQ:
                    message.setText(BotMessages.FAQ.getText());
                    break;
                case HOW_TO_CHANGE_CURRENCY:
                    message.setText(BotMessages.HOW_TO_CHANGE_CURRENCY.getText());
                    break;
                case SUPPORT:
                    message.setText(BotMessages.SUPPORT.getText());
                    break;
                case BACK_IN_MAIN_MENU:
                    message.setText(BotMessages.MAIN_MENU.getText());
                    message.setReplyMarkup(mainKeyboardMarkup);
                    break;
                case ONE_MONTH:
                    message.setText(BotMessages.ONE_MONTH.getText());
                    break;
                case TWO_MONTH:
                    message.setText(BotMessages.TWO_MONTH.getText());
                    break;
                case THREE_MONTH:
                    message.setText(BotMessages.THREE_MONTH.getText());
                    break;
                case CHECK_SUBSCRIPTION:
                    LocalDate endDate = checkSubscription(update);
                    if (endDate!=null) {
                        if (endDate.isAfter(LocalDate.now())) {
                            message.setText(BotMessages.CHECK_SUBSCRIPTION.getText() + endDate);
                        }else
                            message.setText("Ваша подписка истекла: "+endDate);
                    } else
                        message.setText("У вас нет подписки!");
                    break;
                default:
                    message.setText(BotMessages.DEFAULT.getText());
            }
            try {
                sendMessage(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private LocalDate checkSubscription(Update update) {
        User user = dbService.getUserFromDb(update.getMessage().getChat().getId());
        return user.getEndDate();
    }

    private void startContext(Update update) {
        long userID = update.getMessage().getChat().getId();
        String userName = update.getMessage().getChat().getUserName();
        String firstName = update.getMessage().getChat().getFirstName();
        String lastName = update.getMessage().getChat().getLastName();
        long chatID = update.getMessage().getChatId();
        User newUser = new User(userID,userName,firstName,lastName,chatID);
        SendMessage message = new SendMessage().setChatId(chatID);
        if (update.hasMessage()&&update.getMessage().getText().equals("/start")){
            dbService.addUserInDb(newUser);
            System.out.println("в базу добавлен пользователь: "+newUser);
            message.setText("Добро пожаловать, "+firstName+"!"
                    +"\n");
            message.setReplyMarkup(mainKeyboardMarkup);
            try {
                sendMessage(message);
                message.setText("Ты здесь впервые! Можешь воспользоваться пробным периодом.");
                message.setReplyMarkup(createTrialInlineButton());
                sendMessage(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            message.setText("Тебя еще нет в базе, отправь  /start");
            try {
                sendMessage(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


    }

    public void createMainKeyboardMarkup(){
        mainKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(CommandButtons.OFORMIT_PODPISCU.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(CommandButtons.INFO_BOT.getText()));
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
        keyboardRow1.add(new KeyboardButton(CommandButtons.ONE_MONTH.getText()));
        keyboardRow1.add(new KeyboardButton(CommandButtons.TWO_MONTH.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(CommandButtons.THREE_MONTH.getText()));
        keyboardRow2.add(new KeyboardButton(CommandButtons.CHECK_SUBSCRIPTION.getText()));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton(CommandButtons.BACK_IN_MAIN_MENU.getText()));
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
        keyboardRow1.add(new KeyboardButton(CommandButtons.GENERAL_DESCRIPTION.getText()));
        keyboardRow1.add(new KeyboardButton(CommandButtons.FAQ.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(CommandButtons.HOW_TO_CHANGE_CURRENCY.getText()));
        keyboardRow2.add(new KeyboardButton(CommandButtons.SUPPORT.getText()));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton(CommandButtons.BACK_IN_MAIN_MENU.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        infoMenuMarkup.setKeyboard(keyboardRows);
        //infoMenuMarkup.setOneTimeKeyboard(true);
        infoMenuMarkup.setResizeKeyboard(true);
    }

    private InlineKeyboardMarkup createTrialInlineButton(){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText(" Активировать на 2 дня").setCallbackData("settrial"));
        //List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        //rowInline2.add(new InlineKeyboardButton().setText("2 месяца").setCallbackData("2month"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
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
