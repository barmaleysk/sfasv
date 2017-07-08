package telegram_services;

import database_service.DbService;
import entitys.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
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
    ReplyKeyboardMarkup settingsMenuMarkup;

    private DbService dbService;

    public TelegramService(DbService dbService) {
        this.dbService = dbService;
        createMainKeyboardMarkup();
        createSubscripMenuMarkup();
        createInfoMenuMarkup();
        createSettingsMenuMarkup();
    }




    @Override
    public void onUpdateReceived(Update update) {
        //выберем контекст
        if (update.hasMessage()&&update.getMessage().hasText()) {
            long userID = update.getMessage().getChat().getId();
            User userFromDb = dbService.getUserFromDb(userID);
            if (userFromDb != null) {
                mainContext(update.getMessage());
            } else {
                startContext(update.getMessage());
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
                System.out.println("текущая дата"+LocalDate.now());
                userFromDb.setEndDate(LocalDate.now().plusDays(2));
                System.out.println("дата до сохранения"+userFromDb.getEndDate());
                dbService.addUserInDb(userFromDb);
                System.out.println("Дата из базы"+dbService.getUserFromDb(callbackQuery.getMessage().getChat().getId()).getEndDate());
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

    private void mainContext(Message incomingMessage) {
            String texOfMessage = incomingMessage.getText();
            SendMessage message = new SendMessage().setChatId(incomingMessage.getChatId());
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
                    LocalDate endDate = checkSubscription(incomingMessage);
                    if (endDate!=null) {
                        if (endDate.isAfter(LocalDate.now())) {
                            message.setText(BotMessages.CHECK_SUBSCRIPTION.getText() + endDate);
                        }else {
                            message.setText("Ваша подписка истекла: " + endDate);
                        }
                    } else {
                        message.setText("У вас нет подписки!");
                    }
                    break;
                case SETTINGS:
                    message.setText(BotMessages.SETTINGS_MENU.getText());
                    message.setReplyMarkup(settingsMenuMarkup);
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

    private LocalDate checkSubscription(Message incomingMessage) {
        User user = dbService.getUserFromDb(incomingMessage.getChat().getId());
        System.out.println("дата из базы: "+user.getEndDate());
        return user.getEndDate();
    }

    private void startContext(Message message) {
        long userID = message.getChat().getId();
        String userName = message.getChat().getUserName();
        String firstName = message.getChat().getFirstName();
        String lastName = message.getChat().getLastName();
        long chatID = message.getChatId();
        User newUser = new User(userID,userName,firstName,lastName,chatID);
        SendMessage newMessage = new SendMessage().setChatId(chatID);
        if (message.getText().equals("/start")){
            dbService.addUserInDb(newUser);
            System.out.println("в базу добавлен пользователь: "+newUser);
            newMessage.setText("Добро пожаловать, "+firstName+"!"+"\n");
            newMessage.setReplyMarkup(mainKeyboardMarkup);
            try {
                sendMessage(newMessage);
                newMessage.setText("Ты здесь впервые и можешь воспользоваться пробным периодом!");
                newMessage.setReplyMarkup(createTrialInlineButton());
                sendMessage(newMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            newMessage.setText("Тебя еще нет в базе, отправь  /start");
            try {
                sendMessage(newMessage);
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
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton(CommandButtons.SETTINGS.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
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

    private void createSettingsMenuMarkup() {
        settingsMenuMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(CommandButtons.REQUISITES.getText()));
        keyboardRow1.add(new KeyboardButton(CommandButtons.PARTNER_PROGRAM.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(CommandButtons.BACK_IN_MAIN_MENU.getText()));
        keyboardRow2.add(new KeyboardButton(CommandButtons.ADD_REFERAL.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        settingsMenuMarkup.setKeyboard(keyboardRows);
        settingsMenuMarkup.setResizeKeyboard(true);
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
}
