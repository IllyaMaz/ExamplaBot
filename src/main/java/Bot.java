import entity.Currency;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.CurrencyModeService;
import service.CurrencyService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import service.DaoCurrency;

public class Bot extends TelegramLongPollingBot {
    CurrencyService service = new CurrencyService();
    DaoCurrency daoCurrency = new DaoCurrency();
    CurrencyModeService currencyModeService = CurrencyModeService.getInstance();
    @Override
    public String getBotUsername() {
        return "UckraineCurrencyBot";
    }

    @Override
    public String getBotToken() {
        return "5562043722:AAFsXBHaMocrVZN7f6YQAg4n4bTvE1h5wBg";
    }

    @Override
    public void onUpdateReceived(Update update) {
      if (update.hasMessage() && update.getMessage().hasEntities()){
          try {
              handleMessage(update.getMessage());
          } catch (TelegramApiException e) {
              e.printStackTrace();
          }
      } else if (update.hasCallbackQuery()){
          try {
              handlCallback(update.getCallbackQuery());
          } catch (TelegramApiException e) {
              e.printStackTrace();
          }
      }
    }

    private void handlCallback(CallbackQuery callbackQuery) throws TelegramApiException {
        Message message = callbackQuery.getMessage();
        String data = callbackQuery.getData();
        switch (data){
            case "course" :
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(getCourse(currencyModeService.getTargetCurrency(message.getChatId())))
                        .build());

                break;
            case "settings" :
                List<List<InlineKeyboardButton>> button = new ArrayList<>();
                button.add(Arrays.asList(InlineKeyboardButton.builder().text("Валюты").callbackData("currency").build()));
                button.add(Arrays.asList(InlineKeyboardButton.builder().text("Банк").callbackData("bank").build()));
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Настройки")
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(button).build())
                        .build());
                break;
            case "currency conversion" :

                break;
            case "currency" :
                List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                for (Currency currency : Currency.values()) {
                    buttons.add(Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(currency.toString())
                                    .callbackData(currency.toString())
                                    .build()
                    ));
                }
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Выберите валюту")
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                        .build());
                break;
            case "USD" :
            case "EUR" :
            case "RUB" :
                currencyModeService.setTargetCurrency(message.getChatId(), Currency.valueOf(callbackQuery.getData()));
                break;
            case "bank" :

                break;

        }

    }

    private void handleMessage(Message message) throws TelegramApiException {

        if (message.hasText() && message.hasEntities()){
            Optional<MessageEntity> commandEntities = message.getEntities()
                    .stream()
                    .filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntities.isPresent()){
                String command = message
                        .getText()
                        .substring(commandEntities.get().getOffset(), commandEntities.get().getLength());
                switch (command){
                    case "/start" :
                        List<List<InlineKeyboardButton>> button = new ArrayList<>();
                        button.add(Arrays.asList(InlineKeyboardButton.builder().text("Курс").callbackData("course").build()));
                        button.add(Arrays.asList(InlineKeyboardButton.builder().text("Настройки").callbackData("settings").build()));
                        button.add(Arrays.asList(InlineKeyboardButton.builder().text("Конвертация валюты").callbackData("currency conversion").build()));
                        execute(SendMessage.builder()
                                .chatId(message.getChatId().toString())
                                .text("Добро пожаловать!\n" +
                                        "Этот бот поможет отслеживать актуальные курсы валют\n" +
                                        "и конвертировать валюту")
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(button).build())
                                .build());
                                break;
                }

            }


        }
    }

    private String getCourse(Currency target){
        String result = currencyModeService.getOriginalCurrency() + "/" + target;
        DaoCurrency daoCurrency = null;
        try {
            daoCurrency = service.getCurrency(target);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result += "\n" + "Покупка : " + daoCurrency.getSale();
        result += "\n" + "Продажа : " + daoCurrency.getBuy();
        return result;

    }

}
