package telegram_services;

/**
 * Created by kuteynikov on 30.06.2017.
 */
public enum TelegramCommands {
    START("/start"),
    STOP("/stop")
    ;
    private String commands;

    TelegramCommands(String commands) {
        this.commands = commands;
    }
}
