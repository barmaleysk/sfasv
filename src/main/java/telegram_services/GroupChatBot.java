package telegram_services;

import database_service.DbService;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.groupadministration.UnbanChatMember;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;

public class GroupChatBot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(GroupChatBot.class);
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("пришло обновление" + update.getMessage().getChatId());
        //UnbanChatMember unbanChatMember = new UnbanChatMember(-1001132133431l,incomingMessage.getChatId().intValue());
        if (update.getMessage().isSuperGroupMessage()){
            System.out.println(update.getMessage().getText());
            List<User> users = update.getMessage().getNewChatMembers();
            System.out.println("количество NewChatMembers"+users.size());
            KickChatMember kickChatMember;
            if (users!=null&&users.size()>0) {
                DbService dbService = DbService.getInstance();
                for (User u : users) {
                    Long id = u.getId().longValue();
                    System.out.println("userid=" + id);
                    entitys.User userFromDb = dbService.getUserFromDb(id);
                    if (userFromDb != null && userFromDb.getServices().getEndDateOfSubscription().toLocalDate().isAfter(LocalDate.now()) || userFromDb.getServices().getUnlimit()) {
                        System.out.println("добавился хороший пользователь в MainСhat ");
                    }else {
                        kickChatMember = new KickChatMember(update.getMessage().getChatId(), id.intValue());
                        try {
                            sendApiMethod(kickChatMember);
                            log.info("кикнут "+u);
                            if (userFromDb!=null){
                                dbService.setDeletedMainChat(userFromDb.getUserID(),true);
                            }
                        } catch (TelegramApiException e) {
                            log.info("не удалось кикнуть "+u+" "+u);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "New_Wave_bot";
    }

    @Override
    public String getBotToken() {
        return "439174667:AAEHo-Wgm8u0WI4jlqmHtyC2snx8_m2WLyc";
    }

    public synchronized void unkick(Long chatId) {
        UnbanChatMember unbanChatMember = new UnbanChatMember(-1001132133431l,chatId.intValue());
        try {
            sendApiMethod(unbanChatMember);
        } catch (TelegramApiException e) {
            System.out.println("не смог кикнуть "+chatId);
        }
    }

    public void getChatMember(){

    }


    public synchronized void kick(Long id) throws TelegramApiException {
        KickChatMember kickChatMember = new KickChatMember(-1001132133431l,id.intValue());
        sendApiMethod(kickChatMember);
    }
}
