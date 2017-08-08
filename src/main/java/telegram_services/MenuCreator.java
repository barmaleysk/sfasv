package telegram_services;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuteynikov on 14.07.2017.
 */
public class  MenuCreator {
    public synchronized static ReplyKeyboardMarkup createMainMenuMarkup(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(CommandButtons.OFORMIT_PODPISCU.getText()));
       // keyboardRow1.add(new KeyboardButton(CommandButtons.SHOW_SIGNAL.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(CommandButtons.INFO_BOT.getText()));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton(CommandButtons.PARTNER_PROGRAM.getText()));
        KeyboardRow keyboardRow4 = new KeyboardRow();
        keyboardRow4.add(new KeyboardButton(CommandButtons.SETTINGS.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        keyboardRows.add(keyboardRow4);
        keyboardMarkup.setKeyboard(keyboardRows);
        // mainMenuMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public synchronized static ReplyKeyboardMarkup createSubscripMenuMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(CommandButtons.ONE_MONTH.getText()));
        keyboardRow1.add(new KeyboardButton(CommandButtons.TWO_MONTH.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(CommandButtons.THREE_MONTH.getText()));
        keyboardRow2.add(new KeyboardButton(CommandButtons.PRIVATE_CHAT.getText()));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        //keyboardRow3.add(new KeyboardButton(CommandButtons.UNLIMIT.getText()));
        keyboardRow3.add(new KeyboardButton(CommandButtons.INVITE_TO_CHAT.getText()));
        keyboardRow3.add(new KeyboardButton(CommandButtons.CHECK_SUBSCRIPTION.getText()));
        KeyboardRow keyboardRow4 = new KeyboardRow();
        keyboardRow4.add(new KeyboardButton(CommandButtons.BACK_IN_MAIN_MENU.getText()));
        //keyboardRow4.add(new KeyboardButton(CommandButtons.INVITE_TO_CHAT.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        keyboardRows.add(keyboardRow4);
        keyboardMarkup.setKeyboard(keyboardRows);
        //subscripMenuMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public  synchronized static ReplyKeyboardMarkup createInfoMenuMarkup(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(CommandButtons.GENERAL_DESCRIPTION.getText()));
        keyboardRow1.add(new KeyboardButton(CommandButtons.FAQ.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        //keyboardRow2.add(new KeyboardButton(CommandButtons.HOW_TO_CHANGE_CURRENCY.getText()));
        keyboardRow2.add(new KeyboardButton(CommandButtons.SUPPORT.getText()));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton(CommandButtons.BACK_IN_MAIN_MENU.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        keyboardMarkup.setKeyboard(keyboardRows);
        //infoMenuMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public synchronized static ReplyKeyboardMarkup createSettingsMenuMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(CommandButtons.REQUISITES.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(CommandButtons.SITE_ACCOUNT.getText()));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton(CommandButtons.MY_DATA.getText()));
        KeyboardRow keyboardRow4 = new KeyboardRow();
        keyboardRow4.add(new KeyboardButton(CommandButtons.BACK_IN_MAIN_MENU.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        keyboardRows.add(keyboardRow4);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public synchronized static ReplyKeyboardMarkup createPartnersMenu(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(CommandButtons.LOCAL_WALLET.getText()));
        keyboardRow1.add(new KeyboardButton(CommandButtons.INVITE_PARTNER.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(CommandButtons.BACK_IN_MAIN_MENU.getText()));
        //keyboardRow2.add(new KeyboardButton(CommandButtons.SET_REFER.getText()));
        keyboardRow2.add(new KeyboardButton(CommandButtons.CHECK_REFERALS.getText()));
        //KeyboardRow keyboardRow3 = new KeyboardRow();
       // keyboardRow3.add(new KeyboardButton(CommandButtons.BACK_IN_MAIN_MENU.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        //keyboardRows.add(keyboardRow3);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public synchronized static ReplyKeyboardMarkup createAdminMenuMarkup(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(CommandButtons.CHECK_PRIVATE_CHAT.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(CommandButtons.CHECK_TASKS_PAYMENT.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public synchronized static InlineKeyboardMarkup createTrialInlineButton(){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText(CommandButtons.SET_TRIAL.getText()).setCallbackData(CommandButtons.SET_TRIAL.getText()));
        //List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        //rowInline2.add(new InlineKeyboardButton().setText("2 месяца").setCallbackData("2month"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public synchronized static InlineKeyboardMarkup createPayButton(String parameters){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton()
                .setText(CommandButtons.PAY_BUTTOM.getText())
                .setUrl(CommandButtons.URL_FORM_FOR_AC.getText()+"?"+parameters));
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public synchronized static InlineKeyboardMarkup createPaymentsBonusButton(){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton()
                .setText(CommandButtons.REQUEST_PAYMENT_BUTTON.getText())
                .setCallbackData(CommandButtons.REQUEST_PAYMENT_BUTTON.getText()));
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public synchronized static InlineKeyboardMarkup createInlineButton(CommandButtons commandButtons){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton()
                .setText(commandButtons.getText())
                .setCallbackData(commandButtons.getText()));
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }


    public synchronized static InlineKeyboardMarkup createCloseTaskButton(long idTask) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton()
                .setText(CommandButtons.CLOSE_TASK.getText())
                .setCallbackData(CommandButtons.CLOSE_TASK.getText()+idTask));
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}
