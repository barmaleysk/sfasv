package telegram_services;

/**
 * Created by kuteynikov on 07.07.2017.
 */
public enum CommandButtons {
    START("/start"),
    //main menu
    OFORMIT_PODPISCU("Оформить подписку"),
    INFO_BOT("Информация"),
    PARTNER_PROGRAM("Партнёрская программа"),
    SETTINGS("Параметры"),
    //subscripe menu
    ONE_MONTH("1 месяц"),
    TWO_MONTH("2 месяца"),
    THREE_MONTH("3 месяца"),
    CHECK_SUBSCRIPTION("Проверить подписку"),
    BACK_IN_MAIN_MENU("<- Главное меню"),
    PRIVATE_CHAT("Аудит портфеля"),
    UNLIMIT("VIP подписка"),
    PAY_BUTTOM("оплатить"),
    URL_FORM_FOR_AC("http://290193.msk-kvm.ru/redirectToAdvcash"),
    TASK_PRIVATE_CHAT("запросить консультацию"),
    //info menu
    GENERAL_DESCRIPTION("О нас"),
    FAQ("FAQ"),
    HOW_TO_CHANGE_CURRENCY("Как обменять криптовалюту"),
    SUPPORT("Тех поддержка"),
    //settings menu
    REQUISITES("настроить кошелек AdvCash"),
    SITE_ACCOUNT("Аккаунт для сайта"),
    //partners menu
    LOCAL_WALLET("мои бонусы"),
    REQUEST_PAYMENT_BUTTON("запросить выплату бонусов"),
    INVITE_PARTNER("Пригласить"),
    CHECK_REFERALS("Посмотреть рефералов"),
    SET_REFER("Установить моего рефера"),
    //trial buttom
    SET_TRIAL("Активировать на 2 дня"),
    //FAIL
    FAIL("Я пока не знаю что на это ответить"),
    //admin command
    SET_MENEGERS_MENU("/управление"),
    CHECK_SUBSRIPTIONS("/подписчики"),
    CHECK_TASKS_PAYMENT("/заявки на выплаты"),
    CHECK_PRIVATE_CHAT("/заявки на чат"),
    SEND_SIGNAL("/сигнал"),
    //user command
    CHANGE_AC_WALLET("/acwallet ")
    ;

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
