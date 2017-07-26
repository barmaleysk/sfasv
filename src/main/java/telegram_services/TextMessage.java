package telegram_services;

/**
 * Created by kuteynikov on 30.06.2017.
 */
public enum TextMessage {
    WELCOME("Добро пожаловать, "),
    SET_REFER("Чтобы установить рефера введите: " +
            "\n/refer 'id рефера'"),
    COMMAND_ERROR("Ошибка: неверные данные"),
    REFER_SETTED("рефер установлен")
    ;
    private String message;

     TextMessage(String message) {
        this.message = message;
    }

    public String getText() {
        return message;
    }
}
