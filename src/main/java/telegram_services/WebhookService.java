package telegram_services;

import database_service.DbService;
import database_service.NoUserInDb;
import entitys.TaskStatus;
import entitys.TaskType;
import entitys.Tasks;
import entitys.User;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by kuteynikov on 14.07.2017.
 */
public class WebhookService extends TelegramWebhookBot  {
    private DbService dbService;
    private ReplyKeyboardMarkup mainMenuMarkup;
    private ReplyKeyboardMarkup subscripMenuMarkup;
    private ReplyKeyboardMarkup infoMenuMarkup;
    private ReplyKeyboardMarkup settingsMenuMarkup;
    private ReplyKeyboardMarkup partnerMenuMarkup;
    private InlineKeyboardMarkup trialInlineButton;

    public WebhookService(DbService dbService) {
        this.dbService = dbService;
        mainMenuMarkup = MenuCreator.createMainMenuMarkup();
        subscripMenuMarkup = MenuCreator.createSubscripMenuMarkup();
        infoMenuMarkup = MenuCreator.createInfoMenuMarkup();
        settingsMenuMarkup = MenuCreator.createSettingsMenuMarkup();
        partnerMenuMarkup = MenuCreator.createPartnersMenu();
        trialInlineButton = MenuCreator.createTrialInlineButton();
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        //++count;
        //System.out.println("reciv update"+count);
        if (update.hasCallbackQuery()) {
            //System.out.println("пришел CallbackQuery: " + update.getCallbackQuery());
            EditMessageText editMessageText = callBackContext(update.getCallbackQuery());
            return editMessageText;
        } else if (update.hasMessage()&update.getMessage().hasText()){
            long userId = update.getMessage().getChat().getId();
            SendMessage sendMessage;
            if (!dbService.dbHasUser(userId)){
                sendMessage = startContext(update.getMessage());
            }else if (update.getMessage().getText().startsWith("/")){
                sendMessage = commandContext(update.getMessage());
            } else {
                sendMessage = mainContext(update.getMessage());
            }
            return sendMessage;
        } else {
            return null;
        }
    }

    public SendMessage startContext(Message message) {
        long userID = message.getChat().getId();
        String userName = "@"+message.getChat().getUserName();
        String firstName = message.getChat().getFirstName();
        String lastName = message.getChat().getLastName();
        long chatID = message.getChatId();
        String textOfInputMessage = message.getText();
        User newUser = new User(userID, userName, firstName, lastName, chatID);
        newUser.setEndDateOfSubscription(LocalDateTime.now().plusDays(2));
        System.out.println("user создан " + newUser);
        SendMessage replyMessage = new SendMessage().setChatId(chatID);
        if (textOfInputMessage.equals("/start")) {
            dbService.addRootUser(newUser);
            System.out.println("в базу добавлен пользователь: " + newUser);
            replyMessage.setText("Добро пожаловать, " + firstName + "!"
                    + "\nУ вас бесплатный тестовый период 2 дня, если вам всё понравилось, то купите пописку $_$");
            replyMessage.setReplyMarkup(mainMenuMarkup);

        } else if (textOfInputMessage.startsWith("/start ")) {
            String stringID = message.getText().substring(7);
            Long parentuserID = Long.parseLong(stringID);
            try {
                dbService.addChildrenUser(parentuserID, newUser);
                replyMessage.setText("Добро пожаловать, " + firstName + "!"
                        + "\nУ вас бесплатный тестовый период 2 дня, если вам всё понравилось, то купите пописку $_$");
                replyMessage.setReplyMarkup(mainMenuMarkup);
                System.out.println("В базу добавлен приглашённый пользователь: " + newUser);
            } catch (NoUserInDb noUserInDb) {
                dbService.addRootUser(newUser);
                System.out.println("Ошибка в id пригласителя: " + newUser);
                replyMessage.setText("Добро пожаловать, " + firstName + "!"
                        + "\n Ошибка в id пригласителя, свяжитесь с тех поддержкой, и поробуйте добавить пригласителя вручную"
                        +"\nУ вас бесплатный тестовый период 2 дня, если вам всё понравилось, то купите пописку $_$");
                replyMessage.setReplyMarkup(mainMenuMarkup);
            }
        } else {
            System.out.println("пользователь не в базе шлёт сообщение :" + message.getText());
            replyMessage.setText("Ошибка! Тебя еще нет в базе, отправь  /start");
        }
        return replyMessage;
    }

