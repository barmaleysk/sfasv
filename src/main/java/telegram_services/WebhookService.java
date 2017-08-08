package telegram_services;

import configs.GlobalConfigs;
import database_service.DbService;
import database_service.NoTaskInDb;
import database_service.NoUserInDb;
import entitys.*;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import telegram_services.exceptions.AlreadyClosenTask;
import telegram_services.exceptions.TaskTypeException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kuteynikov on 14.07.2017.
 */
public class WebhookService extends TelegramWebhookBot  {
    private static final Logger log = Logger.getLogger(WebhookService.class);
    private DbService dbService;
    private ReplyKeyboardMarkup mainMenuMarkup;
    private ReplyKeyboardMarkup subscripMenuMarkup;
    private ReplyKeyboardMarkup infoMenuMarkup;
    private ReplyKeyboardMarkup settingsMenuMarkup;
    private ReplyKeyboardMarkup partnerMenuMarkup;
    private InlineKeyboardMarkup trialInlineButton;
    private InlineKeyboardMarkup paymentsBonusButton;
    private GroupChatBot groupChatBot;

    public WebhookService(TelegramLongPollingBot groupChatBot) {
        this.groupChatBot = (GroupChatBot) groupChatBot;
        this.dbService = DbService.getInstance();
        this.mainMenuMarkup = MenuCreator.createMainMenuMarkup();
        this.subscripMenuMarkup = MenuCreator.createSubscripMenuMarkup();
        this.infoMenuMarkup = MenuCreator.createInfoMenuMarkup();
        this.settingsMenuMarkup = MenuCreator.createSettingsMenuMarkup();
        this.partnerMenuMarkup = MenuCreator.createPartnersMenu();
        this.trialInlineButton = MenuCreator.createTrialInlineButton();
        this.paymentsBonusButton = MenuCreator.createPaymentsBonusButton();
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            //System.out.println("пришел CallbackQuery: " + update.getCallbackQuery());
            EditMessageText editMessageText = callBackContext(update.getCallbackQuery());
            return editMessageText;
        } else if(update.getMessage().getChat().isSuperGroupChat()){
            System.out.println("сообщение из группового чата id="+update.getMessage().getChatId());
            return PrivateGroupContext(update.getMessage());
        } else if (update.hasMessage()&update.getMessage().hasText()){
            long userId = update.getMessage().getChat().getId();
            SendMessage sendMessage;
            if (!dbService.dbHasUser(userId)){
                sendMessage = startContext(update.getMessage());
            }else if (update.getMessage().getText().startsWith("/")&&!update.getMessage().getText().equals("/start")){
                sendMessage = adminCommandContext(update.getMessage());
            } else {
                sendMessage = mainContext(update.getMessage());
            }
            return sendMessage;
        } else {
            return null;
        }
    }



    public SendMessage startContext(Message message) {
        //вытаскиваем данные из сообщения и создаем пользователя
        long userID = message.getChat().getId();
        String userName = "@"+message.getChat().getUserName();
        String tempFirstName = message.getChat().getFirstName();
        String tempmLastName = message.getChat().getLastName();

        String firstName=null;
        String lastName=null;

        String p = "[\\w]*";
        Pattern pattern = Pattern.compile(p,Pattern.UNICODE_CHARACTER_CLASS);

        try {
            Matcher matcher = pattern.matcher(tempFirstName);
            if (tempFirstName!=null&&matcher.matches()) {
                firstName = tempFirstName;
            }
        }catch (Exception e){
            log.info(" у пользователя"+message.getChat()+" картинка в имени");
        }
        try {
            Matcher matcher = pattern.matcher(tempmLastName);
            if (tempmLastName!=null&&matcher.matches()) {
                lastName = tempmLastName;
            }
        }catch (Exception e){
            log.info(" у пользователя"+message.getChat()+" картинка в фамилии");
        }
        long chatID = message.getChatId();
        User newUser = new User(userID, userName, firstName, lastName, chatID);
        newUser.setEndDateOfSubscription(LocalDateTime.now());//.plusDays(1)); //включаем тестовый период
        log.info("New User created: "+newUser);
        //готовим сообщение для ответа
        SendMessage replyMessage = new SendMessage().setChatId(chatID).enableMarkdown(true);
        String welcomeText="*"+firstName + "*, рады приветствовать Вас в проекте New Wave, мы готовы предоставить, лучшие сигналы для торговли на криптовалютном рынке!\n";
               // + "\nУ вас бесплатный тестовый период 1 день, если вам всё понравилось, то купите подписку";
        welcomeText=userName.equals("@null")?welcomeText+"\n*Внимание!* У Вас не заполнен *Username* в настройках *Telegram*, он необходим для взаимодействия с Вами," +
                "пожалуйста задайте его , затем(чтобы обновить в нашей базе), в меню бота нажмите *Параметры* -> *Мои данные*":welcomeText;
        //проверяем start без параметров=без приглашения||с параметрами=приглашенный
        String start= message.getText();
        if (start.equals("/start")) {
            dbService.addRootUser(newUser);
            log.info("В базу добавлен не приглашенный пользователь "+newUser);
            replyMessage.setText(welcomeText);
            replyMessage.setReplyMarkup(mainMenuMarkup);
        } else if (start.startsWith("/start ")) {
            String stringID = message.getText().substring(7);
            Long parentuserID = Long.parseLong(stringID);
            try {
                dbService.addChildrenUser(parentuserID, newUser);
                replyMessage.setText(welcomeText);
                replyMessage.setReplyMarkup(mainMenuMarkup);
                log.info("В базу добавлен приглашённый пользователь: " + newUser);
            } catch (NoUserInDb noUserInDb) {
                dbService.addRootUser(newUser);
                log.info("Ошибка в id пригласителя");
                replyMessage.setText( welcomeText+"\nОшибка в id пригласителя, ссылка по котрой вы перешли не корректна");
                replyMessage.setReplyMarkup(mainMenuMarkup);
            }
        } else {
            log.info("пользователь"+userID+userName+" не в базе шлёт сообщение :" + message.getText());
            replyMessage.setText("Ошибка! Тебя еще нет в базе, отправь  /start").enableMarkdown(false);
        }
        return replyMessage;
    }

    public SendMessage mainContext(Message incomingMessage) {
        String texOfMessage = incomingMessage.getText();
        SendMessage message = new SendMessage()
                .setChatId(incomingMessage.getChatId())
                .setText(BotMessages.DEFAULT.getText());
        CommandButtons button = CommandButtons.getTYPE(texOfMessage);
        User user = null;
        switch (button) {
            case START:
                message.setText(BotMessages.MAIN_MENU.getText());
                message.setReplyMarkup(mainMenuMarkup);
                break;
            case SHOW_SIGNAL:
                user = dbService.getUserFromDb(incomingMessage.getChatId());
                if (user.getServices().getEndDateOfSubscription().toLocalDate().isAfter(LocalDate.now())||user.getServices().getUnlimit()){
                    List<Signal> signals = dbService.getSignals();
                    System.out.println(signals);
                    if (signals!=null&&signals.size()>0){
                        StringBuilder builder = new StringBuilder();
                        for (Signal s : signals){
                            builder.append(s.getDateTime().toLocalDate()).append(" ").append(s.getDateTime().toLocalTime())
                                    .append("\n").append(s.getText())
                                    .append("\n*********");
                            System.out.println(builder.toString());
                            SendMessage sendMessage = new SendMessage(incomingMessage.getChatId(),builder.toString());
                            try {
                                sendApiMethod(sendMessage);
                            } catch (TelegramApiException e) { log.error("Не смог повторить сигнал "+incomingMessage.getChatId()); }

                            message.setText("******");
                        }
                    } else{ message.setText("Новых сигналов пока нет");}
                }else { message.setText("Повтор сигналов доступен только при наличии подписки");}
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
                message.setText(BotMessages.FAQ.getText()).enableMarkdown(true);
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
                user = dbService.getUserFromDb(incomingMessage.getChat().getId());
                LocalDate endDate = user.getServices().getEndDateOfSubscription().toLocalDate();
                if (user.getServices().getUnlimit()) {
                    message.setText("У вас безлимитная подписка!");
                } else if (endDate != null) {
                    if (endDate.isAfter(LocalDate.now())) {
                        message.setText(BotMessages.CHECK_SUBSCRIPTION.getText() + endDate);
                    } else {
                        message.setText("Ваша подписка истекла: " + endDate);
                    }
                }
                //else {
                 //   message.setText("У вас еще не было подписки"
                  //          + "\n ");
                 //   message.setReplyMarkup(trialInlineButton);
               // }
                break;
            case PRIVATE_CHAT:
                User userPC = dbService.getUserFromDb(incomingMessage.getChatId());
                if (userPC.getPersonalData().getUserNameTelegram().equals("@null")){
                    message.setText("Чтобы воспользоваться услугой, заполните username в настройках телеграм, и нажмите \"Мои данные\" в меню \"Параметры\"").enableMarkdown(false);
                } else if (userPC.getServices().getOnetimeConsultation()
                        ||userPC.getServices().getUnlimit()){
                    message.setText("Оставьте заявку и вас пригласят в чат");
                    message.setReplyMarkup(MenuCreator.createInlineButton(CommandButtons.TASK_PRIVATE_CHAT));
                } else {
                    message.setText("Кнопка запроса будет доступна после оплаты." +
                            "\n Стоимость персональной консультации 0.35,");
                    message.setReplyMarkup(MenuCreator.createPayButton("userId="+incomingMessage.getChat().getId()+"&typeOfParchase=oneTimeConsultation"));
                }
                break;
            case UNLIMIT:
                message.setText(BotMessages.UNLIMIT_SUBSCRIPTION.getText());
                message.setReplyMarkup(
                        MenuCreator.createPayButton("userId="+incomingMessage.getChat().getId()+"&typeOfParchase=unlimit"));
                break;
            case INVITE_TO_CHAT:
                user = dbService.getUserFromDb(incomingMessage.getChatId());
                if (user!=null&&user.getServices().getEndDateOfSubscription().toLocalDate().isAfter(LocalDate.now())||user.getServices().getUnlimit()) {
                    if (user.getServices().getDeletedInMainChat()==null||user.getServices().getDeletedInMainChat()) {
                        groupChatBot.unkick(incomingMessage.getChatId());
                        dbService.setDeletedMainChat(user.getUserID(), false);
                    }
                    message.setText("Ссылка на групповой чат: \nhttps://t.me/joinchat/DqG8xUN6_De-fVQ6HXsm4w").enableMarkdown(false);
                }else
                    message.setText("Чтобы получить ссылку на групповой чат купите подписку. \n Участники у которых закончилась подписка удаляются из чата");
                break;
            case SETTINGS:
                message.setText(BotMessages.SETTINGS_MENU.getText());
                message.setReplyMarkup(settingsMenuMarkup);
                break;
            case SITE_ACCOUNT:
                message.setText("сайт в разработке, скоро здесь можно будет получить данные для входа");
                break;
            case REQUISITES:
                String wallet = dbService.getUserFromDb(incomingMessage.getChat().getId()).getAdvcashWallet();
                message.setText("id вашего кошелька Advcash="+wallet
                            +"\nЧтобы сменить, отправьте:"
                            +"\n/acwallet id_кошелька").enableMarkdown(false);
                break;
            case MY_DATA:
                String p = "[\\w]*";
                Pattern pattern = Pattern.compile(p,Pattern.UNICODE_CHARACTER_CLASS);
                String tempFirstName = incomingMessage.getChat().getFirstName();
                System.out.println("получил имя "+tempFirstName);
                String firstName=null;
                try {
                    Matcher matcher = pattern.matcher(tempFirstName);
                    if (tempFirstName!=null&&matcher.matches()) {
                        System.out.println(tempFirstName);
                        firstName = tempFirstName;
                    }
                }catch (Exception e){
                    log.info(" у пользователя"+incomingMessage.getChatId()+" картинка в имени");
                }
                String tempmLastName = incomingMessage.getChat().getLastName();
                String lastName=null;
                try {
                    Matcher matcher = pattern.matcher(tempmLastName);
                    if (tempmLastName!=null&&matcher.matches()) {
                        System.out.println(tempmLastName);
                        lastName = tempmLastName;
                    }
                }catch (Exception e){
                    log.info(" у пользователя"+incomingMessage.getChatId()+" картинка в фамилии");
                }
                String userName = "@"+incomingMessage.getChat().getUserName();
                dbService.updatePersonalData(firstName,lastName,userName,incomingMessage.getChatId());
                user = dbService.getUserFromDb(incomingMessage.getChatId());
                String s = "Ваш username: " + user.getPersonalData().getUserNameTelegram()
                        +"\nВаше имя: " + user.getPersonalData().getFirstName()
                        +"\nВаша фамилия: "+user.getPersonalData().getLastName()
                        +"\nВаш Id: "+user.getUserID();
                message.setText(s).enableMarkdown(false);
                break;
            case PARTNER_PROGRAM:
                message.setText(CommandButtons.PARTNER_PROGRAM.getText());
                message.setReplyMarkup(partnerMenuMarkup);
                break;
            case REQUEST_PAYMENT_BUTTON:
                break;
            case REQUEST_PRIZE_BUTTON:
                break;
            case INVITE_PARTNER:
                message.setText("Чтобы пригласить участника, скопируйте и отправьте ему эту ссылку "
                        + "\n https://t.me/TheNewWaveBot?start=" + incomingMessage.getChat().getId()
                        + "\n");
                break;
            case CHECK_REFERALS:
                user = dbService.getUserFromDb(incomingMessage.getChat().getId());
                int parentLevel = user.getLevel();
                int parentLeftKey = user.getLeftKey();
                int parentRightKey = user.getRightKey();
                String text;
                if (parentLeftKey + 1 == parentRightKey) {
                    text = "У вас нет рефералов";
                } else {
                    List<User> userList = dbService.getChildrenUsers(parentLevel, parentLeftKey, parentRightKey);
                    StringBuilder level1o = new StringBuilder();
                    StringBuilder level1n = new StringBuilder();
                    int countlevel1=0;
                    StringBuilder level2o = new StringBuilder();
                    StringBuilder level2n = new StringBuilder();
                    StringBuilder level3o = new StringBuilder();
                    StringBuilder level3n = new StringBuilder();
                    for (User u : userList) {
                        if (parentLevel + 1 == u.getLevel()) {
                            if (u.getAdvcashTransactions().size()>0){
                                level1o.append(u.getUserName()+"+\n");
                                countlevel1++;
                            }else
                            {
                                level1n.append(u.getUserName()+"\n");
                            }
                        } else if (parentLevel + 2 == u.getLevel()) {
                            if (u.getAdvcashTransactions().size()>0){
                                level2o.append(u.getUserName()+"+\n");
                                countlevel1++;
                            }else
                            {
                                level2n.append(u.getUserName()+"\n");
                            }
                        } else {
                            if (u.getAdvcashTransactions().size()>0){
                                level3o.append(u.getUserName()+"+\n");
                                countlevel1++;
                            }else
                            {
                                level3n.append(u.getUserName()+"\n");
                            }
                        }
                    }
                    text = "*Рефералы 1го уровня:* "
                            + "\n_(оплативших подписку - "+user.getPersonalData().getReferalsForPrize().size()+")_"
                            + "\n" + level1o.toString()
                            + "\n" + level1n.toString()
                            + "\n*Рефералы 2го уровня:* "
                            + "\n" + level2o.toString()
                            +"\n" + level2n.toString()
                            + "\n*Рефералы 3го уровня:* "
                            + "\n" + level3o.toString()
                            + "\n"+ level3n.toString();
                }
                message.setText(text).enableMarkdown(false);
                break;
            case LOCAL_WALLET:
                 user = dbService.getUserFromDb(incomingMessage.getChatId());
                 //проверяем набрал ли рефовод следующие 10 платежей от первой линии
               /* if (user.getPersonalData().getPrize()>0){
                    BigDecimal cash = user.getLocalWallet();
                    String string = "На вашем счету: *"+cash +"*"
                            +"\n"+user.getPersonalData().getReferalsForPrize().size()+" приглашенных вами пользователей оплатили подписку и вам полагается премия 1000$"
                            +"\n\nОставьте заявку, чтобы вывести бонусы  и получить премию на ваш счет."
                            + "\n Проверьте, правильно ли указан ваш кошелек(если нет, смените его в настройках)."
                            + "\n Ваш Advcash:"+user.getAdvcashWallet()
                            + "\n Зявки обрабатываются в конце недели.\n";
                    message.setText(string);
                    message.setReplyMarkup(MenuCreator.createInlineButton(CommandButtons.REQUEST_PRIZE_BUTTON));
                } else {*/
                    BigDecimal cash = user.getLocalWallet();
                    String string = "На вашем счету: *" + cash + "*"
                            + "\n\nОставьте заявку, чтобы вывести бонусы на ваш счет."
                            + "\n Проверьте, правильно ли указан ваш кошелек(если нет, смените его в настройках)."
                            + "\n Ваш Advcash:" + user.getAdvcashWallet()
                            + "\n Баланс должен быть положительным."
                            + "\n Зявки обрабатываются в конце недели.\n";
                    message.setText(string);
                    message.setReplyMarkup(paymentsBonusButton);
               // }
                break;
            default:
                message.setText(BotMessages.DEFAULT.getText());
        }
        return message;
    }

    public EditMessageText callBackContext(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        //готовим сообщение для отправки
        EditMessageText new_message = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(callbackQuery.getMessage().getMessageId());
        String dataFromQuery = callbackQuery.getData();
        //сначала ветвление для данных с параметрами
        //закрыть заявку
        if (dataFromQuery.startsWith(CommandButtons.CLOSE_TASK.getText())){
            Long idTask = null;
            Tasks task=null;
            try {
                idTask = Long.parseLong(dataFromQuery.substring(14));
                //супер метод dbService.closeTask(..,..)
                task = dbService.closeTask(idTask,chatId);
                String s = "";
                if (task.getType().equals(TaskType.PAY_BONUSES))
                    s = "выплата бонусов";
                else if (task.getType().equals(TaskType.PAY_PRIZE))
                    s= "выплата бонусов и премии";
                else if (task.getType().equals(TaskType.PRIVATE_CHAT))
                    s= "аудит портфеля(персональный чат)";
                else
                    throw new TaskTypeException();
                String textToUserMessage = "Выполнена ваша заявка:" +
                        "\nid: "+idTask
                        +"\nТип: "+s;
                SendMessage messageToUser = new SendMessage(task.getClient().getChatID(),textToUserMessage);
                //отправляем уведомление пользователю
                sendApiMethod(messageToUser);
                //возвращаем ответ админу
                System.out.println("возвращаем ответ админу");
                new_message.setText("Заявка "+task.getId()+" закрыта");

            }catch (NumberFormatException e){
                log.error("ошибка при закрытии заявки, не корректный номер " + dataFromQuery);
                new_message.setText("некорректный id  заявки");
            } catch (NoTaskInDb noTaskInDb) {
                log.error("ошибка при закрытии заявки, в базе нет такой заявки "+idTask);
                new_message.setText("в базе нет такой заявки");
            } catch (NoUserInDb noUserInDb) {
                log.error("ошибка при закрытии заявки, нет юзера-менеджера с id="+chatId);
                new_message.setText("Вашего userId нет в базе обратитесь в техподдержку");
            } catch (TelegramApiException e) {
                log.error("Не смог отправить сообщение о закрытии заявки " + task.getClient());
                new_message.setText("Заявка закрыта, но не отправлено уведомление пользователю");
                log.trace(e);
            } catch (AlreadyClosenTask alreadyClosenTask) {
                log.error("Попытка закрыть уже закрытую заявку" + task);
                new_message.setText("Заявка уже была закрыта");
            } catch (TaskTypeException e) {
                log.error("Ошибка в закрытии заявки, не верный тип Task" + task);
                new_message.setText("Ошибка, не верный тип заявки");
            }
        }        //данные без параметров
        else {
            CommandButtons button = CommandButtons.getTYPE(dataFromQuery);
            BigDecimal cashFromLocalWallet= null;
            //сюда запишем пользовательские заявки
            List<Tasks> userTasks = null;
            //сюда запишем открытую заявку
            Tasks userTask = null;
            //будем использовать в проверке кошелька на 0.00
            int checkCash = -1;
            switch (button) {
                case REQUEST_PAYMENT_BUTTON:
                    User user = dbService.getUserFromDb(chatId);
                    System.out.println("достали пользователя " + user);
                    cashFromLocalWallet = user.getPersonalData().getLocalWallet();
                    System.out.println("cash=" + cashFromLocalWallet);
                    userTasks = user.getTasks();
                    userTask = null;
                    System.out.println("проверяем наличие заявки");
                    if (userTasks != null && userTasks.size() > 0) {
                        for (Tasks t : userTasks) {
                            if (t.getStatus().equals(TaskStatus.OPEN) && t.getType().equals(TaskType.PAY_BONUSES))
                                userTask = t;
                        }
                    }
                    checkCash = -1;
                    if (cashFromLocalWallet != null)
                        checkCash = cashFromLocalWallet.compareTo(new BigDecimal("0.01"));
                    System.out.println("checkCash=" + checkCash);
                    String string = "";
                    if (user.getPersonalData().getAdvcashWallet() == null) {
                        System.out.println("Заявка не принята, не установлен кошелёк Advcash.");
                        string = "Заявка не принята, не установлен кошелёк Advcash.";
                    } else if (checkCash == -1) {
                        string = "Заявка не принята, не достаточно средств.";
                        System.out.println(string);
                    } else if (userTask != null) {
                        string = " Вы уже подали заявку " + userTask.getDateTimeOpening();
                        System.out.println(string);
                    } else {
                        userTask = new Tasks(TaskType.PAY_BONUSES, user);
                        dbService.addTask(user.getUserID(), userTask);
                        string = "Заявка принята!";
                        System.out.println(string);
                    }
                    System.out.println(string);
                    new_message.setText(string);
                    break;
                    //запрос на выплату премии
                case REQUEST_PRIZE_BUTTON:
                    user = dbService.getUserFromDb(chatId);
                    System.out.println("достали пользователя " + user);
                    cashFromLocalWallet = user.getPersonalData().getLocalWallet();
                    System.out.println("cash=" + cashFromLocalWallet);
                    userTasks = user.getTasks();
                    userTask = null;
                    System.out.println("проверяем наличие заявки");
                    //ищем открытую заявку
                    if (userTasks != null && userTasks.size() > 0) {
                        for (Tasks t : userTasks) {
                            if (t.getStatus().equals(TaskStatus.OPEN) && t.getType().equals(TaskType.PAY_PRIZE))
                                userTask = t;
                        }
                    }
                    checkCash = -1;
                    if (cashFromLocalWallet != null)
                        checkCash = cashFromLocalWallet.compareTo(new BigDecimal("0.01"));
                    System.out.println("checkCash=" + checkCash);
                    string = "";
                    if (user.getPersonalData().getAdvcashWallet() == null) {
                        System.out.println("Заявка не принята, не установлен кошелёк Advcash.");
                        string = "Заявка не принята, не установлен кошелёк Advcash.";
                    } else if (checkCash == -1) {
                        string = "Заявка не принята, не достаточно средств.";
                        System.out.println(string);
                    } else if (userTask != null) {
                        string = " Вы уже подали заявку " + userTask.getDateTimeOpening();
                        System.out.println(string);
                    } else {
                        userTask = new Tasks(TaskType.PAY_BONUSES, user);
                        dbService.addTask(user.getUserID(), userTask);
                        string = "Заявка принята!";
                        System.out.println(string);
                    }
                    System.out.println(string);
                    new_message.setText(string);
                    break;
                case TASK_PRIVATE_CHAT:
                    user = dbService.getUserFromDb(callbackQuery.getMessage().getChat().getId());
                    userTask = user.getCurrentTasks(TaskType.PRIVATE_CHAT);
                    if (userTask != null) {
                        new_message.setText("У вас уже открыта заявка на персональный чат.\n" + userTask);
                    } else {
                        userTask = new Tasks(TaskType.PRIVATE_CHAT, user);
                        dbService.addTask(user.getUserID(), userTask);
                        List<User> managers = dbService.getManagers();
                        for (User manager : managers) {
                            SendMessage sendMessage = new SendMessage()
                                    .setChatId(manager.getChatID())
                                    .setText("Новая заявка на аудит портфеля:"
                                            + "\nuserName: " + user.getUserName()
                                            + "\nuserId: " + user.getUserID()
                                            + "\nВремя создания: " + userTask.getDateTimeOpening());
                            try {
                                sendApiMethod(sendMessage);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                        new_message.setText("Запрос принят, ждите приглашения в чат");
                    }
                    break;
                default:
                    new_message.setText("Что то пошло не так обратитесь в тех. поддрежку");
                    break;
            }
        }
        return new_message;
    }

    public SendMessage adminCommandContext(Message incomingMessage) {
        SendMessage replyMessage = new SendMessage()
                .setChatId(incomingMessage.getChatId())
                .setText("неизвестная команда");
        Boolean isAdmin = dbService.getUserFromDb(incomingMessage.getChatId()).getTypeUser().equals("manager");
        String textIncomingMessage = incomingMessage.getText();
        //отправить сигнал
        if (textIncomingMessage.startsWith(CommandButtons.SEND_SIGNAL.getText())&&isAdmin){
            String textSignal = textIncomingMessage.substring(8);
            dbService.persistSignal(textSignal);
            int count = 0;
                List<Long> usersId = dbService.getSubscribers();
                if (usersId != null && usersId.size() > 0) {
                    for (Long uId : usersId) {
                        if (uId>100) {
                            SendMessage sendMessage = new SendMessage()
                                    .setChatId(uId)
                                    .setText(textIncomingMessage.substring(8));
                            try {
                                sendApiMethod(sendMessage);
                                Thread.sleep(45l);
                                count++;
                            } catch (TelegramApiException e) {
                                log.error("Не смог отправить сигнал "+uId);
                            } catch (InterruptedException e) {
                                log.error("ошибка в потоке при отправке сообщений");
                                log.trace(e);
                            }
                        }
                    }
                    replyMessage.setText("Сигнал отправлен " + count + " пользователям");
                }

          //сменить кошелек
        } else if (textIncomingMessage.startsWith("/сообщение ")&&isAdmin){
            System.out.println(textIncomingMessage);
            Long userId = Long.parseLong(textIncomingMessage.substring(11,textIncomingMessage.indexOf("-")));
            System.out.println(userId);
            String text = textIncomingMessage.substring(textIncomingMessage.indexOf("-")+1);
            System.out.println(text);
            SendMessage sendMessage = new SendMessage(userId,text).enableMarkdown(false);
            try {
                System.out.println("отправляем сообщение");
                sendApiMethod(sendMessage);
                System.out.println("отправил");
                replyMessage.setText("сообщение для userId="+userId+"отправлено");
            } catch (TelegramApiException e) {
                replyMessage.setText("сообщение для userId="+userId+"НЕ отправлено");
                e.printStackTrace();
            }
        }//сменить кошелек
        else if(textIncomingMessage.startsWith(CommandButtons.CHANGE_AC_WALLET.getText())) {
            String wallet = textIncomingMessage.substring(10);
            dbService.getUserFromDb(incomingMessage.getChat().getId())
                    .getPersonalData().setAdvcashWallet(wallet);
            replyMessage.setText("Кошелек id="+wallet+" установлен");
          //вывести заявки на чат
        } else if(textIncomingMessage.equals(CommandButtons.CHECK_PRIVATE_CHAT.getText())){
            List<Tasks> tasks = dbService.getTasks(TaskStatus.OPEN,TaskType.PRIVATE_CHAT);
            if (tasks!=null&&tasks.size()>0){
                for (Tasks t : tasks){
                    SendMessage message = new SendMessage(incomingMessage.getChatId(),t.toString());
                    message.setReplyMarkup(MenuCreator.createCloseTaskButton(t.getId()));
                    try {
                        sendApiMethod(message);
                    } catch (TelegramApiException e) {
                        log.error("ошибка в запросе заявок, не смог отправить заявку админу "+incomingMessage.getChatId());
                        log.trace(e);
                    }
                }
                replyMessage.setText("*********");
            } else {
                replyMessage.setText("Заявок нет");
            }
          //вывести заявки на выплату бонусов
        } else if(textIncomingMessage.equals(CommandButtons.CHECK_TASKS_PAYMENT.getText())&&isAdmin){
            List<Tasks> tasks = dbService.getTasks(TaskStatus.OPEN,TaskType.PAY_BONUSES);
            if (tasks!=null&&tasks.size()>0){
                for (Tasks t : tasks){
                    String s = ""+t
                            +"\nБонусы: "+t.getClient().getPersonalData().getLocalWallet()
                            +"\nAC кошелёк: "+t.getClient().getPersonalData().getAdvcashWallet();
                    SendMessage message = new SendMessage(incomingMessage.getChatId(),s);
                    message.setReplyMarkup(MenuCreator.createCloseTaskButton(t.getId()));
                    try {
                        sendApiMethod(message);
                    } catch (TelegramApiException e) {
                        log.error("ошибка в запросе заявок, не смог отправить заявку админу "+incomingMessage.getChatId());
                        log.trace(e);
                    }
                    replyMessage.setText("******");
                }
            } else {
                replyMessage.setText("заявок нет");
            }
          //заявки на премии
        } else if (textIncomingMessage.equals(CommandButtons.CHECK_TASK_PRIZE.getText())){
            List<Tasks> tasks = dbService.getTasks(TaskStatus.OPEN,TaskType.PAY_PRIZE);
            if (tasks!=null&&tasks.size()>0){
                for (Tasks t : tasks){
                    String s = ""+t
                            +"\nБонусы: "+t.getClient().getPersonalData().getLocalWallet()
                            +"\nAC кошелёк: "+t.getClient().getPersonalData().getAdvcashWallet();
                    SendMessage message = new SendMessage(incomingMessage.getChatId(),s);
                    message.setReplyMarkup(MenuCreator.createCloseTaskButton(t.getId()));
                    try {
                        sendApiMethod(message);
                    } catch (TelegramApiException e) {
                        log.error("ошибка в запросе заявок, не смог отправить заявку юзеру "+incomingMessage.getChatId());
                        log.trace(e);
                    }
                    replyMessage.setText("******");
                }
            } else {
                replyMessage.setText("заявок нет");
            }
        }
        //предоставить меню админов
        else if (textIncomingMessage.equals(CommandButtons.SET_MENEGERS_MENU.getText())&&isAdmin){
            replyMessage.setText("меню");
            replyMessage.setReplyMarkup(MenuCreator.createAdminMenuMarkup());
        }
        //дабавить юзера юзеру
        if (textIncomingMessage.startsWith("/adduser")){
            Long id = 301363342l;
            User user1 = new User(11,"@null","testuser11",null,11);
            User user2 = new User(12,"@null","testuser12",null,12);
            User user3 = new User(13,"@null","testuser13",null,13);
            User user4 = new User(14,"@null","testuser14",null,14);
            User user5 = new User(15,"@null","testuser15",null,15);
            User user6 = new User(16,"@null","testuser16",null,16);
            User user7 = new User(17,"@null","testuser17",null,17);
            User user8 = new User(18,"@null","testuser18",null,18);
            User user9 = new User(19,"@null","testuser19",null,19);
            User user10 = new User(20,"@null","testuser20",null,20);
            try {
                dbService.addChildrenUser(id,user1);
                dbService.addChildrenUser(id,user2);
                dbService.addChildrenUser(id,user3);
                dbService.addChildrenUser(id,user4);
                dbService.addChildrenUser(id,user5);
                dbService.addChildrenUser(id,user6);
                dbService.addChildrenUser(id,user7);
                dbService.addChildrenUser(id,user8);
                dbService.addChildrenUser(id,user9);
                dbService.addChildrenUser(id,user10);
            } catch (NoUserInDb noUserInDb) {
                noUserInDb.printStackTrace();
            }
        }
        return replyMessage;
    }

    private BotApiMethod PrivateGroupContext(Message incomingMessage) {
        String texOfMessage = incomingMessage.getText();
        CommandButtons button = CommandButtons.getTYPE(texOfMessage);
        SendMessage sendMessage = new SendMessage(incomingMessage.getChatId(),"_");
        switch (button){
            case END_TASK:
                KickChatMember kick = new KickChatMember(incomingMessage.getChatId(),301363342);
                try {
                    sendApiMethod(kick);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                sendMessage = new SendMessage(incomingMessage.getChatId(),"заявка закрыта");
                break;
        }
        return sendMessage;
    }



    @Override
    public String getBotUsername() {
        return GlobalConfigs.botname;
    }

    @Override
    public String getBotToken() {
        return GlobalConfigs.token;
    }

    @Override
    public String getBotPath() {
        return GlobalConfigs.botPath;
    }



}
