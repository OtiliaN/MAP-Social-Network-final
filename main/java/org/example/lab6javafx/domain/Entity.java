package org.example.lab6javafx.domain;

import java.io.Serializable;
import java.util.Objects;

/*
BASE CLASS FOR OUR OBJECTS
    It holds an id.
 */

/**
 * Entity base class
 * @param <ID> The type of id for the entity
 */
public class Entity<ID>  implements Serializable{

    private ID id;

    /**
     * @return ID of entity
     */
    public ID getId() {
        return id;
    }

    /**
     * sets id
     * @param id Sets this as new id
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * @param o See if object is equal with o
     * @return true if the objects are equal, else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    /**
     * Hashcode of objects
     * @return hashcoded id
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Printing objects
     * @return id of the object as a string
     */
    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                "}";
    }
}