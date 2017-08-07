package telegram_services;

import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.groupadministration.UnbanChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class GroupChatBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("пришло обновление" + update.getMessage().getChatId());
        //UnbanChatMember unbanChatMember = new UnbanChatMember(-1001132133431l,incomingMessage.getChatId().intValue());
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
            e.printStackTrace();
        }
    }

    public void getChatMember(){

    }


    public synchronized void kick(Long id) throws TelegramApiException {
        KickChatMember kickChatMember = new KickChatMember(-1001132133431l,id.intValue());
        sendApiMethod(kickChatMember);
    }
}
