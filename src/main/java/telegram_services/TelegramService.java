package telegram_services;

import org.telegram.telegrambots.api.methods.send.SendMessage;

/**
 * Created by kuteynikov on 14.07.2017.
 */
public interface TelegramService {
    public void messageSend(SendMessage message);
}
