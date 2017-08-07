package telegram_services;

import database_service.DbService;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimerTask;

public class KickTimer extends Thread {
    private static final Logger log = Logger.getLogger(KickTimer.class);
    private GroupChatBot groupChatBot;
    public KickTimer(TelegramLongPollingBot groupChatBot) {
        this.groupChatBot = (GroupChatBot) groupChatBot;
    }

    @Override
    public void run() {
        LocalTime time1=LocalTime.of(02,05,00);
        LocalTime time2=LocalTime.of(03,00,00);

        while (true) {
            if (LocalTime.now().isAfter(time1) && LocalTime.now().isBefore(time2)) {
                log.info("===kick timer started===");
                List<Long> usersId = DbService.getInstance().getUnSubscriptionUsers();
                log.info("Количество пользователей для удаления=" + usersId.size());
                int countUsers = 0;
                for (Long id : usersId) {
                    try {
                        groupChatBot.kick(id);
                        countUsers++;
                        Thread.sleep(4000l);
                    } catch (TelegramApiException e) {
                        log.info("не удалось кикнуть юзера id" + id);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("кинул " + countUsers + " юзеров");
                log.info("===kick timer stoped===");
            }
            try {
                Thread.sleep(1800000l);
            } catch (InterruptedException e) {
                log.trace(e);
            }
        }
    }
}

