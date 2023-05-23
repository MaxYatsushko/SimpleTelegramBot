package pro.sky.telegrambot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask{

    @Id
    @GeneratedValue
    @Column(name = "id_task")
    private Long idTask;
    @Column(name = "notification_date")
    private LocalDateTime notificationDate;
    @Column(name = "text_message")
    private String text;
    @Column(name = "id_chat")
    private Long idChat;

    public Long getIdTask() {
        return idTask;
    }

    public void setIdTask(Long idTask) {
        this.idTask = idTask;
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(idTask, that.idTask) && Objects.equals(notificationDate, that.notificationDate) && Objects.equals(text, that.text) && Objects.equals(idChat, that.idChat);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "idTask=" + idTask +
                ", notificationDate=" + notificationDate +
                ", text='" + text + '\'' +
                ", idChat=" + idChat +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTask, notificationDate, text, idChat);
    }
}