    public SendMessage mainContext(Message incomingMessage) {
        String texOfMessage = incomingMessage.getText();
        SendMessage message = new SendMessage().setChatId(incomingMessage.getChatId());
        CommandButtons button = CommandButtons.getTYPE(texOfMessage);
        switch (button) {
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
                message.setReplyMarkup(
                        MenuCreator.createPayButton("userId="+incomingMessage.getChat().getId()+"&typeOfParchase=oneMonth"));
                break;
            case TWO_MONTH:
                message.setText(BotMessages.TWO_MONTH.getText());
                message.setReplyMarkup(
                        MenuCreator.createPayButton("userId="+incomingMessage.getChat().getId()+"&typeOfParchase=twoMonth"));
                break;
            case THREE_MONTH:
                message.setText(BotMessages.THREE_MONTH.getText());
                message.setReplyMarkup(
                        MenuCreator.createPayButton("userId="+incomingMessage.getChat().getId()+"&typeOfParchase=threeMonth"));
                break;
            case CHECK_SUBSCRIPTION:
                LocalDate endDate = dbService.getEndOfSubscription(incomingMessage.getChat().getId());
                if (endDate != null) {
                    if (endDate.isAfter(LocalDate.now())) {
                        message.setText(BotMessages.CHECK_SUBSCRIPTION.getText() + endDate);
                    } else {
                        message.setText("Ваша подписка истекла: " + endDate);
                    }
                } else {
                    message.setText("У вас еще не было подписки"
                            + "\n ");
                    message.setReplyMarkup(trialInlineButton);
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
                        + "\n https://t.me/Sl0wP0ke_Bot?start=" + incomingMessage.getChat().getId()
                        + "\n");
                break;
            case CHECK_REFERALS:
                User parentUser = dbService.getUserFromDb(incomingMessage.getChat().getId());
                int parentLevel = parentUser.getLevel();
                int parentLeftKey = parentUser.getLeftKey();
                int parentRightKey = parentUser.getRightKey();
                String text;
                if (parentLeftKey + 1 == parentRightKey) {
                    text = "У вас нет рефералов";
                } else {
                    List<User> userList = dbService.getChildrenUsers(parentLevel, parentLeftKey, parentRightKey);
                    String level1 = "";
                    String level2 = "";
                    String level3 = "";
                    for (User u : userList) {
                        System.out.println(u);
                        if (parentLevel + 1 == u.getLevel()) {
                            level1 = level1 + " " + u.getUserName() + "-" + u.getFirstName()+";\n";
                        } else if (parentLevel + 2 == u.getLevel()) {
                            level2 = level2 + " " + u.getUserName() + "-" + u.getFirstName()+";\n";
                        } else {
                            level3 = level3 + " " + u.getUserName() + "-" + u.getFirstName()+"\n";
                        }
                    }
                    text = "Рефералы 1го уровня: "
                            + "\n " + level1
                            + "\nРефералы 2го уровня: "
                            + "\n" + level2
                            + "\nРефералы 3го уровня: "
                            + "\n" + level3;
                }
                message.setText(text);
                break;
            case LOCAL_WALLET:
                User user = dbService.getUserFromDb(incomingMessage.getChat().getId());
                BigDecimal cash = user.getLocalWallet();
                String string = "На вашем счету: *"+cash +"*"
                                +"\n\nОставьте заявку, чтобы вывести бонусы на ваш счет."
                                + "\n Проверте, правильно ли укзан ваш кошелек(если нет, смените его в настройках):"
                                + "\n Advcash:"+user.getAdvcashWallet()
                                + "\n Баланс должен быть положительным."
                                + "\n Зявки обрабатываются в конце недели.\n";
                message.setText(string);
                message.setReplyMarkup(MenuCreator.createPaymentsBonusButton());
                break;
            default:
                message.setText(BotMessages.DEFAULT.getText());
        }
        return message;
    }

    public EditMessageText callBackContext(CallbackQuery callbackQuery) {
        String dataFromQuery = callbackQuery.getData();
        CommandButtons button = CommandButtons.getTYPE(dataFromQuery);
        EditMessageText new_message = new EditMessageText()
                .setChatId(callbackQuery.getMessage().getChatId())
                .setMessageId(callbackQuery.getMessage().getMessageId());
        switch (button) {
            case SET_TRIAL:
                User userFromDb = dbService.getUserFromDb(callbackQuery.getMessage().getChat().getId());
                userFromDb.setEndDateOfSubscription(LocalDateTime.now().plusDays(2));
                dbService.updateUser(userFromDb);
                System.out.println("Изменён пользователь: " + userFromDb);
                new_message.setText("2 дня активированы!");
                break;
            case REQUEST_PAYMENT_BUTTON:
                User user = dbService.getUserFromDb(callbackQuery.getMessage().getChat().getId());
                System.out.println("достали пользователя "+user);
                BigDecimal cash = user.getPersonalData().getLocalWallet();
                System.out.println("cash="+cash);
                List <Tasks> tasks = user.getTasks();
                Tasks task =null;
                System.out.println("проверяем наличие заявки");
                if (tasks!=null&&tasks.size()>0){
                    for (Tasks t : tasks){
                        if (t.getStatus().equals(TaskStatus.OPEN)&&t.getType().equals(TaskType.PAY_BONUSES))
                            task=t;
                    }
                }
                int checkCash=-1;
                if (cash!=null)
                 checkCash = cash.compareTo(new BigDecimal("0.01"));
                System.out.println("checkCash="+checkCash);
                String string = "";
                if (user.getPersonalData().getAdvcashWallet()==null) {
                    System.out.println("Заявка не принята, не установлен кошелёк Advcash.");
                    string = "Заявка не принята, не установлен кошелёк Advcash.";
                } else if (checkCash==-1) {
                    string = "Заявка не принята, не достаточно средств.";
                    System.out.println(string);
                }else if (task!=null) {
                    string = " Вы уже подали заявку " + task.getDateTimeOpening();
                    System.out.println(string);
                }else {
                    task = new Tasks(TaskType.PAY_BONUSES,user);
                    dbService.addTask(user.getUserID(),task);
                    string = "Заявка принята!";
                    System.out.println(string);
                }
                System.out.println(string);
                new_message.setText(string);
                break;
            default:
                new_message.setText("Что то пошло не так обратитесь в тех. поддрежку");
                break;
        }
        return new_message;
    }


    public SendMessage commandContext(Message incomingMessage) {
        String textIncomingMessage = incomingMessage.getText();
        if (textIncomingMessage.startsWith(CommandButtons.SEND_SIGNAL.getText())){
          //  List<>
        }

        return null;
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
    public String getBotPath() {
        return "Sl0wP0ke_Bot";
    }

}
