package telegram_services;

import database_service.DbService;
import database_service.NoUserInDb;
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
public class LongpollingSevice extends TelegramLongPollingBot {
    ReplyKeyboardMarkup mainMenuMarkup;
    ReplyKeyboardMarkup subscripMenuMarkup;
    ReplyKeyboardMarkup infoMenuMarkup;
    ReplyKeyboardMarkup settingsMenuMarkup;
    ReplyKeyboardMarkup partnerMenuMarkup;

    private DbService dbService;

    public LongpollingSevice(DbService dbService) {
        this.dbService = dbService;
        createMainMenuMarkup();
        createSubscripMenuMarkup();
        createInfoMenuMarkup();
        createSettingsMenuMarkup();
        this.partnerMenuMarkup =createPartnersMenu();
    }

    @Override
    public void onUpdateReceived(Update update) {
        //выберем контекст

    }

    private void callBackContext(CallbackQuery callbackQuery) {
        String dataFromQuery = callbackQuery.getData();
        CommandButtons button = CommandButtons.getTYPE(dataFromQuery);
        EditMessageText new_message = new EditMessageText()
                .setChatId(callbackQuery.getMessage().getChatId())
                .setMessageId(callbackQuery.getMessage().getMessageId());
        switch (button) {
            case SET_TRIAL:
                User userFromDb = dbService.getUserFromDb(callbackQuery.getMessage().getChat().getId());
                userFromDb.setEndDate(LocalDate.now().plusDays(2));
                dbService.updateUser(userFromDb);
                System.out.println("Изменён пользователь: " + userFromDb);
                new_message.setText("2 дня активированы!");
                break;
            default:
                new_message.setText("Что то пошло не так обратитесь в тех. поддрежку");
                break;
        }
        try {
            editMessageText(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void mainContext(Message incomingMessage) {
            String texOfMessage = incomingMessage.getText();
            SendMessage message = new SendMessage().setChatId(incomingMessage.getChatId());
            CommandButtons button = CommandButtons.getTYPE(texOfMessage);
            switch (button){
                case START:
                    message.setText(BotMessages.MAIN_MENU.getText());
                    message.setReplyMarkup(mainMenuMarkup);
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
                    message.setReplyMarkup(mainMenuMarkup);
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
                case PARTNER_PROGRAM:
                    message.setText(CommandButtons.PARTNER_PROGRAM.getText());
                    message.setReplyMarkup(partnerMenuMarkup);
                    break;
                case INVITE_PARTNER:
                    message.setText("Чтобы пригласить участника, скопируйте и отправьте ему эту ссылку "
                            +"\n https://t.me/Sl0wP0ke_Bot?start="+incomingMessage.getChat().getId()
                            +"\n");
                    break;
                case CHECK_REFERALS:
                    User parentUser = dbService.getUserFromDb(incomingMessage.getChat().getId());
                    System.out.println(parentUser);
                    int parentLevel=parentUser.getLevel();
                    int parentLeftKey=parentUser.getLeftKey();
                    int parentRightKey = parentUser.getRightKey();
                    String text;
                    if (parentLeftKey+1==parentRightKey){
                        text = "У вас нет рефералов";
                    } else {
                        List<User> userList = dbService.getChildrenUsers(parentLevel,parentLeftKey,parentRightKey);
                        String level1="";
                        String level2="";
                        String level3="";
                        for (User u : userList) {
                            if (parentLevel==u.getLevel()+1){
                                level1=level1+" "+u.getUserName()+"-"+u.getFirstName();
                            }else if (parentLevel==u.getLevel()+2){
                                level2=level2+" "+u.getUserName()+"-"+u.getFirstName();
                            }else {
                                level3=level3+" "+u.getUserName()+"-"+u.getFirstName();
                            }
                        }
                        text = "Рефералы 1го уровня: "
                                +"\n "+level1
                                +"\nРефералы 2го уровня: "
                                +"\n"+level2
                                +"\nРефералы 3го уровня: "
                                +"\n"+level3;
                    }
                    message.setText(text);
                    break;
                case BACK_IN_SETTINGS:
                    message.setText(BotMessages.SETTINGS_MENU.getText());
                    message.setReplyMarkup(settingsMenuMarkup);
                    break;
                default:
                    message.setText(BotMessages.DEFAULT.getText());
            }
            mySendMessage(message);
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
            dbService.addRootUser(newUser);
            System.out.println("в базу добавлен пользователь: "+newUser);
            newMessage.setText("Добро пожаловать, "+firstName+"!"+"\n");
            newMessage.setReplyMarkup(mainMenuMarkup);
            mySendMessage(newMessage);
            newMessage.setText("Ты здесь впервые и можешь воспользоваться пробным периодом!");
            newMessage.setReplyMarkup(createTrialInlineButton());
            mySendMessage(newMessage);

        }else if (message.getText().startsWith("/start=")) {
              String stringID = message.getText().substring(7);
              Long parentuserID = Long.parseLong(stringID);
            try {
                dbService.addChildrenUser(parentuserID,newUser);
                newMessage.setText("Добро пожаловать, "+firstName+"!"+"\n");
                System.out.println("В базу добавлен приглашённый пользователь: "+newUser);
            } catch (NoUserInDb noUserInDb) {
                System.out.println("Ошибка в id пригласителя: "+newUser);
                newMessage.setText("Ошибка в id пригласителя, свяжитесь с тех поддержкой, и поробуйте добавить пригласителя вручную");
            }
            newMessage.setReplyMarkup(mainMenuMarkup);
            mySendMessage(newMessage);
            newMessage.setText("Ты здесь впервые и можешь воспользоваться пробным периодом!");
            newMessage.setReplyMarkup(createTrialInlineButton());
            mySendMessage(newMessage);
        }else {
            newMessage.setText("Ошибка! Тебя еще нет в базе, отправь  /start");
            mySendMessage(newMessage);
        }
    }

    private void mySendMessage(SendMessage message){
        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            System.out.println("Не смог отправить сообщение в чат №: "+message.getChatId());
            e.printStackTrace();
        }
    }

    public void createMainMenuMarkup(){
        mainMenuMarkup = new ReplyKeyboardMarkup();
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
        mainMenuMarkup.setKeyboard(keyboardRows);
       // mainMenuMarkup.setOneTimeKeyboard(true);
        mainMenuMarkup.setResizeKeyboard(true);
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
        rowInline1.add(new InlineKeyboardButton().setText(CommandButtons.SET_TRIAL.getText()).setCallbackData(CommandButtons.SET_TRIAL.getText()));
        //List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        //rowInline2.add(new InlineKeyboardButton().setText("2 месяца").setCallbackData("2month"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private ReplyKeyboardMarkup createPartnersMenu(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(CommandButtons.INVITE_PARTNER.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(CommandButtons.BACK_IN_SETTINGS.getText()));
        keyboardRow2.add(new KeyboardButton(CommandButtons.CHECK_REFERALS.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    private LocalDate checkSubscription(Message incomingMessage) {
        User user = dbService.getUserFromDb(incomingMessage.getChat().getId());
        return user.getEndDate();
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
