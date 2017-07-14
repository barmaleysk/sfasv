package telegram_services;

import database_service.DbService;
import database_service.NoUserInDb;
import entitys.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.time.LocalDate;
import java.util.List;


/**
 * Created by kuteynikov on 14.07.2017.
 */
public class MessageHedler {
    private DbService dbService;
    private ReplyKeyboardMarkup mainMenuMarkup;
    private ReplyKeyboardMarkup subscripMenuMarkup;
    private ReplyKeyboardMarkup infoMenuMarkup;
    private ReplyKeyboardMarkup settingsMenuMarkup;
    private ReplyKeyboardMarkup partnerMenuMarkup;
    private InlineKeyboardMarkup trialInlineButton;
    private TelegramService telegramService;

    public MessageHedler(DbService dbService, TelegramService telegramService) {
        this.dbService = dbService;
        this.telegramService = telegramService;
        mainMenuMarkup = MenuCreator.createMainMenuMarkup();
        subscripMenuMarkup = MenuCreator.createSubscripMenuMarkup();
        infoMenuMarkup = MenuCreator.createInfoMenuMarkup();
        settingsMenuMarkup = MenuCreator.createSettingsMenuMarkup();
        partnerMenuMarkup = MenuCreator.createPartnersMenu();
        trialInlineButton = MenuCreator.createTrialInlineButton();
    }

    public SendMessage startContext(Message message) {
        long userID = message.getChat().getId();
        String userName = message.getChat().getUserName();
        String firstName = message.getChat().getFirstName();
        String lastName = message.getChat().getLastName();
        long chatID = message.getChatId();
        String textOfInputMessage = message.getText();
        User newUser = new User(userID,userName,firstName,lastName,chatID);
        SendMessage replyMessage = new SendMessage().setChatId(chatID);

        if (textOfInputMessage.equals("/start")){
            dbService.addRootUser(newUser);
            System.out.println("в базу добавлен пользователь: "+newUser);
            replyMessage.setText("Добро пожаловать, "+firstName+"!"+"\n");
            replyMessage.setReplyMarkup(mainMenuMarkup);
            telegramService.messageSend(replyMessage);

        }else if (textOfInputMessage.startsWith("/start=")) {
            String stringID = message.getText().substring(7);
            Long parentuserID = Long.parseLong(stringID);
            try {
                dbService.addChildrenUser(parentuserID,newUser);
                replyMessage.setText("Добро пожаловать, "+firstName+"!"+"\n");
                System.out.println("В базу добавлен приглашённый пользователь: "+newUser);
            } catch (NoUserInDb noUserInDb) {
                System.out.println("Ошибка в id пригласителя: "+newUser);
                replyMessage.setText("Добро пожаловать, "+firstName+"!"
                        +"\n Ошибка в id пригласителя, свяжитесь с тех поддержкой, и поробуйте добавить пригласителя вручную");
                replyMessage.setReplyMarkup(mainMenuMarkup);
                telegramService.messageSend(replyMessage);
            }
            replyMessage.setText("Ты здесь впервые и можешь воспользоваться пробным периодом!");
            replyMessage.setReplyMarkup(trialInlineButton);
        }else {
            replyMessage.setText("Ошибка! Тебя еще нет в базе, отправь  /start");

        }
        return replyMessage;
    }

    public SendMessage mainContext(Message incomingMessage) {
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
                LocalDate endDate = dbService.getEndOfSubscription(incomingMessage.getChat().getId());
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
        return message;
    }


}
