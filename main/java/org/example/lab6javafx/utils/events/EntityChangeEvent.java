package org.example.lab6javafx.utils.events;

import org.example.lab6javafx.domain.Entity;

public class EntityChangeEvent<E extends Entity> implements Event {
    private ChangeEventType type;
    private E data, oldData;

    public EntityChangeEvent(ChangeEventType type, E data) {
        this.type = type;
        this.data = data;
    }
    public EntityChangeEvent(ChangeEventType type, E data, E oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public E getData() {
        return data;
    }

    public E getOldData() {
        return oldData;
    }
}
