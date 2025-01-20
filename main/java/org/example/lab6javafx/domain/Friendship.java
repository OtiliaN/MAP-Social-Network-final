package org.example.lab6javafx.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Friendship extends Entity<Long> {
    private LocalDateTime date;
    private final Utilizator user1;
    private final Utilizator user2;
    private FriendshipStatus status;


    public Friendship(Utilizator user1, Utilizator user2, LocalDateTime date, FriendshipStatus status) {
        this.user1 = user1;
        this.user2 = user2;
        this.date = date;
        this.status = status;
    }


    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Utilizator getUser1() {
        return user1;
    }

    public Utilizator getUser2() {
        return user2;
    }


    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    /**
     * getter for the date
     * @return the date when the friendship was made
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "User='" + user1.getFirstName() + '\'' + ", lastName='" + user1.getLastName() + '\'' +
                " with User='" + user2.getFirstName() + '\'' + ", lastName='" + user2.getLastName() + '\'' +
                " Since: = " + formatter(date) + '}';
    }

    private String formatter(LocalDateTime date) {
        String timetoprint;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timetoprint = date.format(formatter);
        return timetoprint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2);
    }

}
