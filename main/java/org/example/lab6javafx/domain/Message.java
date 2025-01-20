package org.example.lab6javafx.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message extends Entity<Long>{
    private Utilizator from;
    private Utilizator to;
    private String message;
    private LocalDateTime date;
    private Long replyingTo;

    public Message(Utilizator from, Utilizator to, String message, LocalDateTime date, Long replyingTo) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.replyingTo = replyingTo;
    }

    public Utilizator getTo() {
        return to;
    }

    public void setTo(Utilizator to) {
        this.to = to;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getReplyingTo() {
        return replyingTo;
    }

    public void setReplyingTo(Long replyingTo) {
        this.replyingTo = replyingTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(getFrom(), message1.getFrom()) && Objects.equals(getTo(), message1.getTo()) && Objects.equals(getMessage(), message1.getMessage()) && Objects.equals(getDate(), message1.getDate()) && Objects.equals(getReplyingTo(), message1.getReplyingTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), getMessage(), getDate(), getReplyingTo());
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", replyingTo=" + replyingTo +
                '}';
    }
}
