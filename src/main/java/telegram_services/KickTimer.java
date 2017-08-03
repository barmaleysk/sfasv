package telegram_services;

import database_service.DbService;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;
import java.util.TimerTask;

public class KickTimer extends TimerTask {
    private static final Logger log = Logger.getLogger(KickTimer.class);
    private GroupChatBot groupChatBot;
    public KickTimer(TelegramLongPollingBot groupChatBot) {
        this.groupChatBot = (GroupChatBot) groupChatBot;
    }

    @Override
    public void run() {
        log.info("===kick timer started===");
        List<Long> usersId = DbService.getInstance().getUnSubscriptionUsers();
        log.info("Количество пользователей для удаления="+usersId.size());
        int countUsers=0;
        for (Long id : usersId){
            try {
                groupChatBot.kick(id);
                countUsers++;
            } catch (TelegramApiException e) {
                log.info("не удалось кикнуть юзера id"+id);
            }
        }
        log.info("кинул "+countUsers+" юзеров");
        log.info("===kick timer stoped===");
    }
}

