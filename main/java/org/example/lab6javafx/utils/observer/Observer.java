package org.example.lab6javafx.utils.observer;


import org.example.lab6javafx.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);

}