package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class NotificationTaskService {
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }


    public NotificationTask createNotificationTask(NotificationTask notificationTask) {
        if (notificationTask ==null)
            throw new IllegalArgumentException("NotificationTask is null");
        return notificationTaskRepository.save(notificationTask);
    }

    public Collection<NotificationTask> getNotificationTasksByDate(LocalDateTime localDateTime) {
        return notificationTaskRepository.findNotificationTasksByNotificationDate(localDateTime);
    }
}
