package telegram_services;

/**
 * Created by kuteynikov on 07.07.2017.
 */
public enum CommandButtons {
    START("/start"),
    //main menu
    SHOW_SIGNAL("Сигналы"),
    OFORMIT_PODPISCU("Услуги"),
    INFO_BOT("Информация"),
    PARTNER_PROGRAM("Партнёрская программа"),
    SETTINGS("Параметры"),
    //subscripe menu
    ONE_MONTH("Подписка на 2 дня"),
    TWO_MONTH("Подписка на 4 дня"),
    THREE_MONTH("Подписка на 7 дней"),
    CHECK_SUBSCRIPTION("Проверить подписку"),
    BACK_IN_MAIN_MENU("<- Главное меню"),
    PRIVATE_CHAT("Аудит портфеля"),
    UNLIMIT("VIP подписка"),
    PAY_BUTTOM("оплатить"),
    URL_FORM_FOR_AC("http://new-wave.io/redirectToAdvcash"),
    TASK_PRIVATE_CHAT("запросить консультацию"),
    INVITE_TO_CHAT("Общий чат"),
    //info menu
    GENERAL_DESCRIPTION("О нас"),
    FAQ("FAQ"),
    HOW_TO_CHANGE_CURRENCY("Как обменять криптовалюту"),
    SUPPORT("Тех поддержка"),
    //settings menu
    REQUISITES("Настроить кошелек AdvCash"),
    SITE_ACCOUNT("Аккаунт для сайта"),
    MY_DATA("Мои данные"),
    //partners menu
    LOCAL_WALLET("Мои бонусы"),
    REQUEST_PAYMENT_BUTTON("запросить выплату бонусов"),
    REQUEST_PRIZE_BUTTON("выплата бонусов и премии"),
    INVITE_PARTNER("Пригласить"),
    CHECK_REFERALS("Посмотреть рефералов"),
    SET_REFER("Установить моего рефера"),
    //trial buttom
    SET_TRIAL("Активировать на 2 дня"),
    //FAIL
    FAIL("Я пока не знаю что на это ответить"),
    //admin command
    SET_MENEGERS_MENU("/admin"),
    CHECK_SUBSRIPTIONS("/подписчики"),
    CHECK_TASKS_PAYMENT("/заявки на выплаты"),
    CHECK_PRIVATE_CHAT("/заявки на чат"),
    CHECK_TASK_PRIZE("/заявки на премию"),
    SEND_SIGNAL("/сигнал"),
    CLOSE_TASK("Закрыть заявку"),
    HADLE_TASK("Взять в работу"),
    //user command
    CHANGE_AC_WALLET("/acwallet ");

    public String getText() {
        return s;
    }

    private String s;

    CommandButtons(String s) {
        this.s = s;
    }

    public static CommandButtons getTYPE(String s){
        CommandButtons type = FAIL;
        for (CommandButtons tempTYPE : CommandButtons.values()){
            if (s.equals(tempTYPE.getText()))
                type = tempTYPE;
        }
        return type;
    }
}
