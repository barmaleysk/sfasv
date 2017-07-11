package telegram_services;

/**
 * Created by kuteynikov on 07.07.2017.
 */
public enum CommandButtons {
    START("/start"),
    //main menu
    OFORMIT_PODPISCU("Оформить подписку"),
    INFO_BOT("Информация"),
    SETTINGS("Параметры"),
    //subscripe menu
    ONE_MONTH("30 дней = 100р"),
    TWO_MONTH("60 дней = 180р"),
    THREE_MONTH("90 дней = 240р"),
    CHECK_SUBSCRIPTION("Проверить подписку"),
    BACK_IN_MAIN_MENU("<- Вернутся в главное меню"),
    //info menu
    GENERAL_DESCRIPTION("Общее описание"),
    FAQ("FAQ"),
    HOW_TO_CHANGE_CURRENCY("Как обменять криптовалюту"),
    SUPPORT("Тех поддержка"),
    //settings menu
    REQUISITES("Добавить платежные реквезиты"),
    PARTNER_PROGRAM("Партнёрская программа"),
    ADD_REFERAL("Прикрутить рефера"),
    //partners menu
    CHECK_REFERALS("Посмотреть рефералов"),
    //trial buttom
    SET_TRIAL("Активировать на 2 дня"),
    //FAIL
    FAIL("Я пока не знаю что на это ответить")
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
