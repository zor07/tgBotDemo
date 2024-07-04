package com.zor07.tgdemo.service.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.tgdemo.entity.Client;
import com.zor07.tgdemo.enums.RegistrationState;
import com.zor07.tgdemo.service.bot.model.ReminderData;
import com.zor07.tgdemo.service.ClientService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReminderBot implements LongPollingSingleThreadUpdateConsumer {

    private static final AtomicLong REMINDER_ID = new AtomicLong(0L);
    private final TelegramClient telegramClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Long, String> reminders = new HashMap<>();
    private final ClientService clientService;


    public ReminderBot(TelegramClient telegramClient,
                       ClientService clientService) {
        this.telegramClient = telegramClient;
        this.clientService = clientService;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            SendMessage message;

            Optional<Client> clientOptional = clientService.getByChatId(chatId);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                RegistrationState registrationState = RegistrationState.valueOf(client.getRegistrationState());
                switch (registrationState) {
                    case ASK_NAME: {
                        String name = messageText;
                        client.setName(name);
                        client.setRegistrationState(RegistrationState.ASK_BIRTHDATE.name());
                        message = createTextMessage(
                                String.format("Приятно познакомиться! Введи плз дату рождения в формате DD.MM.YYYY"),
                                chatId
                        );
                        break;
                    }
                    case ASK_BIRTHDATE: {
                        String dateString = messageText;
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            LocalDate birthDate = LocalDate.parse(dateString, formatter);
                            client.setBirthday(birthDate);
                            client.setRegistrationState(RegistrationState.ASK_EMAIL.name());
                            message = createTextMessage(
                                    "Круто! Осталось ввести только твой email ;)", chatId
                            );
                        } catch (DateTimeParseException e) {
                            message = createTextMessage(
                                    "Воу... Чет не очень похоже на дату...Попробуй еще раз в формате DD.MM.YYYY", chatId
                            );
                        }
                        break;
                    }
                    case ASK_EMAIL: {
                        String email = messageText;
                        if (isValidEmail(email)) {
                            client.setEmail(email);
                            client.setRegistrationState(RegistrationState.REGISTERED.name());
                            message = createTextMessage(
                                    "Все! Теперь мы знакомы. Если надо что-то напомнить - просто чиркани, лапуля", chatId
                            );
                        } else {
                            message = createTextMessage(
                                    "То, что ты ввел не очень похоже на мыло( Давай еще разок...", chatId
                            );
                        }
                        break;

                    }
                    case REGISTERED: {
                        if ("/start".equals(messageText)) {
                            message = createTextMessage(
                                    "Привет! Я помогу тебе не забыть важные вещи. Просто напиши о чем тебе нужно напомнить.",
                                    chatId
                            );

                        } else {
                            Long id = nextReminderId();
                            reminders.put(id, messageText);
                            message = SendMessage // Создаем объект сообщения
                                    .builder()
                                    .chatId(chatId)
                                    .text("Через сколько напомнить?")
                                    .replyMarkup(InlineKeyboardMarkup.builder()
                                            .keyboardRow(new InlineKeyboardRow(
                                                    reminderButton(10, id),
                                                    reminderButton(20, id),
                                                    reminderButton(30, id)
                                            ))
                                            .build())
                                    .build();
                        }
                        break;
                    }
                    default: {
                        message = createTextMessage("Какая-то ошибка, попробуй перезапустить бота", chatId);
                    }
                }
                if (registrationState != RegistrationState.REGISTERED) {
                    clientService.save(client);
                }

            } else {
                Client client = new Client();
                client.setChatId(chatId);
                client.setRegistrationState(RegistrationState.ASK_NAME.name());
                clientService.save(client);
                message = createTextMessage("Давайте познакомимся, я бот, а вас как зовут?", chatId);
            }

            try {
                telegramClient.execute(message); // Отправляем сообщение через ТГ-клиента
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callbackData = update.getCallbackQuery().getData();
            ReminderData reminderData = jsonToReminder(callbackData);
            String reminderText = reminders.get(reminderData.getId());

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SendMessage reminderMessage = createTextMessage(
                            String.format("Напомнить: %s", reminderText),
                            chatId
                    );
                    try {
                        telegramClient.execute(reminderMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }, reminderData.getSec() * 1000L);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private SendMessage createTextMessage(String text, Long chatId) {
        return SendMessage // Создаем объект сообщения
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    private Long nextReminderId() {
        return REMINDER_ID.incrementAndGet();
    }

    private ReminderData jsonToReminder(String json) {
        try {
            return objectMapper.readValue(json, ReminderData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String reminderToJson(ReminderData reminderData) {
        try {
            return objectMapper.writeValueAsString(reminderData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private InlineKeyboardButton reminderButton(Integer seconds, Long id) {
        String data = reminderToJson(new ReminderData(seconds, id));
        return InlineKeyboardButton
                .builder()
                .text(String.format("%s секунд", seconds))
                .callbackData(data)
                .build();
    }
}
