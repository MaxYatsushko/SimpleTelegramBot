package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private final NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }
    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            generateWelcomeMessage(updates);
            createNotificationTaskRecord(updates);
            executeNotificationTaskOnScheduledTime();
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    /*
    Method generates message via direct command "/start"
       parameters:
            List<Update> - list of messages from telegram
       return void
    */
    private void generateWelcomeMessage(List<Update> updates) {

        updates
                .stream()
                .map(update -> update.message())
                .filter(message -> message.text().equals("/start"))
                .map(message -> message.chat())
                .forEach(chat -> {
                    SendMessage message = new SendMessage(chat.id(), "Добро пожаловать "  + chat.firstName() + ". Я бот Gena. Я вывожу запланированные задачи. Введите задачу в формате 'дд.мм.гггг чч:мм Текст задачи на русском'");
                    SendResponse response = telegramBot.execute(message);
                });
    }

    /*
    Method creates new record of the notification task in DB from the message according to the pattern
       parameters:
            List<Update> - list of messages from telegram
       return void
    */
    private void createNotificationTaskRecord(List<Update> updates) {
        updates
                .stream()
                .map(Update::message)
                .filter(message -> (isMessageMatchToPattern(message.text())))
                .forEach(message -> notificationTaskService.createNotificationTask(ParseMessageTextAndCreateNotificationTask(message)));
    }

    /*
    Method check the text of message matches to the pattern
       parameters:
            String - text of message
       return boolean - success
     */
    private boolean isMessageMatchToPattern(String text) {
        Matcher matcher = PATTERN.matcher(text);
        return matcher.matches();
    }

    /*
   Method parses the text of message and creates object of NotificationTask
       parameters:
            Message - message from telegram
       return NotificationTask - created object
    */
    private NotificationTask ParseMessageTextAndCreateNotificationTask(Message message) {
        String text = message.text();
        Matcher matcher = PATTERN.matcher(text);

        String date = null;
        String task = null;
        if (matcher.matches()) {
            date = matcher.group(1);
            task = matcher.group(3);
        }
        LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        NotificationTask notificationTask = new NotificationTask();
        notificationTask.setNotificationDate(localDateTime);
        notificationTask.setText(task);
        notificationTask.setIdChat(message.chat().id());

        return notificationTask;
    }

    /*
   Method executes send notification by schedule from DB
       parameters:
       return void
    */
    @Scheduled(cron = "0 0/1 * * * *")
    private void executeNotificationTaskOnScheduledTime() {
        if (!notificationTaskService.getNotificationTasksByDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)).isEmpty()) {
            notificationTaskService
                    .getNotificationTasksByDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                    .stream()
                    .forEach(notificationTask -> {
                        SendMessage message = new SendMessage(notificationTask.getIdChat(), notificationTask.getText());
                        SendResponse response = telegramBot.execute(message);
                    });
        }
    }
}
