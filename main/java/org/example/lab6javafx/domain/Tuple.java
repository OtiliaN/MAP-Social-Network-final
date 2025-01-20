package org.example.lab6javafx.domain;

import java.util.Objects;

public class Tuple<E1, E2> {
    private E1 e1;
    private E2 e2;

    /**
     * creates tuple of two entities
     * @param e1 first entity of tuple
     * @param e2 second entity of tuple
     */
    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * @return first entity
     */
    public E1 getLeft() {
        return e1;
    }

    /**
     * sets first entity
     * @param newE1
     */
    public void setLeft(E1 newE1) {
        this.e1 = e1;
    }

    /**
     * @return second entity
     */
    public E2 getRight() {
        return e2;
    }

    /**
     * sets second entity
     * @param newE2
     */
    public void setRight(E2 newE2) {
        this.e2 = e2;
    }

    /**
     * to string for tuple
     * @return
     */
    @Override
    public String toString() {
        return "" + e1 + "," + e2;
    }

    /**
     * equality of tuples
     * @param obj to check if the object is equal
     * @return true if the objects are equal, false else
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Tuple<E1, E2> newObj = (Tuple) obj;
        return e1.equals(newObj.getLeft()) && e2.equals(newObj.getRight());
    }

    /**
     * hashcode of tuple
     * @return hashcode of objects
     */
    @Override
    public int hashCode() {
        return Objects.hash(e1,e2);
    }
}
