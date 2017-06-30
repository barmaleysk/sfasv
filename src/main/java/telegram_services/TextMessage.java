package telegram_services;

/**
 * Created by kuteynikov on 30.06.2017.
 */
public enum TextMessage {
    WELCOME("Добро пожаловать, ")
    ;
    private String message;

     TextMessage(String message) {
        this.message = message;
    }
}
